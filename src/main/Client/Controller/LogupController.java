package main.Client.Controller;

import main.Client.View.LogUp;
import javafx.application.Platform;
import main.Client.Network.TCP.SocketClient;
import main.util.PasswordUtils;
import org.bson.Document;

public class LogupController {
    private LogUp logUpView;
    private static final String SALT = "hienanh"; // Salt cố định dùng cho cả register và login

    public LogupController (LogUp logUpView) {
        this.logUpView = logUpView;
    }

    public void onClickLogup() {
        String username = logUpView.getName();
        String fullname = logUpView.getFullName();
        String email = logUpView.getEmail();
        String password = logUpView.getPassword();
        // Ma hoa password truoc khi
        String hashedPassword = PasswordUtils.hashPassword(password + SALT);

        if (username.isEmpty() || fullname.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {
            Platform.runLater(() ->
                    logUpView.showError("Please fill in all fields.")
            );
            return;
        }
        if (!email.contains("@")) {
            Platform.runLater(() ->
                    logUpView.showError("Invalid email format")
            );
            return;
        }
        if (password.length() < 6) {
            Platform.runLater(() ->
                    logUpView.showError("Password must be at least 6 characters")
            );
            return;
        }

        new Thread(() -> {
            try {
                SocketClient socketClient = SocketClient.getInstance();

                Document request = new Document("type", "LOGUP")
                        .append("username", username)
                        .append("fullname", fullname)
                        .append("email", email)
                        .append("password", hashedPassword);

                socketClient.send(request);

                Document response = Document.parse(
                        socketClient.getReader().readLine()
                );

                String type = response.getString("type");

                if ("LOGUP_OK".equals(type)) {
                    Platform.runLater(logUpView::showSuccess);
                } else {
                    Platform.runLater(() ->
                            logUpView.showError(response.getString("message"))
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        logUpView.showError("TCP not connected!")
                );
            }
        }).start();
    }
}
