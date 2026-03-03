package model;

public class Patient extends User {

    private static final long serialVersionUID = 1L;

    private int patientRecordId;

    public Patient(int userId, String name, String email, String passwordHash, int patientRecordId) {
        this(userId, name, email, passwordHash, null, patientRecordId);
    }

    public Patient(int userId, String name, String email, String passwordHash, String plainPassword, int patientRecordId) {
        super(userId, name, email, passwordHash, plainPassword, "PATIENT");
        this.patientRecordId = patientRecordId;
    }

    public int getPatientRecordId() { return patientRecordId; }
    public void setPatientRecordId(int patientRecordId) { this.patientRecordId = patientRecordId; }
}
