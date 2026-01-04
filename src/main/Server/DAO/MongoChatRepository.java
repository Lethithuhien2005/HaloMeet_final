package main.Server.DAO; // THAO TÁC TRỰC TIẾP VỚI CSDL

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import main.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;

public class MongoChatRepository {
    private final MongoDatabase db;
    private final MongoCollection<Document> conversations;
    private final MongoCollection<Document> groups;
    private final MongoCollection<Document> members;
    private final MongoCollection<Document> messages;


    public MongoChatRepository() {
        db = MongoDBConnection.getDatabase();
        conversations = db.getCollection("conversations");
        groups = db.getCollection("groups");
        members = db.getCollection("member");      // bạn đặt tên collection là "member"
        messages = db.getCollection("message");    // bạn đặt tên collection là "message"

        // Index khuyến nghị
//        conversations.createIndex(Indexes.ascending("type", "private_key"));
//        members.createIndex(Indexes.ascending("conversation_id", "user_id"),
//                new IndexOptions().unique(true));
//        messages.createIndex(Indexes.ascending("conversation_id", "created_at"));
        conversations.createIndex(Indexes.ascending("type"));
        groups.createIndex(Indexes.ascending("name_group"), new IndexOptions().unique(true));
        groups.createIndex(Indexes.ascending("conversation_id"), new IndexOptions().unique(true));

        members.createIndex(Indexes.ascending("conversation_id", "user_id"),
                new IndexOptions().unique(true));
        messages.createIndex(Indexes.ascending("conversation_id", "created_at"));
    }

    private String privateKey(String aHex, String bHex) {
        return (aHex.compareTo(bHex) < 0) ? (aHex + "_" + bHex) : (bHex + "_" + aHex);
    }

    public String getOrCreatePrivateConversation(String userAHex, String userBHex) {
        String key = privateKey(userAHex, userBHex);

        Document found = conversations.find(and(eq("type", "private"), eq("private_key", key))).first();
        if (found != null) return found.getObjectId("_id").toHexString();

        ObjectId convoId = new ObjectId();
//        long now = System.currentTimeMillis();
        Date now = new Date();

        conversations.insertOne(new Document("_id", convoId)
                .append("type", "private")
                .append("private_key", key)
                .append("created_at", now)
                .append("created_by", new ObjectId(userAHex))
        );

        members.insertMany(List.of(
                new Document("_id", new ObjectId())
                        .append("conversation_id", convoId)
                        .append("user_id", new ObjectId(userAHex))
                        .append("role", "member")
                        .append("is_muted", true)
                        .append("is_camera_on", false),
                new Document("_id", new ObjectId())
                        .append("conversation_id", convoId)
                        .append("user_id", new ObjectId(userBHex))
                        .append("role", "member")
                        .append("is_muted", true)
                        .append("is_camera_on", false)
        ));

        return convoId.toHexString();
    }

    public List<Document> getMessages(String conversationIdHex, int limit) {
        ObjectId convoId = new ObjectId(conversationIdHex);

        List<Document> out = new ArrayList<>();
        messages.find(eq("conversation_id", convoId))
                .sort(ascending("created_at"))
                .limit(limit)
                .into(out);
        return out;
    }

    public Document insertMessage(String conversationIdHex, String senderIdHex, String content) {
        ObjectId msgId = new ObjectId();
//        long now = System.currentTimeMillis();
        java.util.Date now = new java.util.Date();

        Document msg = new Document("_id", msgId)
                .append("conversation_id", new ObjectId(conversationIdHex))
                .append("sender_id", new ObjectId(senderIdHex))
                .append("content", content)
                .append("created_at", now);

        messages.insertOne(msg);
        return msg;
    }

    // tìm user còn lại trong private conversation bằng collection member
    public String getOtherUserInConversation(String conversationIdHex, String senderIdHex) {
        ObjectId convoId = new ObjectId(conversationIdHex);
        ObjectId senderId = new ObjectId(senderIdHex);

        Document other = members.find(and(
                eq("conversation_id", convoId),
                ne("user_id", senderId)
        )).first();

        if (other == null) return null;
        return other.getObjectId("user_id").toHexString();
    }


    // ====== GROUP ======
    public String createGroup(String creatorHex, String groupName, List<String> memberIdsHex, String avatar) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new RuntimeException("groupName is empty");
        }

        // check trùng tên
        Document existing = groups.find(eq("name_group", groupName)).first();
        if (existing != null) {
            throw new RuntimeException("Group name already exists");
        }

        ObjectId convoId = new ObjectId();
        Date now = new Date();

        // 1) conversations: chỉ _id, type, created_at, created_by
        conversations.insertOne(new Document("_id", convoId)
                .append("type", "group")
                .append("created_at", now)
                .append("created_by", new ObjectId(creatorHex)));

        // 2) groups
        ObjectId groupId = new ObjectId();
        groups.insertOne(new Document("_id", groupId)
                .append("name_group", groupName)
                .append("avatar", avatar) // có thể null
                .append("conversation_id", convoId));

        // 3) members (creator là leader)
        List<Document> docs = new ArrayList<>();
        docs.add(new Document("_id", new ObjectId())
                .append("conversation_id", convoId)
                .append("user_id", new ObjectId(creatorHex))
                .append("role", "leader"));

        if (memberIdsHex != null) {
            for (String uid : memberIdsHex) {
                if (uid == null || uid.equals(creatorHex)) continue;
                docs.add(new Document("_id", new ObjectId())
                        .append("conversation_id", convoId)
                        .append("user_id", new ObjectId(uid))
                        .append("role", "member"));
            }
        }

        if (docs.size() > 0) members.insertMany(docs);

        return convoId.toHexString();
    }
    public String openGroupByName(String groupName, String requesterHex) {
        Document g = groups.find(eq("name_group", groupName)).first();
        if (g == null) return null;

        ObjectId convoId = g.getObjectId("conversation_id");

        // optional: chỉ cho mở nếu là member
        Document mem = members.find(and(eq("conversation_id", convoId),
                eq("user_id", new ObjectId(requesterHex)))).first();
        if (mem == null) throw new RuntimeException("You are not a member of this group");

        return convoId.toHexString();
    }

    public String getOrCreateGroupConversationByName(String groupName, String joinerHex) {
        // tìm group theo type + group_name
        Document found = conversations.find(and(eq("type", "group"), eq("group_name", groupName))).first();
        ObjectId convoId;

        if (found != null) {
            convoId = found.getObjectId("_id");
        } else {
            convoId = new ObjectId();
            conversations.insertOne(new Document("_id", convoId)
                    .append("type", "group")
                    .append("group_name", groupName)
                    .append("created_at", new java.util.Date())   // đồng bộ kiểu Date
                    .append("created_by", new ObjectId(joinerHex))
            );
        }

        // đảm bảo joiner là member (upsert)
        members.updateOne(
                and(eq("conversation_id", convoId), eq("user_id", new ObjectId(joinerHex))),
                new Document("$setOnInsert", new Document("_id", new ObjectId())
                        .append("conversation_id", convoId)
                        .append("user_id", new ObjectId(joinerHex))
                        .append("role", "member")
                ),
                new com.mongodb.client.model.UpdateOptions().upsert(true)
        );

        return convoId.toHexString();
    }
    public List<String> getMemberUserIds(String conversationIdHex) {
        ObjectId convoId = new ObjectId(conversationIdHex);
        List<String> ids = new ArrayList<>();

        for (Document d : members.find(eq("conversation_id", convoId))) {
            ids.add(d.getObjectId("user_id").toHexString());
        }
        return ids;
    }
    // Lấy type của conversation (private/group)
    public String getConversationType(String conversationIdHex) {
        Document c = conversations.find(eq("_id", new ObjectId(conversationIdHex))).first();
        return (c != null) ? c.getString("type") : null;
    }
    public List<Document> listGroupsForUser(String userIdHex) {
        System.out.println("LIST_GROUPS requested by " + userIdHex);

        ObjectId uid = new ObjectId(userIdHex);

        // 1) lấy tất cả conversation_id mà user đang là member
        List<ObjectId> convoIds = new ArrayList<>();
        for (Document m : members.find(eq("user_id", uid))) {
            convoIds.add(m.getObjectId("conversation_id"));
        }
        if (convoIds.isEmpty()) return List.of();

        // 2) từ convoIds -> lấy group info trong collection groups
        List<Document> out = new ArrayList<>();
        for (Document g : groups.find(in("conversation_id", convoIds))) {
            ObjectId convoId = g.getObjectId("conversation_id");
            out.add(new Document()
                    .append("conversationId", convoId.toHexString())
                    .append("groupId", g.getObjectId("_id").toHexString())
                    .append("groupName", g.getString("name_group"))
                    .append("avatar", g.getString("avatar"))
            );
        }
        return out;
    }


}
