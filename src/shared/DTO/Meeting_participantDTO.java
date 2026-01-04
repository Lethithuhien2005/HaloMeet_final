package shared.DTO;

import java.io.Serializable;

public class Meeting_participantDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String username;
    private String fullName;
    private String avatar;
    private String role;
    private boolean isMuted;
    private boolean isCameraOn;


    public Meeting_participantDTO() {}

    public Meeting_participantDTO(String userId, String username, String fullName, String avatar, String role, boolean isMuted, boolean isCameraOn) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.avatar = avatar;
        this.role = role;
        this.isMuted = isMuted;
        this.isCameraOn = isCameraOn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isCameraOn() {
        return isCameraOn;
    }

    public void setCameraOn(boolean cameraOn) {
        isCameraOn = cameraOn;
    }
}

