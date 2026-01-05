package main.Server.Controller;
// NHẬN REQUEST TỪ CLIENT
// XỬ LÝ LOGIC ĐIỀU PHỐI
// GỌI DAO ĐỂ THAO TÁC CSDL
// GỬI REPONSE VỀ CLIENT

import com.mongodb.client.MongoDatabase;
import main.Server.DAO.UserDAO;
import main.Server.Model.User;
import main.util.MongoDBConnection;
import main.util.PasswordUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import shared.DTO.UserDTO;

import static com.mongodb.client.model.Filters.ne;

public class UserController {
    private UserDAO userDAO;
    private static final String SALT = "hienanh"; // Salt cố định dùng cho cả register và login


    public UserController() {
        userDAO = new UserDAO();
    }

    public String register(String username,String fullName, String email, String hashedPassword) {
        if (username.isEmpty() || email.isEmpty() || hashedPassword.isEmpty()) {
            return "Please fill in all fields.";
        }
        if (userDAO.checkEmail(email)) {
            return "Email already exists!!!";
        }

        User user = new User(username,fullName, email, hashedPassword, "user");
        userDAO.addUser(user);
        return "SUCCESS";
    }

    public String login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return "Please fill in all fields.";
        }
        // Get user from database base on email
        User user = userDAO.getUserByEmail(email);
        if (user ==  null) {
            return "Email does not exists!";
        }

        // Check password
//        String salt = "hienanh";
//        String hashedInput = PasswordUtils.hashPassword(password + salt);
        String hashedInput = PasswordUtils.hashPassword(password + SALT);
        if (!hashedInput.equals(user.getPassword())) {
            return "Incorrect password!";
        }

        // update updated_at
        userDAO.updateUpdatedAt(email);

        return "SUCCESS";
    }
    public User getUserProfile(String email) {
        return userDAO.getUserByEmail(email);
    }

    public boolean updateUserProfile(User user) {
        return userDAO.updateUser(user);
    }

    public Document updateUserPassword(Document request) {
        Document userDoc = (Document) request.get("user");
        if (userDoc == null) {
            return new Document("type", "UPDATE_PASSWORD_FAIL").append("message", "No user data provided");
        }
        // Chuyển Document sang User object
        User user = new User();
        user.setUsername(userDoc.getString("username"));
        user.setFullName(userDoc.getString("fullName"));
        user.setEmail(userDoc.getString("email"));
        user.setPassword(userDoc.getString("password"));
        user.setRole(userDoc.getString("role"));
        user.setGender(userDoc.getString("gender"));
        user.setPhone(userDoc.getString("phone"));
        user.setAddress(userDoc.getString("address"));
        String dobStr = userDoc.getString("dob");
        user.setDob(dobStr != null ? LocalDate.parse(dobStr) : null);

        boolean success = userDAO.updatePassword(user.getEmail(), user.getPassword());
        if (success) {
            return new Document("type", "UPDATE_PASSWORD_OK");
        } else {
            return new Document("type", "UPDATE_PASSWORD_FAIL")
                    .append("message", "Password update failed");
        }
    }

    // Lấy tất cả user trừ email hiện tại
    public Document getAllUsersExcept(Document request) {
        String email = request.getString("email");

        List<UserDTO> users = new ArrayList<>();

        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = db.getCollection("users"); // đúng tên collection

        // Lấy toàn bộ user có email khác currentEmail
        FindIterable<Document> docs = collection.find(ne("email", email));

        for (Document doc : docs) {

            UserDTO user = new UserDTO(
                    doc.getString("username"),
                    doc.getString("fullName"), // đúng key
                    doc.getString("email"),
                    doc.getString("password"),
                    doc.getString("role"),
                    doc.getString("gender"),
                    doc.getString("phone"),
                    doc.getString("address"),
                    doc.getDate("dob") != null
                            ? doc.getDate("dob").toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            : null
            );

            user.setUserId(doc.getObjectId("_id"));     // thêm
            user.setCreatedAt(doc.getDate("created_at"));
            user.setUpdatedAt(doc.getDate("updated_at"));

            users.add(user);
        }

        // Chuyển sang List<Document> để gửi cho client
        List<Document> userDocs = users.stream().map(u -> {
            Document d = new Document();
            d.append("userId", u.getUserId().toHexString());
            d.append("username", u.getUsername());
            d.append("fullName", u.getFullName());
            d.append("email", u.getEmail());
            d.append("role", u.getRole());
            d.append("gender", u.getGender());
            d.append("phone", u.getPhone());
            d.append("address", u.getAddress());
            d.append("status", u.getStatus());
            d.append("dob", u.getDob() != null ? u.getDob().toString() : null);
            d.append("createdAt", u.getCreatedAt());
            d.append("updatedAt", u.getUpdatedAt());
            return d;
        }).toList();

        return new Document("type", "GET_OTHER_USERS_OK").append("users", userDocs);
    }

    public Document handleGetUser(Document request) {
        String email = request.getString("email");
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            return new Document("type", "ERROR")
                    .append("message", "User not found");
        } else {
            // Chuyen User sang Document
            Document userDoc = new Document()
                    .append("userId", user.getUserId().toHexString())
                    .append("username", user.getUsername())
                    .append("fullName", user.getFullName())
                    .append("email", user.getEmail())
                    .append("role", user.getRole())
                    .append("gender", user.getGender())
                    .append("phone", user.getPhone())
                    .append("address", user.getAddress())
                    .append("status", user.getStatus())
                    .append("dob", user.getDob() != null ? user.getDob().toString() : null)
                    .append("createdAt", user.getCreatedAt())
                    .append("updatedAt", user.getUpdatedAt());

            return new Document("type", "GET_USER_OK").append("user", userDoc);
        }

    }

    public Document handleUpdateUser(Document request) {
        Document userDoc = (Document) request.get("user");
        if (userDoc == null) {
            return new Document("type", "UPDATE_USER_FAIL").append("message", "No user data provided");
        }
        // Chuyển Document sang User object
        User user = new User();
        user.setUsername(userDoc.getString("username"));
        user.setFullName(userDoc.getString("fullName"));
        user.setEmail(userDoc.getString("email"));
        user.setRole(userDoc.getString("role"));
        user.setGender(userDoc.getString("gender"));
        user.setPhone(userDoc.getString("phone"));
        user.setAddress(userDoc.getString("address"));
        String dobStr = userDoc.getString("dob");
        user.setDob(dobStr != null ? LocalDate.parse(dobStr) : null);

        boolean success = userDAO.updateUser(user);

        if (success) {
            return new Document("type", "UPDATE_USER_OK");
        } else {
            return new Document("type", "UPDATE_USER_FAIL")
                    .append("message", "Database update failed");
        }

    }
}
