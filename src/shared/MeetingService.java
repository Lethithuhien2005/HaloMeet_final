package shared;

//import common.meeting.ChatMeeting;

import java.rmi.Remote;
import java.rmi.RemoteException;


import shared.DTO.RoomDTO;
import shared.MeetingClientCallback;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MeetingService extends Remote {
        public void createMeeting(String hostId, String title, String passcode, MeetingClientCallback callback) throws RemoteException;
        public void joinMeeting(String userId, String meetCode, String passcode, MeetingClientCallback callback) throws RemoteException;
        public void leaveMeeting(String userId, String roomId, MeetingClientCallback callback) throws RemoteException;
        public List<RoomDTO> getMeetingsToday(String userId) throws RemoteException;
        public void setMic(String roomId, String currentUser, String targetUser) throws RemoteException;
        public void setCam(String roomId, String currentUser, String targerUser) throws RemoteException;
        public void kickUser(String roomId, String currentUser, String targetUser) throws RemoteException;

        public List<RoomDTO> getRecentMeetings(String userIdHex) throws RemoteException;
}