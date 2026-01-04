package main.Client.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.Client.ClientMain;
import shared.MeetingService;

public class Dashboard extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        MeetingService meetingService = ClientMain.meetingService; // lấy service đã khởi tạo
        SidebarController sidebarController = new SidebarController(meetingService);

        BorderPane root = new BorderPane();
        root.setLeft(sidebarController.getSidebar());
        root.setCenter(sidebarController.getContentPane());

        Scene scene = new Scene(root);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("HaloMeet");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
