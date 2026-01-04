package main.Server.Controller; // NHẬN REQUEST TỪ CLIENT + XỬ LÝ NGHIỆP VỤ

import main.Server.DAO.MeetingDAO;
import main.Server.DAO.UserDAO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeetingHandler {
    private MeetingDAO meetingDAO;
    private UserDAO userDAO;

    public MeetingHandler() {
        meetingDAO = new MeetingDAO();
        userDAO = new UserDAO();
    }
    // Xu ly yeu cau CREATE MEETING
    public Document handleCreateMeeting(Document request) {
        String type = request.getString("type");
        if ("CREATE_MEETING".equals(type)) {
            String title = request.getString("title");
            String passcode = request.getString("passcode");
            String userID = request.getString("userID");
            if (userID == null) {
                return new Document("type", "CREATE_MEETING_FAIL")
                        .append("message", "User not authenticated");
            }
            if (title == null || passcode == null) {
                return new Document("type", "CREATE_MEETING_FAIL")
                        .append("message", "MeetingHandler cannot handle request: "+ type + " because of missing required fields");
            }

            // Tao ma phong hop random
            ObjectId hostId = new ObjectId(userID);
            String meeting_code = String.valueOf(100000 + new java.security.SecureRandom().nextInt(900000));
            ObjectId conversationId = meetingDAO.createMeeting(hostId, title, meeting_code, passcode);

            // Lay thoi gian tao phong hop
            Document roomDoc = meetingDAO.getRoomByConversationId(conversationId);
            Date timeCreateRoom = roomDoc.getDate("created_at");

            // Tra response cho client
            return new Document("type", "CREATE_MEETING_OK")
                    .append("conversationId", conversationId.toHexString())
                    .append("meeting_code", meeting_code)
                    .append("title", title)
                    .append("time_create_meeting", timeCreateRoom.getTime());
        }
        else {
            return new Document("type", "CREATE_MEETING_FAIL").append("message", "MeetingHandler cannot handle request: " + type);
        }
    }

    // Xu ly yeu cau JOIN MEETING
    public Document handleJoinMeeting(Document request) {
        String type = request.getString("type");
        if ("JOIN_MEETING".equals(type)) {
            String meetingId = request.getString("conservationId");
            String meeting_code = request.getString("meeting_code");
            String userIdStr = request.getString("userId");
            if (userIdStr == null) {
                return new Document("type", "JOIN_MEETING_FAIL")
                        .append("message", "User not authenticated");
            }
            if (meetingId == null) {
                return new Document("type", "JOIN_MEETING_FAIL")
                        .append("message", "MeetingHandler cannot handle request: "+ type + " because of missing required fields");
            }

            ObjectId conversationId = new ObjectId(meetingId);
            ObjectId userID = new ObjectId(userIdStr);

            // Tim phong hop
            Document room = meetingDAO.getRoomByConversationId(conversationId);
            if (room == null) {
                return new Document("type", "JOIN_MEETING_FAIL")
                        .append("message", "Meeting room not found");
            }

            // Kiem tra passcode cua phong hop (null hoac co passcode)
            if (!meeting_code.equals(room.getString("meeting_code"))) {
                return new Document("type", "JOIN_MEETING_FAIL")
                        .append("message", "Invalid meeting code");
            }

            // Kiem tra trang thai phong
            if (!"active".equals(room.getString("status"))) {
                return new Document("type", "JOIN_MEETING_FAIL")
                        .append("message", "Meeting is not active");
            }

            // Neu chua la member trong phong hop thi them vao
            String role = "member";
            ObjectId hostId = room.getObjectId("created_by");
            if (hostId != null && hostId.equals(userID)) {
                role = "host";
            }

            // Neu chua join lan nao thi them hoac da tung join sau do leave thi rejoin
            if (meetingDAO.hasEverJoined(conversationId, userID)) {
                meetingDAO.rejoin(conversationId, userID);
            } else {
                meetingDAO.addParticipant(conversationId, userID, role);
            }

            // Lay danh sach nguoi dang tham gia cuoc hop de gui ve client
            List<Document> participants = meetingDAO.getActiveParticipants(conversationId);
            List<Document> participantList = new ArrayList<>();
            for (Document p : participants) {
                ObjectId userId = p.getObjectId("user_id");

                // Lay thong tin user tu users collection
                Document userDoc = userDAO.getUserById(userId);
                String username = userDoc != null ? userDoc.getString("username") : "Unknown";
                String fullname = userDoc != null ? userDoc.getString("fullName") : "Unknown";
                String avatar = userDoc != null ? userDoc.getString("avatar") : "default_avatar";

                participantList.add(
                        new Document("user_id", userId.toHexString())
                                .append("username", username)
                                .append("fullName", fullname)
                                .append("avatar", avatar)
                                .append("role", p.getString("role"))
                                .append("is_muted", p.getBoolean("is_muted"))
                                .append("is_camera_on", p.getBoolean("is_camera_on"))
                );

            }
            return new Document("type", "JOIN_MEETING_OK")
                    .append("conversationId", conversationId.toHexString())
                    .append("meeting_code", meeting_code)
                    .append("title", room.getString("title"))
                    .append("participantList", participantList);
        }
        else {
            return new Document("type", "JOIN_MEETING_FAIL").append("message", "MeetingHandler cannot handle request: " + type);
        }
    }

}
