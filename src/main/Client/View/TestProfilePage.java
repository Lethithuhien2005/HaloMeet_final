package main.Client.View;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TestProfilePage extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Tạo ảnh demo (nếu có)
        Image demoAvatar = new Image("./images/chatPage/profile.png");
        String demoName = "Nguyen Anh";

        // Tạo ProfilePage và chạy
//        ProfilePage profilePage = new ProfilePage(primaryStage, demoName, demoAvatar);
//        profilePage.start(primaryStage);
//        PersonalProfile personalProfilePage = new PersonalProfile(primaryStage, demoName, demoAvatar);
//        personalProfilePage.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
