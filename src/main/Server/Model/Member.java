package main.Server.Model; // ENTITIES ÁNH XẠ CSDL

import org.bson.types.ObjectId;

public class Member {
    private ObjectId memberId;
    private ObjectId conversation_id;
    private String role; // host || member || leader (chat group)

    public Member() {
    }

    public Member(ObjectId conversation_id, String role, boolean is_muted, boolean is_camera_on) {
        this.conversation_id = conversation_id;
        this.role = role;    }

    public ObjectId getMemberId() {
        return memberId;
    }

    public void setMemberId(ObjectId memberId) {
        this.memberId = memberId;
    }

    public ObjectId getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(ObjectId conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    }