//package shared;
//
////import common.meeting.ChatMeeting;
////import common.meeting.MeetingService;
//
//import shared.DTO.ChatMeeting;
//
//import java.rmi.Remote;
//import java.rmi.RemoteException;
//
//public interface ChatService extends Remote {
//    interface ClientCallback extends Remote {
//        void onNewMessage(ChatMeeting message) throws RemoteException;
//        void onUserJoined(String userName) throws RemoteException;
//        void onUserLeft(String userName) throws RemoteException;
//    }
//
//    void joinMeeting(
//            String roomId,
//            String userName,
//            ClientCallback callback
//    ) throws RemoteException;
//
//    void leaveMeeting(
//            String roomId,
//            String userName,
//            ClientCallback callback
//    ) throws RemoteException;
//
//    void sendMessage(
//            String roomId,
//            ChatMeeting message
//    ) throws RemoteException;
//
//}


package shared;

import shared.DTO.ChatMeeting;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {

    void joinRoom(
            String roomId,
            String userId,
            ChatClientCallback callback
    ) throws RemoteException;

    void leaveRoom(
            String roomId,
            String userId
    ) throws RemoteException;

    void sendMessage(
            String roomId,
            ChatMeeting message
    ) throws RemoteException;
}
