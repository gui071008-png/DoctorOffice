package model;

public class Doctor extends User {

    private static final long serialVersionUID = 1L;

    public Doctor(int userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, "DOCTOR");
    }
}
