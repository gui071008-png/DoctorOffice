package model;

import java.io.Serializable;

public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String plainPassword;
    private String role;

    public User(int userId, String name, String email, String passwordHash, String plainPassword, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.plainPassword = plainPassword;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getPlainPassword() { return plainPassword; }
    public String getRole() { return role; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPlainPassword(String plainPassword) { this.plainPassword = plainPassword; }
}
