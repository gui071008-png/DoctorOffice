package service;

import data.PatientRecordRepository;
import data.UserRepository;
import model.PatientRecord;

import java.util.List;

public class PatientService {

    private final PatientRecordRepository recordRepository;
    private final UserRepository userRepository;

    public PatientService() {
        this.recordRepository = new PatientRecordRepository();
        this.userRepository = new UserRepository();
    }

    public int createPatient(String firstName, String lastName, String email, String password) {
        if (userRepository.emailExists(email)) return -1;
        String fullName = firstName + " " + lastName;
        int userId = userRepository.createPatientUser(fullName, email, password);
        if (userId == -1) return -1;
        return recordRepository.createRecord(userId, firstName, lastName, email);
    }

    public PatientRecord getRecord(int recordId) {
        return recordRepository.findById(recordId);
    }

    public List<PatientRecord> searchPatients(String query) {
        return recordRepository.searchByName(query);
    }

    public List<PatientRecord> getAllPatients() {
        return recordRepository.findAll();
    }

    public boolean saveRecord(PatientRecord record) {
        if (userRepository.emailExistsForAnotherRecord(record.getRecordId(), record.getEmail())) {
            return false;
        }

        boolean recordUpdated = recordRepository.updateRecord(record);
        if (!recordUpdated) {
            return false;
        }

        return userRepository.updatePatientIdentity(record.getRecordId(), record.getFullName(), record.getEmail());
    }

    public void deletePatient(int recordId) {
        recordRepository.deleteRecordAndUser(recordId);
    }
}
