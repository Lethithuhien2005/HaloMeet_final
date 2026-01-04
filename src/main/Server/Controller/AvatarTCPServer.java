//package main.Server.Controller;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.file.Files;
//
//public class AvatarTCPServer {
//
//    private final int PORT = 12345; // port phải trùng với client
//    private final String AVATAR_DIR = "E:\\DACS4\\imgsAVT"; // thư mục chứa avatar
//
//    public void start() {
//        new Thread(() -> {
//            try (ServerSocket server = new ServerSocket(PORT)) {
//                System.out.println("Avatar TCP server running on port " + PORT);
//
//                while (true) {
//                    Socket client = server.accept();
//                    System.out.println("Client connected: " + client.getRemoteSocketAddress());
//
//                    new Thread(() -> handleClient(client)).start();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    private void handleClient(Socket client) {
//        try (
//                Socket socket = client;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//                InputStream in = socket.getInputStream();
//                OutputStream out = socket.getOutputStream()
//        ) {
//            String command = reader.readLine();
//            String userIdHex = reader.readLine();
//
//            if ("UPLOAD_AVATAR".equals(command)) {
//                long length = Long.parseLong(reader.readLine());
//                File outFile = new File(AVATAR_DIR, userIdHex + ".jpg"); // lưu theo userIdHex.jpg
//                try (FileOutputStream fos = new FileOutputStream(outFile)) {
//                    byte[] buffer = new byte[4096];
//                    long remaining = length;
//                    int read;
//                    while (remaining > 0 && (read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
//                        fos.write(buffer, 0, read);
//                        remaining -= read;
//                    }
//                    fos.flush();
//                }
//                writer.println("UPLOAD_SUCCESS");
//                System.out.println("File uploaded for userId: " + userIdHex);
//            }
//            else if ("GET_AVATAR".equals(command)) {
//                File file = new File(AVATAR_DIR, userIdHex + ".jpg");
//                if (!file.exists()) {
//                    file = new File(AVATAR_DIR, "default.png");
//                }
//
//                writer.println(file.length());
//                writer.flush();
//
//                Files.copy(file.toPath(), out);
//                out.flush();
//
//                System.out.println("File sent for userId: " + userIdHex);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

package main.Server.Controller;

import main.Server.DAO.UserDAO;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class AvatarTCPServer {

    private static final int PORT = 12345;
    private static final String AVATAR_DIR = "E:\\DACS4\\imgsAVT";

    public void start() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("Avatar TCP server running on port " + PORT);

                while (true) {
                    Socket client = server.accept();
                    System.out.println("Client connected: " + client.getRemoteSocketAddress());
                    new Thread(() -> handleClient(client)).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(), true
                );
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            String command = reader.readLine();
            String userIdHex = reader.readLine();

            // ================= UPLOAD =================
            if ("UPLOAD_AVATAR".equals(command)) {

                long length = Long.parseLong(reader.readLine());
                String fileName = reader.readLine(); // ⚠ nhận tên file

                File folder = new File(AVATAR_DIR);
                if (!folder.exists()) folder.mkdirs();

                File outFile = new File(folder, fileName);

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[4096];
                    long remaining = length;
                    int read;
                    while (remaining > 0 &&
                            (read = in.read(buffer, 0,
                                    (int) Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, read);
                        remaining -= read;
                    }
                }

                // ✅ UPDATE MONGODB
                UserDAO userDAO = new UserDAO();
                boolean updated = userDAO.updateAvatar(
                        new ObjectId(userIdHex),
                        fileName
                );

                if (updated) {
                    writer.println("UPLOAD_SUCCESS");
                    System.out.println("Avatar updated in DB: " + fileName);
                } else {
                    writer.println("UPLOAD_FAIL");
                    System.out.println("Failed to update avatar in DB");
                }
            }

            // ================= DOWNLOAD =================
            else if ("GET_AVATAR".equals(command)) {

                UserDAO userDAO = new UserDAO();
                String avatarName = "default.png";

                var userDoc = userDAO.getUserById(new ObjectId(userIdHex));
                if (userDoc != null && userDoc.getString("avatar") != null) {
                    avatarName = userDoc.getString("avatar");
                }

                File file = new File(AVATAR_DIR, avatarName);
                if (!file.exists()) {
                    file = new File(AVATAR_DIR, "default.png");
                }

                writer.println(file.length());
                writer.flush();

                Files.copy(file.toPath(), out);
                out.flush();

                System.out.println("Avatar sent: " + avatarName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
