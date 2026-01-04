package main.Server.Controller;

//import common.meeting.ChatMeeting;
import main.Server.DAO.UserDAO;
import org.bson.types.ObjectId;
import shared.ChatClientCallback;
import shared.ChatService;
import shared.DTO.ChatMeeting;
import shared.MeetingService;
import main.Server.DAO.MeetingDAO;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServiceImplement extends UnicastRemoteObject implements ChatService {

    private MeetingDAO meetingDAO;
//    private final Map<String, List<ClientCallback>> meetingClients = new ConcurrentHashMap<>();
    private final Map<String, List<ChatClientCallback>> roomClients = new ConcurrentHashMap<>();

    public ChatServiceImplement() throws RemoteException {
        super();
        meetingDAO = new MeetingDAO();
    }

//    @Override
//    public void joinRoom(String roomId, String userId, ChatClientCallback callback) throws RemoteException {
//
//    }
    @Override
    public synchronized void joinRoom(String roomId, String userId, ChatClientCallback cb) throws RemoteException {
        roomClients.computeIfAbsent(roomId, k -> new ArrayList<>()).add(cb);
    }


    @Override
    public void leaveRoom(String roomId, String userId) throws RemoteException {

    }

    @Override
    public synchronized void sendMessage(String room_id, ChatMeeting message) throws RemoteException {

        // 1️⃣ convert id
        //ObjectId conversationId = new ObjectId("507f1f77bcf86cd799439011"); // test
        //ObjectId conversationId = new ObjectId(); // auto-generate // tạm thời
//        ObjectId senderId = new ObjectId(message.getSender());
//        ObjectId senderId = new ObjectId();

        ObjectId roomObjectId = new ObjectId(room_id);
        ObjectId conversationId =
                meetingDAO.getConversationIdByRoomId(roomObjectId);
        if (conversationId == null) {
            System.err.println("❌ Conversation not found for room " + room_id);
            return;
        }

        String senderId = message.getSender(); // userIdHex từ Session

        // 2️⃣ LƯU DB QUA DAO
        meetingDAO.saveMessage(
                conversationId,
                senderId,
                message.getContent()
        );

        // 3️⃣ PUSH REALTIME
        List<ChatClientCallback> clients = roomClients.get(room_id);
        if (clients == null) return;

        // SERVER PUSH MESSAGE VỀ CLIENT
//        for (ClientCallback cb : clients) {
//            cb.onNewMessage(message);
//        }
        Iterator<ChatClientCallback> it = clients.iterator();

        while (it.hasNext()) {
            ChatClientCallback cb = it.next();
            try {
                cb.onNewMessage(message);
            } catch (RemoteException e) {
                System.out.println("⚠️ Remove dead callback");
                it.remove(); // client đã disconnect
            }
        }


        System.out.println(
                "[SERVER] Message saved to DB | sender=" + senderId +
                        " | content=" + message.getContent()
        );

    }
}