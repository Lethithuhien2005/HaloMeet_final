package shared;

import shared.DTO.ChatMeeting;

import java.rmi.Remote;
import java.rmi.RemoteException;

//
//public class ChatClientCallback {
//}
public interface ChatClientCallback extends Remote {
    void onNewMessage(ChatMeeting message) throws RemoteException;
//    void onUserJoined(String userId) throws RemoteException;
//    void onUserLeft(String userId) throws RemoteException;
}
