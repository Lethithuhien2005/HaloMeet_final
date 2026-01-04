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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

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

    public void updateUserPassword(User user) {
        userDAO.updatePassword(user.getEmail(), user.getPassword());
    }

    // Lấy tất cả user trừ email hiện tại
    public List<User> getAllUsersExcept(String email) {

        List<User> users = new ArrayList<>();

        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = db.getCollection("users"); // đúng tên collection

        // Lấy toàn bộ user có email khác currentEmail
        FindIterable<Document> docs = collection.find(ne("email", email));

        for (Document doc : docs) {

            User user = new User(
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

            user.setUserId(doc.getObjectId("_id"));     // ✅ thêm
            user.setCreatedAt(doc.getDate("created_at"));
            user.setUpdatedAt(doc.getDate("updated_at"));

            users.add(user);
        }

        return users;
    }


}
