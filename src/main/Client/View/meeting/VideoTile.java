package main.Client.View.meeting;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.util.ImageUtil;

import java.net.URL;

public class VideoTile extends StackPane {
    private ImageView videoView;
    private Label accountName;
    private ImageView avatarView;
    private boolean cameraOn;
    private Image fakeWebcamImage;
    private String userId;


    public VideoTile(String userId, String username) {
        this.userId = userId;
        accountName = new Label(username);

        videoView = new ImageView();
        videoView.setPreserveRatio(true);
        videoView.setSmooth(true);
        // QUAN TRỌNG: video sẽ co giãn theo tile
        videoView.fitWidthProperty().bind(this.widthProperty());
        videoView.fitHeightProperty().bind(this.heightProperty());

        fakeWebcamImage = new Image(
                getClass().getResource("/images/video_call.jpg").toExternalForm()
        );
        // mặc định camera ON
        videoView.setImage(fakeWebcamImage);
        cameraOn = true;

        // Neu khong bat camera
        // Avatar
        avatarView = new ImageView();
        avatarView.setFitWidth(80);
        avatarView.setFitHeight(80);
        avatarView.setPreserveRatio(true);

        Circle clip = new Circle(40, 40, 40);
        avatarView.setClip(clip);

        StackPane.setAlignment(avatarView, Pos.CENTER);
        avatarView.setVisible(false); // mặc định camera ON

        accountName = new Label(username);
        accountName.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        accountName.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-padding: 5px;");

        StackPane.setAlignment(accountName, Pos.BOTTOM_CENTER);
        this.setMinSize(100, 70);     // an toàn
        this.setPrefSize(300, 200);   // gợi ý
//        this.setMaxSize(800, 600);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.getChildren().addAll(videoView,avatarView, accountName);
        this.setStyle("-fx-background-color: #000; -fx-border-color: #444; -fx-border-width: 1; -fx-background-radius: 15; -fx-border-radius: 15;");
    }

    // bật / tắt camera
    public void setCameraOn(boolean on) {
        this.cameraOn = on;
        if (on) {
            videoView.setImage(fakeWebcamImage);
            videoView.setVisible(true);
            avatarView.setVisible(false);
        } else {
            videoView.setVisible(false);
            avatarView.setVisible(true);
        }
    }

    // nhận avatar từ backend (String)
    public void setAvatar(String avatarName) {
        avatarView.setImage(ImageUtil.loadAvatarSafe(avatarName));
    }

    public String getUserId() {
        return userId;
    }

}

