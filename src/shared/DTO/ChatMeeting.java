package shared.DTO;

import java.io.Serializable;

public class ChatMeeting implements Serializable {

    private final String roomId;
    private final String sender;
    private final String content;
    private final long timestamp;

    public ChatMeeting(String roomId, String sender, String content) {
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getRoomId() { return roomId; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
