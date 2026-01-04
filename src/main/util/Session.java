package main.util;

public class Session {
    private static Session instance;
    private String email;
    private String userIdHex;
    private String fullName;


    private Session() {}

    public void setUser(String email, String userIdHex, String fullName) {
        this.email = email;
        this.userIdHex = userIdHex;
        this.fullName = fullName;
    }
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }


    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public String getUserIdHex() {
        return userIdHex;
    }
    public String getFullName() { return fullName; }

    public boolean isLoggedIn() {
        return email != null && userIdHex != null;
    }
}