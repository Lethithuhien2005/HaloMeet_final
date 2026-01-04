package main.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class DialogUtil {
    // Hiển thị dialog thông báo lỗi
    public static void showError(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
