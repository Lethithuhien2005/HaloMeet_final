package main.Server;

//import common.meeting.MeetingService;
import main.Server.Controller.*;

import main.Server.DAO.MongoChatRepository;
import main.Server.DAO.UserDAO;
import main.Server.Model.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import shared.ChatService;
import shared.MeetingService;
//import shared.MeetingService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        int tcpPort = 5555;
        int rmiPort = 2005;

        try {
            // ===== 1. START RMI =====
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            MeetingService meetingService = new MeetingServiceImplement();
            ChatService chatService = new ChatServiceImplement();

            registry.rebind("MeetingService", meetingService);
            registry.rebind("ChatService", chatService);

            System.out.println("RMI Services running on port " + rmiPort);
            System.out.println("   - MeetingService");
            System.out.println("   - ChatService");

            // ===== 2. START TCP SERVER =====
            MongoChatRepository repo = new MongoChatRepository();
            CentralHandler tcpServer = new CentralHandler(tcpPort, repo);
            tcpServer.start(); // chặn tại đây
//            new Thread(() -> {
//                try {
//                    tcpServer.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();

            System.out.println("TCP Server running on port " + tcpPort);

            // ===============================
            // các RMI, MongoDB, TCP server khác
//            AvatarTCPServer avatarServer = new AvatarTCPServer();
//            avatarServer.start();
//
//            System.out.println("All servers started.");



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
