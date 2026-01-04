package main.Server.Controller;

import main.Server.DAO.UserDAO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.nio.file.Files;

public class AvatarService {
    private UserDAO userDAO = new UserDAO(); // DAO đã có sẵn

    // Gửi avatar của user cho client qua OutputStream
    public void sendAvatar(String userIdHex, OutputStream clientOut) throws IOException {
        Document userDoc = userDAO.getUserById(new ObjectId(userIdHex));
        String avatarFileName = "default.png"; // mặc định
        if(userDoc != null && userDoc.getString("avatar") != null) {
            avatarFileName = userDoc.getString("avatar");
        }

        File file = new File("E:\\DACS4\\imgsAVT", avatarFileName);
        if(!file.exists()) {
            file = new File("E:\\DACS4\\imgsAVT\\default.png");
        }
        else
            System.out.println("Avatar file PATH doesn't exist.");

        // gửi độ dài trước
        PrintWriter writer = new PrintWriter(clientOut, true);
        writer.println(file.length());

//        writer.flush();
//
//        Files.copy(file.toPath(), clientOut);
//        clientOut.flush();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while((read = fis.read(buffer)) != -1) {
                clientOut.write(buffer, 0, read);
            }
        }
        clientOut.flush();
    }
}
