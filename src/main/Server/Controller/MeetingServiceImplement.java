package main.Server.Controller;

import main.Server.DAO.MeetingDAO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import common.meeting.MeetingService;
//import common.meeting.ChatMeeting;


import shared.ChatService;
import shared.DTO.ChatMeeting;
import shared.DTO.Meeting_participantDTO;
import shared.DTO.RoomDTO;
import shared.MeetingClientCallback;
import main.Server.DAO.MeetingDAO;
import main.Server.DAO.UserDAO;
import org.bson.Document;
import org.bson.types.ObjectId;
import shared.MeetingService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MeetingServiceImplement extends UnicastRemoteObject implements MeetingService {

    private MeetingDAO meetingDAO;
    private UserDAO userDAO;
    // Luu callback cua tat ca client theo phong (roonID)
    private Map<String, List<MeetingClientCallback>> roomCallbacks = new HashMap<>();

    public MeetingServiceImplement() throws RemoteException {
        super();
        meetingDAO = new MeetingDAO();
        userDAO = new UserDAO();
    }

    @Override
    public void createMeeting(String hostId, String title, String passcode, MeetingClientCallback callback) throws RemoteException {
        if (hostId == null) {
            callback.onCreateMeetingFail("User not loggin.");
            return;
        }
        if (title == null) {
            callback.onCreateMeetingFail("Meeting title is required");
            return;
        }

        ObjectId hostObjectId = new ObjectId(hostId);
        // Tao ma phong hop random
        String meetingCode = String.valueOf(100000 + new SecureRandom().nextInt(900000));
        ObjectId conversationId = meetingDAO.createMeeting(hostObjectId, title, meetingCode, passcode);

        // Lay phong hop thong qua conversationId
        Document roomDoc = meetingDAO.getRoomByConversationId(conversationId);
        ObjectId roomId = roomDoc.getObjectId("_id");
        // Them host vao meeting_participants
        meetingDAO.addParticipant(roomId, hostObjectId, "host");

        // Tra ket qua cho client
        // CONVERT ObjectId → String
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setTitle(roomDoc.getString("title"));
        roomDTO.setMeeting_code(roomDoc.getString("meeting_code"));
        roomDTO.setPasscode(roomDoc.getString("passcode"));
        roomDTO.setStatus(roomDoc.getString("status"));
        roomDTO.setConservationId(conversationId.toHexString());
        Date createdAt = roomDoc.getDate("created_at");
        roomDTO.setCreated_at(createdAt.getTime());

        callback.onCreateMeetingSuccess(roomDTO.getMeeting_code(), roomDTO.getPasscode(), roomDTO.getTitle(), roomDTO.getCreated_at());
    }

    @Override
    public void joinMeeting(String userId, String meetCode, String passcode, MeetingClientCallback callback) throws RemoteException {
        // Tim phong hop thong qua meetingCode
        Document room = meetingDAO.getRoomByMeetingCode(meetCode);
        if (room == null) {
            callback.onJoinMeetingFail("Meeting room not found");
            return;
        }

        // Kiem tra passcode
        String roomPasscode = room.getString("passcode");
        // Neu phong co passcode
        if (roomPasscode != null && !roomPasscode.isEmpty()) {
            if (!roomPasscode.equals(passcode)) { // client nhập không đúng
                callback.onJoinMeetingFail("Invalid passcode");
                return;
            }
        }

        // Kiểm tra trạng thái phòng
        if (!"active".equals(room.getString("status"))) {
            callback.onJoinMeetingFail("Meeting is not active");
            return;
        }

        ObjectId roomID = room.getObjectId("_id");
        ObjectId userID = new ObjectId(userId);
        // LẤY conversation_id TỪ ROOM
        ObjectId conversationId = room.getObjectId("conversation_id");

        // Neu chua la member trong phong hop thi them vao
        String role = "member";
        ObjectId hostId = room.getObjectId("created_by");
        if (hostId != null && hostId.equals(userID)) {
            role = "host";
        }
        if (meetingDAO.hasEverJoined(roomID, userID)) {
            meetingDAO.rejoin(roomID, userID);
        } else {
            meetingDAO.addParticipant(roomID, userID, role);
        }

        // Luu callback vao roomCallbacks (danh sach callback) de sau nay server thong bao cho cac client trong cung 1 phong
        // Tao 1 danh sach rong chua callback cua cac client neu phong chua co trong roomCallbacks, neu phong da ton tai trong roomCallbacks thi giu nguyen, khong khi de
        roomCallbacks.putIfAbsent(roomID.toHexString(), new ArrayList<>()); // Tao danh sach chua cac callback cua cac client trong phong
        List<MeetingClientCallback> callbacks = roomCallbacks.get(roomID.toHexString());
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }

        // Lấy danh sách participant dựa trên roomId
        List<Document> participants = meetingDAO.getActiveParticipants(roomID);
        List<Meeting_participantDTO> participantList = new ArrayList<>();
        for (Document p : participants) {
            ObjectId user_id = p.getObjectId("user_id");
            Document userDoc = userDAO.getUserById(user_id);
            Meeting_participantDTO dto = new Meeting_participantDTO(
                    user_id.toHexString(),
                    userDoc != null ? userDoc.getString("username") : "Unknown",
                    userDoc != null ? userDoc.getString("fullName") : "Unknown",
                    userDoc != null ? userDoc.getString("avatar") : "default_avatar",
                    p.getString("role"),
                    Boolean.TRUE.equals(p.getBoolean("is_muted")),
                    Boolean.TRUE.equals(p.getBoolean("is_camera_on"))
            );
            participantList.add(dto);
        }

        // ===== LOAD CHAT HISTORY =====
        List<Document> messageDocs =
                meetingDAO.getMessagesByConversationId(conversationId);

        List<ChatMeeting> chatHistory = new ArrayList<>();

        for (Document d : messageDocs) {
            chatHistory.add(new ChatMeeting(
                    roomID.toHexString(),                 // roomId cho UI context
                    d.getString("sender_id"),
                    d.getString("content")
            ));
        }

        callback.onJoinMeetingSuccess(
                roomID.toHexString(),
                participantList,
                chatHistory
        );

        // Thong bao cho tat ca cac client trong phong
        notifyUpdatingParticipants(roomID.toHexString());
    }

    // Cap nhat giao dien danh sach nguoi tham gia cuoc hop cho tat ca cac client khi co 1 client join, leave, mute,...
    private void notifyUpdatingParticipants(String roomId) throws RemoteException {
        // Server giu danh sach callback cua moi client trong phong
        List<MeetingClientCallback> callbacks = roomCallbacks.get(roomId);

        // Lay danh sach cac client dang co trong cuoc hop dua tren roomId
        List<Document> participants = meetingDAO.getActiveParticipants(new ObjectId(roomId));
        List<Meeting_participantDTO> participantList = new ArrayList<>();
        for (Document p : participants) {
            ObjectId user_id = p.getObjectId("user_id");
            Document userDoc = userDAO.getUserById(user_id);
            Meeting_participantDTO dto = new Meeting_participantDTO(
                    user_id.toHexString(),
                    userDoc != null ? userDoc.getString("username") : "Unknown",
                    userDoc != null ? userDoc.getString("fullName") : "Unknown",
                    userDoc != null ? userDoc.getString("avatar") : "default_avatar",
                    p.getString("role"),
                    Boolean.TRUE.equals(p.getBoolean("is_muted")),
                    Boolean.TRUE.equals(p.getBoolean("is_camera_on"))
            );
            participantList.add(dto);
        }
        // Gọi callback cho tat ca client de cap nhat giao dien
        for (MeetingClientCallback c : callbacks) {
            c.onParticipantListUpdated(participantList);
        }
    }

    @Override
    public synchronized void leaveMeeting(String userId, String roomId, MeetingClientCallback callback) throws RemoteException {
        ObjectId roomID = new ObjectId(roomId);
        ObjectId userID = new ObjectId(userId);

        // Set trang thai left cho client
        meetingDAO.updateStatusParticipant(roomID, userID);

        // Xoa callback cua client trong danh sach
        List<MeetingClientCallback> callbacks = roomCallbacks.get(roomID.toHexString());
        if (callbacks != null) {
            callbacks.remove(callback);
        }

        // Thong bao cho cac client cap nhat giao dien
        notifyUpdatingParticipants(roomID.toHexString());
    }

    // Load cac cuoc hop hom nay moi lan vao Home
    @Override
    public List<RoomDTO> getMeetingsToday(String userId) throws RemoteException {
        ObjectId userID = new ObjectId(userId);

        List<Document> rooms = meetingDAO.getMeetingTodayByUser(userID);
        List<RoomDTO> meetingsToday = new ArrayList<>();

        for (Document r : rooms) {
            RoomDTO dto = new RoomDTO();
            dto.setMeeting_code(r.getString("meeting_code"));
            dto.setTitle(r.getString("title"));
            dto.setPasscode(r.getString("passcode"));

            Date createdAt = r.getDate("created_at");
            dto.setCreated_at(createdAt.getTime());

            meetingsToday.add(dto);
        }
        return meetingsToday;
    }
    @Override
    public List<RoomDTO> getRecentMeetings(String userIdHex) throws RemoteException {

        ObjectId userId = new ObjectId(userIdHex);
        List<Document> docs = meetingDAO.getMeetingsLast7Days(userId);

        List<RoomDTO> result = new ArrayList<>();

        for (Document d : docs) {
            Document room = d.get("room", Document.class);

            Document hostDoc =
                    userDAO.getUserById(room.getObjectId("created_by"));

            RoomDTO dto = new RoomDTO();
            dto.setRoomId(room.getObjectId("_id").toHexString());
            dto.setTitle(room.getString("title"));
            dto.setMeeting_code(room.getString("meeting_code"));
            dto.setPasscode(room.getString("passcode"));
            dto.setCreated_at(room.getDate("created_at").getTime());
            dto.setCreated_by(room.getObjectId("created_by").toHexString());

            dto.setHostFullName(
                    hostDoc != null
                            ? hostDoc.getString("fullName")
                            : "Unknown"
            );

            result.add(dto);
        }
        return result;
    }


    @Override
    public void setMic(String roomId, String currentUser, String targetUser) throws RemoteException {
         ObjectId roomID = new ObjectId(roomId);
         ObjectId currentUserID = new ObjectId(currentUser);
         ObjectId targetUserID = new ObjectId(targetUser);

         // Neu nguoi dung hien tai muon dieu khien mic ca nhan
        if (currentUserID.equals(targetUserID)) {
            meetingDAO.updateMicStatus(roomID, targetUserID);
            // Cap nhat giao dien cho tat cac client trong phong hop
            notifyUpdatingParticipants(roomId);
            return;
        }

        // Neu nguoi dung hien tai muon dieu khien mic nguoi khac => Check quyen
        if (!meetingDAO.canControl(roomID, currentUserID)) {
            return;
        }
        meetingDAO.updateMicStatus(roomID, targetUserID);
        notifyUpdatingParticipants(roomId);
    }

    @Override
    public void setCam(String roomId, String currentUser, String targetUser) throws RemoteException {
            ObjectId roomID = new ObjectId(roomId);
            ObjectId currentUserID = new ObjectId(currentUser);
            ObjectId targetUserID = new ObjectId(targetUser);

        // Neu nguoi dung muon dieu khien cam ca nhan
        if (currentUserID.equals(targetUserID)) {
            meetingDAO.updateCameraStatus(roomID, targetUserID);
            notifyUpdatingParticipants(roomId); // cap nhat giao dien cho cac client
            return;
        }

        // Neu nguoi dung muon dieu khien camera cua nguoi khac
        if (!meetingDAO.canControl(roomID, currentUserID)) {
            return;
        }
        meetingDAO.updateCameraStatus(roomID, targetUserID);
        notifyUpdatingParticipants(roomId);
    }

    @Override
    public void kickUser(String roomId, String currentUser, String targetUser) throws RemoteException {
        ObjectId roomID = new ObjectId(roomId);
//        ObjectId currentUserID = new ObjectId(currentUser);
        ObjectId targetUserID = new ObjectId(targetUser);

//        // Check quyen
//        if (!meetingDAO.canControl(roomID, currentUserID)) {
//            return;
//        }
        meetingDAO.updateStatusParticipant(roomID, targetUserID);
        notifyUpdatingParticipants(roomId);

        // Thông báo riêng cho user bị kick
        List<MeetingClientCallback> callbacks = roomCallbacks.get(roomId);
        for (MeetingClientCallback c : callbacks) {
            // Kiểm tra nếu callback của user bị kick
            if (c.getUserId().equals(targetUser)) {
                c.onKickedFromMeeting(roomId, "You have been kicked by the host");
            }
        }
    }

}