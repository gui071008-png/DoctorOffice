package data;

import model.Patient;
import model.PatientRecord;
import model.Prescription;
import model.FollowUp;
import model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PatientRecordRepository {

    private final DatabaseManager databaseManager;

    public PatientRecordRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public int createRecord(int userId, String firstName, String lastName, String email) {
        int recordId = databaseManager.nextRecordId();
        PatientRecord record = new PatientRecord(
                recordId,
                userId,
                firstName,
                lastName,
                email,
                null,
                null,
                0,
                0,
                "",
                "",
                null,
                false
        );

        databaseManager.putPatientRecord(record);

        User user = databaseManager.getUser(userId);
        if (user instanceof Patient) {
            Patient patient = new Patient(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getPlainPassword(),
                    recordId
            );
            databaseManager.putUser(patient);
        }

        databaseManager.save();
        return recordId;
    }

    public PatientRecord findById(int recordId) {
        PatientRecord record = databaseManager.getPatientRecord(recordId);
        return record == null ? null : copy(record);
    }

    public List<PatientRecord> searchByName(String query) {
        List<PatientRecord> results = new ArrayList<PatientRecord>();
        String normalized = query == null ? "" : query.trim().toLowerCase();
        for (PatientRecord record : databaseManager.getPatientRecords()) {
            if (record.getFirstName().toLowerCase().contains(normalized)
                    || record.getLastName().toLowerCase().contains(normalized)) {
                results.add(copy(record));
            }
        }
        sortByName(results);
        return results;
    }

    public List<PatientRecord> findAll() {
        List<PatientRecord> results = new ArrayList<PatientRecord>();
        for (PatientRecord record : databaseManager.getPatientRecords()) {
            results.add(copy(record));
        }
        sortByName(results);
        return results;
    }

    public boolean updateRecord(PatientRecord record) {
        PatientRecord stored = databaseManager.getPatientRecord(record.getRecordId());
        if (stored == null) {
            return false;
        }

        PatientRecord updated = copy(record);
        if (updated.getUserId() == 0) {
            updated.setUserId(stored.getUserId());
        }

        databaseManager.putPatientRecord(updated);
        databaseManager.save();
        return true;
    }

    public void deleteRecord(int recordId) {
        databaseManager.removePatientRecord(recordId);
        databaseManager.save();
    }

    public void deleteRecordAndUser(int recordId) {
        PatientRecord record = databaseManager.getPatientRecord(recordId);
        if (record == null) {
            return;
        }

        List<Integer> prescriptionIds = new ArrayList<Integer>();
        for (Prescription prescription : databaseManager.getPrescriptions()) {
            if (prescription.getRecordId() == recordId) {
                prescriptionIds.add(prescription.getPrescriptionId());
            }
        }
        for (Integer prescriptionId : prescriptionIds) {
            databaseManager.removePrescription(prescriptionId);
        }

        List<Integer> followUpIds = new ArrayList<Integer>();
        for (FollowUp followUp : databaseManager.getFollowUps()) {
            if (followUp.getRecordId() == recordId) {
                followUpIds.add(followUp.getFollowUpId());
            }
        }
        for (Integer followUpId : followUpIds) {
            databaseManager.removeFollowUp(followUpId);
        }

        databaseManager.removePatientRecord(recordId);
        databaseManager.removeUser(record.getUserId());
        databaseManager.save();
    }

    public int getRecordIdByUserId(int userId) {
        for (PatientRecord record : databaseManager.getPatientRecords()) {
            if (record.getUserId() == userId) {
                return record.getRecordId();
            }
        }
        return -1;
    }

    private PatientRecord copy(PatientRecord record) {
        return new PatientRecord(
                record.getRecordId(),
                record.getUserId(),
                record.getFirstName(),
                record.getLastName(),
                record.getEmail(),
                record.getPassword(),
                record.getDateOfBirth(),
                record.getHeight(),
                record.getWeight(),
                record.getSymptoms(),
                record.getDiagnosis(),
                record.getNextFollowUpDate(),
                record.isAlertSent()
        );
    }

    private void sortByName(List<PatientRecord> records) {
        Collections.sort(records, Comparator
                .comparing(PatientRecord::getLastName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(PatientRecord::getFirstName, String.CASE_INSENSITIVE_ORDER));
    }
}
