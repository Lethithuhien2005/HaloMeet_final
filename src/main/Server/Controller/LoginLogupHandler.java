package main.Server.Controller;

import main.Server.Controller.UserController;
import org.bson.Document;

public class LoginLogupHandler {
    private final UserController userController = new UserController();

    // Trả về Document response
    public Document handle(Document request) {
        System.out.println("SERVER RECEIVED: " + request.toJson());

        String type = request.getString("type");

//        if ("LOGIN".equals(type)) {
//            return handleLogin(request);
//        } else if ("LOGUP".equals(type)) {
//            return handleLogup(request);
//        } else {
//            return new Document("type", "ERROR")
//                    .append("message", "LoginLogupHandler cannot handle type: " + type);
//        }
        if ("LOGIN".equals(type)) {
            return handleLogin(request);
        }
        else if ("LOGUP".equals(type)) {
            return handleLogup(request);
        }
        else if ("GET_PROFILE".equals(type)) {
            return handleGetProfile(request);
        }
        else {
            return new Document("type", "ERROR")
                    .append("message", "Cannot handle type: " + type);
        }
    }
    private Document handleGetProfile(Document request) {

        String email = request.getString("email");
        System.out.println("GET_PROFILE email = " + email);


        if (email == null || email.isEmpty() ) {
            return new Document("type", "GET_PROFILE_FAIL")
                    .append("message", "Email is required");
        }

        var user = userController.getUserProfile(email);
        System.out.println("👤 USER = " + user);

        if (user == null) {
            return new Document("type", "GET_PROFILE_FAIL")
                    .append("message", "User not found");
        }

        return new Document("type", "GET_PROFILE_OK")
                .append("email", user.getEmail())
                .append("fullName", user.getFullName())
                .append("userIdHex", user.getUserId().toHexString())
                .append("role", user.getRole());
    }


    private Document handleLogin(Document request) {
        String email = request.getString("email");
        String password = request.getString("password");

        String result = userController.login(email, password);
        if ("SUCCESS".equals(result)) {
            // Lấy userIdHex từ UserController
            String userIdHex = userController.getUserProfile(email).getUserId().toHexString();
            return new Document("type", "LOGIN_OK")
                    .append("userIdHex", userIdHex);
        } else {
            return new Document("type", "LOGIN_FAIL")
                    .append("message", result);
        }

    }

    private Document handleLogup(Document request) {
        // TODO: xử lý logup
//        return new Document("type", "LOGUP_RESPONSE").append("status", "OK");
        String username = request.getString("username");
        String fullname = request.getString("fullname");
        String email = request.getString("email");
        String password = request.getString("password");

        String result = userController.register(username, fullname, email, password);

        if ("SUCCESS".equals(result)) {
            return new Document("type", "LOGUP_OK");
        } else {
            return new Document("type", "LOGUP_FAIL")
                    .append("message", result);
        }
    }
}
