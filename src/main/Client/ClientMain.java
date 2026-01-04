package main.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.Client.Network.TCP.SocketClient;
import main.Client.View.LogIn;
import main.util.DialogUtil;
import main.util.IPUtil;
import shared.ChatService;
import shared.MeetingService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain extends Application {

    // GIỮ RMI SERVICE TOÀN CLIENT
    public static MeetingService meetingService;
    public static ChatService chatService;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Connect TCP ngay khi app start (chạy trong thread riêng)
        connectTCP();

        // Connect RMI và mở Login chỉ khi RMI sẵn sàng
        connectRMI(() -> {
            try {
                LogIn login = new LogIn();
                login.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void connectTCP() {
        new Thread(() -> {
            try {
                SocketClient.getInstance().connect("localhost", 5555);
                System.out.println("TCP Connected!");
            } catch (Exception ex) {
                Platform.runLater(() ->
                        DialogUtil.showError(
                                "TCP Error",
                                "Cannot connect TCP server",
                                ex.getMessage()
                        )
                );
            }
        }).start();
    }

    private void connectRMI(Runnable onSuccess) {
        new Thread(() -> {
            try {
                // lookup service
                Registry registry = LocateRegistry.getRegistry("localhost", 2005);
                meetingService = (MeetingService) registry.lookup("MeetingService");
                chatService    = (ChatService) registry.lookup("ChatService");

                // Cho RMI ket noi xong truoc khi mo Home/Controller
                System.out.println("RMI Connected!");
                if (onSuccess != null) {
                    Platform.runLater(onSuccess);
                }

                System.out.println("RMI Connected!");
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> DialogUtil.showError(
                        "RMI Connect error",
                        "Couldn't connect to RMI server",
                        "RMI connection failed"
                ));
            }
        }).start();
    }

    public static void main(String[] args) {
        // BẮT BUỘC cho RMI callback
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

//        String localIP = IPUtil.getLocalIP();
//        System.out.println("Local IP: " + localIP);
//        System.setProperty("java.rmi.server.hostname", localIP);
        launch(args);
    }
}
