package main.Client.Controller;

import javafx.application.Platform;
import main.Client.ClientMain;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import main.util.Session;
import shared.ChatClientCallback;
import shared.ChatService;
import shared.DTO.ChatMeeting;
import shared.MeetingService;
//
//import common.meeting.MeetingService;
//import common.meeting.ChatMeeting;


public class MeetingChatController {

    private ChatService chatService;
    private String room_id ;
    private String userName;

//    private MeetingService.ClientCallback callback;
    private ChatClientCallback callback;


    public interface UiListener {
        void onMessageReceived(ChatMeeting msg);
        void onSystemMessage(String text);
    }

    private UiListener uiListener;

    public MeetingChatController(String room_id, String userName) {
        this.room_id = room_id;
        this.userName = userName;
    }

    public void setUiListener(UiListener listener) {
        this.uiListener = listener;
    }

    /* ===== CONNECT RMI ===== */
    public void connect() throws Exception {
        chatService = ClientMain.chatService;

//        callback = new ClientCallbackImpl();
//        meetingService.joinMeeting(room_id, userName, callback);

        if (chatService == null) {
            throw new IllegalStateException("RMI MeetingService not connected");
        }

        // callback client
//        MeetingService.ClientCallback callback =
//                new ClientCallbackImpl();
        callback = new ClientCallbackImpl();
        chatService.joinRoom(room_id, userName, callback);

        System.out.println("[CLIENT] Joined chat room: " + room_id);
    }

    /* ===== SEND MESSAGE ===== */
    public void sendMessage(String text) throws RemoteException {
        if (text == null || text.trim().isEmpty()) return;

        String senderId = Session.getInstance().getUserIdHex();

        ChatMeeting  msg =
                new ChatMeeting (
                        room_id,
                        senderId,
                        text
                );

        chatService.sendMessage(room_id, msg);

        System.out.println(
                "[CLIENT] Message sent successfully by userId=" + senderId
                        + ", content=" + text
        );
    }

    /* ===== CALLBACK IMPLEMENT ===== */
    private class ClientCallbackImpl
            extends UnicastRemoteObject
            implements ChatClientCallback {

        protected ClientCallbackImpl() throws RemoteException {
            super();
        }

        @Override
        public void onNewMessage(ChatMeeting message) {
            Platform.runLater(() -> {
                if (uiListener != null) {
                    uiListener.onMessageReceived(message);
                }
            });
        }
    }
}
