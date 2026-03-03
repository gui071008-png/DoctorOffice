package model;

public class Doctor extends User {

    private static final long serialVersionUID = 1L;

    public Doctor(int userId, String name, String email, String passwordHash) {
        this(userId, name, email, passwordHash, null);
    }

    public Doctor(int userId, String name, String email, String passwordHash, String plainPassword) {
        super(userId, name, email, passwordHash, plainPassword, "DOCTOR");
    }
}
