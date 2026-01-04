package main.Server.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import main.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MeetingDAO {
    private MongoCollection<Document> conversations;
    private MongoCollection<Document> rooms;
    private MongoCollection<Document> meeting_participants;
    private MongoCollection<Document> messages;

    public MeetingDAO() {
        conversations = MongoDBConnection.getDatabase().getCollection("conversations");
        rooms = MongoDBConnection.getDatabase().getCollection("rooms");
        meeting_participants = MongoDBConnection.getDatabase().getCollection("meeting_participants");
        messages = MongoDBConnection.getDatabase().getCollection("messages");
    }

    // Add a meeting to database
    public ObjectId createMeeting(ObjectId hostId, String title, String meeting_code, String passcode) {
        // Tao conversation
        Document conversationDoc = new Document("type", "meeting")
                .append("created_by", hostId)
                .append("created_at", new Date());
        conversations.insertOne(conversationDoc);
        ObjectId conservationId = conversationDoc.getObjectId("_id"); // Lay id tu database

        // Tao phong hop
        Document roomDoc = new Document("title", title)
                .append("meeting_code", meeting_code)
                .append("passcode", passcode)
                .append("status", "active")
                .append("created_by", hostId)
                .append("created_at", new Date())
                .append("conversation_id", conservationId);
        rooms.insertOne(roomDoc);

        return conservationId;
    }

    // Lay phong hop thong qua meetingCode
    public Document getRoomByMeetingCode(String meetingCode) {
        return rooms.find(new Document("meeting_code", meetingCode)).first();
    }

    // Lay phong hop thong qua conversationId
    public Document getRoomByConversationId(ObjectId conversationId) {
        return rooms.find(new Document("conversation_id", conversationId)).first();
    }

    // Them user vao bang meeting_participants
    public void addParticipant(ObjectId roomId, ObjectId userId, String role) {
        Document memberDoc = new Document("room_id", roomId)
                .append("user_id", userId)
                .append("role", role)
                .append("joined_at", new Date())
                .append("left_at", null)
                .append("status", "joined")
                .append("is_muted", true)
                .append("is_camera_on", false);
        meeting_participants.insertOne(memberDoc);
    }

    public List<Document> getActiveParticipants(ObjectId roomId) {
        List<Document> participantList = new ArrayList<>();
        meeting_participants.find(new Document("room_id", roomId)
                        .append("status", "joined")
        ).forEach(participantList::add);

        return participantList;
    }

    public boolean hasEverJoined(ObjectId roomId, ObjectId userId) {
        Document query = new Document("room_id", roomId)
                .append("user_id", userId);

        return meeting_participants.find(query).first() != null;
    }

    public List<Document> getMeetingTodayByUser(ObjectId userID) {

        // Lấy thời gian đầu ngày & cuối ngày
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = cal.getTime();

        // Lấy danh sách room_id mà user đã tham gia
        List<ObjectId> roomIds = meeting_participants.find(new Document("user_id", userID)).map(doc -> doc.getObjectId("room_id"))
                .into(new ArrayList<>());

        // Lay nhung cuoc hop hom nay
        Document query = new Document("_id", new Document("$in", roomIds))
                .append("created_at", new Document("$gte", startOfDay).append("$lt", endOfDay));
        return rooms.find(query).into(new ArrayList<>());

    }
    public List<Document> getMeetingsLast7Days(ObjectId userId) {

        Instant now = Instant.now();
        Instant sevenDaysAgo = now.minus(7, ChronoUnit.DAYS);

        return meeting_participants.aggregate(List.of(
                Aggregates.match(Filters.and(
                        Filters.eq("user_id", userId),
                        Filters.gte("joined_at", Date.from(sevenDaysAgo))
                )),
                Aggregates.lookup(
                        "rooms",
                        "room_id",
                        "_id",
                        "room"
                ),
                Aggregates.unwind("$room"),
                Aggregates.sort(Sorts.descending("joined_at"))
        )).into(new ArrayList<>());
    }


    public void rejoin(ObjectId roomId, ObjectId userId) {
        meeting_participants.updateOne(
                new Document("room_id", roomId)
                        .append("user_id", userId),
                new Document("$set",
                        new Document("status", "joined")
                                .append("left_at", null)
                                .append("joined_at", new Date()))
        );
    }

    public void updateStatusParticipant(ObjectId roomId, ObjectId userId) {
        meeting_participants.updateOne(
                Filters.and(
                        Filters.eq("room_id", roomId),
                        Filters.eq("user_id", userId),
                        Filters.eq("status", "joined") // chỉ update nếu đang trong phong hop
                ),
                Updates.combine(
                        Updates.set("status", "left"),
                        Updates.set("left_at", System.currentTimeMillis())
                )
        );
    }

    // Chat Room
    public void saveMessage(
            ObjectId conversationId,
            String senderId,
            String content
    ) {
        Document doc = new Document()
                .append("conversation_id", conversationId)
                .append("sender_id", senderId)
                .append("content", content)
                .append("created_at", Date.from(Instant.now()));

        messages.insertOne(doc);
    }
    public ObjectId getConversationIdByRoomId(ObjectId roomId) {
        Document room = rooms.find(
                new Document("_id", roomId)
        ).first();

        if (room == null) return null;

        return room.getObjectId("conversation_id");
    }
    public List<Document> getMessagesByConversationId(ObjectId conversationId) {
        return messages.find(
                        Filters.eq("conversation_id", conversationId)
                ).sort(Sorts.ascending("created_at"))
                .into(new ArrayList<>());
    }


    public boolean canControl(ObjectId roomId, ObjectId userCurrentId) {
        Document participant = meeting_participants.find(
                Filters.and(
                        Filters.eq("room_id", roomId),
                        Filters.eq("user_id", userCurrentId),
                        Filters.eq("status", "joined"),
                        Filters.in("role", "host", "admin")
                )
        ).first();

        return participant != null;
    }


    public void updateMicStatus(ObjectId roomId, ObjectId targetUserId) {
        // Tim nguoi tham gia trong cuoc hop
        Document participant = meeting_participants.find(
                Filters.and(
                        Filters.eq("room_id", roomId),
                        Filters.eq("user_id", targetUserId),
                        Filters.eq("status", "joined") // Chi thao tac voi nguoi con trong cuoc hop
                )
        ).first();
        if (participant == null) {
            return;
        }

        // Lay trang thai mic hien tai cua targer
        boolean isMuted = Boolean.TRUE.equals(participant.getBoolean("is_muted"));

        // Update trang thai mic
        meeting_participants.updateOne(Filters.eq("_id", participant.getObjectId("_id")), Updates.set("is_muted", !isMuted));
    }

    public void updateCameraStatus(ObjectId roomId, ObjectId targetUserId) {
        // Tim nguoi tham gia torng cuoc hop
        Document participant = meeting_participants.find(
                Filters.and(
                        Filters.eq("room_id", roomId),
                        Filters.eq("user_id", targetUserId),
                        Filters.eq("status", "joined")
                )
        ).first();

        if (participant == null) {
            return;
        }

        // Lay trang thai camera hien tai cua target user
        Boolean isCameraOn = Boolean.TRUE.equals(participant.getBoolean("is_camera_on"));

        // update trang thai camera
        meeting_participants.updateOne(Filters.eq("_id", participant.getObjectId("_id")), Updates.set("is_camera_on", !isCameraOn));
    }

}
