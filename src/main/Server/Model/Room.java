package main.Server.Model; // ENTITIES ÁNH XẠ CSDL

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Date;

public class Room {
    private ObjectId roomId;
    private String title;
    private String meeting_code;
    private String passcode;
    private String status;
    private ObjectId conservationId;
    private Date created_at;

    public Room(String title, String meeting_code, String passcode, String status, Date created_at, ObjectId conservationId) {
        this.title = title;
        this.meeting_code = meeting_code;
        this.passcode = passcode;
        this.status = status;
        this.created_at = created_at;
        this.conservationId = conservationId;
    }

    public ObjectId getRoomId() {
        return roomId;
    }

    public void setRoomId(ObjectId roomId) {
        this.roomId = roomId;
    }

    public String getMeeting_code() {
        return meeting_code;
    }

    public void setMeeting_code(String meeting_code) {
        this.meeting_code = meeting_code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreate_at() {
        return created_at;
    }

    public void setCreate_at(Date create_at) {
        this.created_at = create_at;
    }

    public ObjectId getConservationId() {
        return conservationId;
    }

    public void setConservationId(ObjectId conservationId) {
        this.conservationId = conservationId;
    }
}
