package main.Server.Model;

import org.bson.types.ObjectId;

public class Meeting_participant {
    private ObjectId participants_id;
    private String role;
    private boolean is_muted;
    private boolean is_camera_on;
    private long joined_at;
    private long left_at;
    private String status;
    private String user_id;
    private ObjectId room_id;
}

