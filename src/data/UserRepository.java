package data;

import model.Doctor;
import model.Patient;
import model.PatientRecord;
import model.User;
import util.SecurityUtil;

public class UserRepository {

    private final DatabaseManager databaseManager;

    public UserRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public User findByEmailAndRole(String email, String plainPassword, String role) {
        for (User user : databaseManager.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getRole().equals(role)) {
                if (SecurityUtil.matchesStoredPassword(plainPassword, user.getPasswordHash())) {
                    return copyUser(user);
                }
            }
        }
        return null;
    }

    public boolean emailExists(String email) {
        for (User user : databaseManager.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public int createPatientUser(String name, String email, String plainPassword) {
        int userId = databaseManager.nextUserId();
        Patient patient = new Patient(
                userId,
                name,
                email,
                SecurityUtil.createStoredPassword(plainPassword),
                -1
        );
        databaseManager.putUser(patient);
        databaseManager.save();
        return userId;
    }

    public boolean emailExistsForAnotherRecord(int recordId, String email) {
        PatientRecord record = new PatientRecordRepository().findById(recordId);
        int currentUserId = record == null ? -1 : record.getUserId();

        for (User user : databaseManager.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getUserId() != currentUserId) {
                return true;
            }
        }
        return false;
    }

    public boolean updatePatientIdentity(int recordId, String name, String email) {
        PatientRecord record = new PatientRecordRepository().findById(recordId);
        if (record == null) {
            return false;
        }

        User existing = databaseManager.getUser(record.getUserId());
        if (existing == null) {
            return false;
        }

        User updated;
        if (existing instanceof Doctor) {
            updated = new Doctor(existing.getUserId(), name, email, existing.getPasswordHash());
        } else {
            int patientRecordId = ((Patient) existing).getPatientRecordId();
            if (patientRecordId <= 0) {
                patientRecordId = recordId;
            }
            updated = new Patient(existing.getUserId(), name, email, existing.getPasswordHash(), patientRecordId);
        }

        databaseManager.putUser(updated);
        databaseManager.save();
        return true;
    }

    private User copyUser(User user) {
        if (user instanceof Doctor) {
            return new Doctor(user.getUserId(), user.getName(), user.getEmail(), user.getPasswordHash());
        }

        Patient patient = (Patient) user;
        int recordId = patient.getPatientRecordId();
        if (recordId <= 0) {
            recordId = new PatientRecordRepository().getRecordIdByUserId(user.getUserId());
        }
        return new Patient(user.getUserId(), user.getName(), user.getEmail(), user.getPasswordHash(), recordId);
    }
}
