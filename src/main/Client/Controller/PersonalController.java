package main.Client.Controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.Client.Network.TCP.SocketClient;
import main.Client.View.PersonalProfile;
import org.bson.Document;
import shared.DTO.UserDTO;

import javafx.scene.paint.Color;
import java.time.LocalDate;

public class PersonalController {
    private PersonalProfile personalProfileView;

    public PersonalController(PersonalProfile personalProfileView) {
        this.personalProfileView = personalProfileView;

    }

    public void getUserProfile(String email) {
        if (email.isEmpty()) {
            Platform.runLater(() -> {
                personalProfileView.showError("Please fill in all fields.");
            });
            return;
        }
        new Thread(() -> {
            try {
                SocketClient socketClient = SocketClient.getInstance();
                if (!socketClient.isConnected()) {
                    Platform.runLater(() -> personalProfileView.showError("TCP chưa kết nối. Hãy đợi kết nối xong!"));
                    return;
                }
                // Gui request
                Document getUserRequest = new Document("type", "GET_USER").append("email", email);
                socketClient.send(getUserRequest);

                // Nhan response
                Document response = Document.parse(socketClient.getReader().readLine());
                String type = response.getString("type");

                if ("GET_USER_OK".equals(type)) {
                    Document userDoc = (Document) response.get("user");
                    String dobStr = userDoc.getString("dob"); // server trả yyyy-MM-dd
                    LocalDate dob = (dobStr != null) ? LocalDate.parse(dobStr) : null;

                    // Convert Document sang UserDTO
                    UserDTO userDTO = new UserDTO(
                            userDoc.getString("username"),
                            userDoc.getString("fullName"),
                            userDoc.getString("email"),
                            null,
                            userDoc.getString("role"),
                            userDoc.getString("gender"),
                            userDoc.getString("phone"),
                            userDoc.getString("address"),
                            dob
                    );
                    personalProfileView.setCurrentUser(userDTO);
                }
                else {
                    String message = response.getString("message");
                    Platform.runLater(() -> personalProfileView.showError(message));
                }
            } catch (Exception ex) {
                Platform.runLater(() -> personalProfileView.showError("TCP not connected!"));
            }
        }).start();
    }

    public void updateUserProfile(UserDTO userDTO) {
        new Thread(() -> {
            try {
                SocketClient socketClient = SocketClient.getInstance();
                if (!socketClient.isConnected()) {
                    Platform.runLater(() -> personalProfileView.showError("TCP chưa kết nối. Hãy đợi kết nối xong!"));
                    return;
                }

                // Chuyển UserDTO sang Document
                Document userDoc = new Document()
                        .append("username", userDTO.getUsername())
                        .append("fullName", userDTO.getFullName())
                        .append("email", userDTO.getEmail())
                        .append("role", userDTO.getRole())
                        .append("gender", userDTO.getGender())
                        .append("phone", userDTO.getPhone())
                        .append("address", userDTO.getAddress())
                        .append("dob", userDTO.getDob() != null ? userDTO.getDob().toString() : null);

                // Tạo request gửi server
                Document updateRequest = new Document("type", "UPDATE_USER").append("user", userDoc);
                socketClient.send(updateRequest);

                // Nhận response từ server
                Document response = Document.parse(socketClient.getReader().readLine());
                String type = response.getString("type");


                if ("UPDATE_USER_OK".equals(type)) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("User profile updated successfully!");
                        alert.show();
                    });
                } else if ("UPDATE_USER_FAIL".equals(type)){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("Update failed. Please try again.");
                        alert.show();
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> personalProfileView.showError("Can not update user"));
            }
        }).start();
    }

    public void updateUserPassword(UserDTO userDTO) {
        new Thread(() -> {
            try {
                SocketClient socketClient = SocketClient.getInstance();
                if (!socketClient.isConnected()) {
                    Platform.runLater(() -> personalProfileView.showError("TCP chưa kết nối. Hãy đợi kết nối xong!"));
                    return;
                }

                // Chuyển UserDTO sang Document
                Document userDoc = new Document()
                        .append("username", userDTO.getUsername())
                        .append("fullName", userDTO.getFullName())
                        .append("email", userDTO.getEmail())
                        .append("password", userDTO.getPassword())
                        .append("role", userDTO.getRole())
                        .append("gender", userDTO.getGender())
                        .append("phone", userDTO.getPhone())
                        .append("address", userDTO.getAddress())
                        .append("dob", userDTO.getDob() != null ? userDTO.getDob().toString() : null);

                // Tạo request gửi server
                Document updatePasswordRequest = new Document("type", "UPDATE_PASSWORD").append("user", userDoc);
                socketClient.send(updatePasswordRequest);

                // Nhận response từ server
                Document response = Document.parse(socketClient.getReader().readLine());
                String type = response.getString("type");


                if ("UPDATE_PASSWORD_OK".equals(type)) {
                    Platform.runLater(() -> {
                        personalProfileView.currentPasswordMsg.setText("Password updated successfully");
                        personalProfileView.currentPasswordMsg.setTextFill(Color.GREEN);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Password updated successfully!");
                        alert.show();

                        // Reset các field
                        personalProfileView.currentPasswordField.clear();
                        personalProfileView.newPasswordField.clear();
                        personalProfileView.confirmPasswordField.clear();
                        personalProfileView.currentPasswordMsg.setText("");
                        personalProfileView.confirmPasswordMsg.setText("");
                    });
                } else if ("UPDATE_PASSWORD_FAIL".equals(type)){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("Update failed. Please try again.");
                        alert.show();
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> personalProfileView.showError("Can not update user"));
            }
        }).start();
    }
}
