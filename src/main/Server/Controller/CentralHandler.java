package main.Server.Controller;
// NHẬN REQUEST TỪ CLIENT
// XỬ LÝ LOGIC ĐIỀU PHỐI
// GỌI DAO ĐỂ THAO TÁC CSDL
// GỬI REPONSE VỀ CLIENT

import main.Server.DAO.MongoChatRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// CHAT SERVER
public class CentralHandler {
    private final int port;
    private final MongoChatRepository repo;

    // userIdHex -> handler
    private final Map<String, ClientHandler> online = new ConcurrentHashMap<>();

    public CentralHandler(int port, MongoChatRepository repo) {
        this.port = port;
        this.repo = repo;
    }

    public void start() throws Exception {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Server listening on port " + port);

        while (true) {
            Socket s = ss.accept();
            ClientHandler h = new ClientHandler(s);
            new Thread(h).start();
        }
    }

    class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String userIdHex; // set sau HELLO

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private void send(Document doc) {
            synchronized (out) {
                out.println(doc.toJson());
                out.flush();
            }
        }

        private long toMillis(Object createdAtValue) {
            if (createdAtValue instanceof java.util.Date) {
                return ((java.util.Date) createdAtValue).getTime();
            }
            if (createdAtValue instanceof Number) {
                return ((Number) createdAtValue).longValue();
            }
            return 0;
        }
        /** Convert raw message docs -> json-friendly DTO */
        private List<Document> toMessageDtos(List<Document> history) {
            List<Document> arr = new ArrayList<>();
            if (history == null) return arr;

            for (Document m : history) {
                ObjectId mid = m.getObjectId("_id");
                ObjectId sid = m.getObjectId("sender_id");

                arr.add(new Document()
                        .append("messageId", mid != null ? mid.toHexString() : null)
                        .append("senderId", sid != null ? sid.toHexString() : null)
                        .append("content", m.getString("content"))
                        .append("createdAt", toMillis(m.get("created_at")))
                );
            }
            return arr;
        }


        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                LoginLogupHandler loginLogupHandler = new LoginLogupHandler();
                MeetingHandler meetingHandler = new MeetingHandler();
                UserController userController = new UserController();

                String line;
                while ((line = in.readLine()) != null) {
                    Document req = Document.parse(line);
                    String type = req.getString("type");

                    if ("LOGIN".equals(type) || "LOGUP".equals(type) || "GET_PROFILE".equals(type)) {
                        // LoginLogupHandler se xu ly
                        Document resp = loginLogupHandler.handle(req);
                        send(resp);
                    }
                    else if ("HELLO".equals(type)) handleHello(req);
                    else if ("OPEN_PRIVATE".equals(type)) handleOpenPrivate(req);
                    else if ("SEND".equals(type)) handleSend(req);
                    else if ("OPEN_GROUP".equals(type)) handleOpenGroup(req);
                    else if ("CREATE_GROUP".equals(type)) handleCreateGroup(req);
                    else if ("LIST_GROUPS".equals(type)) handleListGroups(req);
                    else if ("GET_USER".equals(type)) {
                        // UserController se xu ly
                        Document resp = userController.handleGetUser(req);
                        send(resp);
                    }
                    else if ("GET_OTHER_USERS".equals(type)) {
                        Document resp = userController.getAllUsersExcept(req);
                        send(resp);
                    }
                    else if ("UPDATE_USER".equals(type)) {
                        // MeetingHandler se xu ly
                        Document resp = userController.handleUpdateUser(req);
                        send(resp);
                    }
                    else if ("UPDATE_PASSWORD".equals(type)) {
                        // MeetingHandler se xu ly
                        Document resp = userController.updateUserPassword(req);
                        send(resp);
                    }
                    else send(new Document("type", "ERROR").append("message", "Unknown type: " + type));
                }
            }  catch (Exception ex) {
                ex.printStackTrace();  // bắt buộc in ra
            } finally {
                if (userIdHex != null) online.remove(userIdHex);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void handleHello(Document req) {
            userIdHex = req.getString("userId");
            if (userIdHex == null || userIdHex.trim().isEmpty()) {
                send(new Document("type", "ERROR").append("message", "HELLO missing userId"));
                return;
            }
            online.put(userIdHex, this);

            send(new Document("type", "HELLO_OK"));
        }

        private void handleOpenPrivate(Document req) {
            try {
//                String withUserIdHex = req.getString("withUserId");

                if (userIdHex == null) {
                    send(new Document("type", "ERROR").append("message", "Send HELLO first"));
                    return;
                }

                String withUserIdHex = req.getString("withUserId");
                if (withUserIdHex == null || withUserIdHex.trim().isEmpty()) {
                    send(new Document("type", "ERROR").append("message", "OPEN_PRIVATE missing withUserId"));
                    return;
                }

                String convoIdHex = repo.getOrCreatePrivateConversation(userIdHex, withUserIdHex);

//                List<Document> history = repo.getMessages(convoIdHex, 50);
                List<Document> history = repo.getMessages(convoIdHex, 200);
                List<Document> arr = toMessageDtos(history);

                // convert history ra json-friendly (ObjectId -> hex)
//                var arr = new java.util.ArrayList<Document>();


//                for (Document m : history) {
//                    java.util.Date d = m.getDate("created_at");
//                    long createdAtMs = (d != null) ? d.getTime() : 0;
//
//                    arr.add(new Document()
//                            .append("messageId", m.getObjectId("_id").toHexString())
//                            .append("senderId", m.getObjectId("sender_id").toHexString())
//                            .append("content", m.getString("content"))
//                             .append("createdAt", createdAtMs)
//                    );
//                }

                send(new Document("type", "OPEN_PRIVATE_OK")
                        .append("conversationId", convoIdHex)
                        .append("withUserId", withUserIdHex)
                        .append("messages", arr)
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                send(new Document("type","ERROR").append("message", ex.getMessage()));
            }
        }

        private void handleSend(Document req) {
            try {
                if (userIdHex == null) {
                    send(new Document("type", "ERROR").append("message", "Send HELLO first"));
                    return;
                }

                String convoIdHex = req.getString("conversationId");
                String content = req.getString("content");

                if (convoIdHex == null || convoIdHex.trim().isEmpty()) {
                    send(new Document("type", "ERROR").append("message", "SEND missing conversationId"));
                    return;
                }
                if (content == null) content = "";

                Document saved = repo.insertMessage(convoIdHex, userIdHex, content);
                // ✅ created_at là Date -> convert ra millis để gửi client
                java.util.Date createdAtDate = saved.getDate("created_at");
                long createdAtMs = createdAtDate.getTime();

                Document push = new Document("type", "NEW_MESSAGE")
                        .append("conversationId", convoIdHex)
                        .append("message", new Document()
                                        .append("messageId", saved.getObjectId("_id").toHexString())
                                        .append("senderId", saved.getObjectId("sender_id").toHexString())
                                        .append("content", saved.getString("content"))
//                            .append("createdAt", saved.getLong("created_at"))
//                                        .append("createdAt", createdAtMs)
                                        .append("createdAt", toMillis(saved.get("created_at")))
                        );

                // push về người gửi (ack)
                send(push);

                // push cho người còn lại nếu online
//            String otherHex = repo.getOtherUserInConversation(convoIdHex, userIdHex);
//            if (otherHex != null) {
//                ClientHandler other = online.get(otherHex);
//                if (other != null) other.send(push);
//            }
                String ctype = repo.getConversationType(convoIdHex);
                if ("private".equals(ctype)) {
                    String otherHex = repo.getOtherUserInConversation(convoIdHex, userIdHex);
                    if (otherHex != null) {
                        ClientHandler other = online.get(otherHex);
                        if (other != null) other.send(push);
                    }
                } else if ("group".equals(ctype)) {
                    List<String> members = repo.getMemberUserIds(convoIdHex);
                    for (String uid : members) {
                        if (uid.equals(userIdHex)) continue;
                        ClientHandler h = online.get(uid);
                        if (h != null) h.send(push);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                send(new Document("type", "ERROR").append("message", ex.getMessage()));
            }
        }

//        private void handleOpenGroup(Document req) {
//            try {
//                if (userIdHex == null) {
//                    send(new Document("type", "ERROR").append("message", "Send HELLO first"));
//                    return;
//                }
//
//                String groupName = req.getString("groupName");
//                String convoIdHex = repo.getOrCreateGroupConversationByName(groupName, userIdHex);
//
//                List<Document> history = repo.getMessages(convoIdHex, 50);
//
//
//                var arr = new java.util.ArrayList<Document>();
//                for (Document m : history) {
//                    java.util.Date createdAtDate = m.getDate("created_at");
//                    long createdAtMs = createdAtDate.getTime();
//
//                    arr.add(new Document()
//                            .append("messageId", m.getObjectId("_id").toHexString())
//                            .append("senderId", m.getObjectId("sender_id").toHexString())
//                            .append("content", m.getString("content"))
//                            .append("createdAt", createdAtMs)
//                    );
//                }
//
//                send(new Document("type", "OPEN_GROUP_OK")
//                        .append("conversationId", convoIdHex)
//                        .append("groupName", groupName)
//                        .append("messages", arr)
//                );
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                send(new Document("type","ERROR").append("message", ex.getMessage()));
//            }
//        }

        private void handleOpenGroup(Document req) {
            try {
                if (userIdHex == null) {
                    send(new Document("type", "ERROR").append("message", "Send HELLO first"));
                    return;
                }

                String groupName = req.getString("groupName");
                if (groupName == null || groupName.trim().isEmpty()) {
                    send(new Document("type", "ERROR").append("message", "OPEN_GROUP missing groupName"));
                    return;
                }

                // ✅ lấy đúng conversationId từ collection groups
                String convoIdHex = repo.openGroupByName(groupName, userIdHex);
                if (convoIdHex == null) {
                    send(new Document("type", "ERROR").append("message", "Group not found"));
                    return;
                }

//                List<Document> history = repo.getMessages(convoIdHex, 50);
//                var arr = new java.util.ArrayList<Document>();
//                for (Document m : history) {
//                    long createdAtMs = toMillis(m.get("created_at"));
//                    arr.add(new Document()
//                            .append("messageId", m.getObjectId("_id").toHexString())
//                            .append("senderId", m.getObjectId("sender_id").toHexString())
//                            .append("content", m.getString("content"))
//                            .append("createdAt", createdAtMs)
//                    );
//                }

                List<Document> history = repo.getMessages(convoIdHex, 200);
                List<Document> arr = toMessageDtos(history);

                send(new Document("type", "OPEN_GROUP_OK")
                        .append("conversationId", convoIdHex)
                        .append("groupName", groupName)
                        .append("messages", arr));

            } catch (Exception ex) {
                ex.printStackTrace();
                send(new Document("type","ERROR").append("message", ex.getMessage()));
            }
        }
        @SuppressWarnings("unchecked")
        private void handleCreateGroup(Document req) {
            try {
                if (userIdHex == null) {
                    send(new Document("type", "ERROR").append("message", "Send HELLO first"));
                    return;
                }

                String groupName = req.getString("groupName");
                List<String> memberIds = (List<String>) req.get("memberIds"); // json array

                String convoIdHex = repo.createGroup(userIdHex, groupName, memberIds, null);

                // group mới -> history rỗng
//                send(new Document("type", "OPEN_GROUP_OK")
//                        .append("conversationId", convoIdHex)
//                        .append("groupName", groupName)
//                        .append("messages", new java.util.ArrayList<>()));
                send(new Document("type", "OPEN_GROUP_OK")
                        .append("conversationId", convoIdHex)
                        .append("groupName", groupName)
                        .append("messages", new ArrayList<Document>())
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                send(new Document("type","ERROR").append("message", ex.getMessage()));
            }
        }
        private void handleListGroups(Document req) {
            try {
                if (userIdHex == null) {
                    send(new Document("type", "ERROR").append("message", "Send HELLO first"));
                    return;
                }

                List<Document> groups = repo.listGroupsForUser(userIdHex);

                // groups đã json-friendly: conversationId, groupName, avatar...
                send(new Document("type", "LIST_GROUPS_OK").append("groups", groups));
            } catch (Exception ex) {
                ex.printStackTrace();
                send(new Document("type","ERROR").append("message", ex.getMessage()));
            }
        }

    }
}
