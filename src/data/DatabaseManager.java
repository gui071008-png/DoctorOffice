package data;

import model.Doctor;
import model.FollowUp;
import model.PatientRecord;
import model.Prescription;
import model.User;
import util.SecurityUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseManager {

    private static final DatabaseManager INSTANCE = new DatabaseManager();
    private static final String DATA_FILE = "clinic-data.ser";

    private DataStore store;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() {
        if (store != null) {
            return;
        }

        store = loadStore();
        seedDoctorIfNeeded();
        save();
    }

    synchronized int nextUserId() {
        return store.nextUserId++;
    }

    synchronized int nextRecordId() {
        return store.nextRecordId++;
    }

    synchronized int nextPrescriptionId() {
        return store.nextPrescriptionId++;
    }

    synchronized int nextFollowUpId() {
        return store.nextFollowUpId++;
    }

    synchronized Collection<User> getUsers() {
        return new ArrayList<User>(store.users.values());
    }

    synchronized Collection<PatientRecord> getPatientRecords() {
        return new ArrayList<PatientRecord>(store.patientRecords.values());
    }

    synchronized Collection<Prescription> getPrescriptions() {
        return new ArrayList<Prescription>(store.prescriptions.values());
    }

    synchronized Collection<FollowUp> getFollowUps() {
        return new ArrayList<FollowUp>(store.followUps.values());
    }

    synchronized User getUser(int userId) {
        return store.users.get(userId);
    }

    synchronized PatientRecord getPatientRecord(int recordId) {
        return store.patientRecords.get(recordId);
    }

    synchronized void putUser(User user) {
        store.users.put(user.getUserId(), user);
    }

    synchronized void putPatientRecord(PatientRecord record) {
        store.patientRecords.put(record.getRecordId(), record);
    }

    synchronized void putPrescription(Prescription prescription) {
        store.prescriptions.put(prescription.getPrescriptionId(), prescription);
    }

    synchronized void putFollowUp(FollowUp followUp) {
        store.followUps.put(followUp.getFollowUpId(), followUp);
    }

    synchronized void removeUser(int userId) {
        store.users.remove(userId);
    }

    synchronized void removePatientRecord(int recordId) {
        store.patientRecords.remove(recordId);
    }

    synchronized void removePrescription(int prescriptionId) {
        store.prescriptions.remove(prescriptionId);
    }

    synchronized void removeFollowUp(int followUpId) {
        store.followUps.remove(followUpId);
    }

    synchronized void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            output.writeObject(store);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data: " + e.getMessage(), e);
        }
    }

    private DataStore loadStore() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object loaded = input.readObject();
            if (loaded instanceof DataStore) {
                return (DataStore) loaded;
            }
        } catch (IOException ignored) {
            // Start with a fresh data file when none exists yet.
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Stored data is incompatible with this version.", e);
        }
        return new DataStore();
    }

    private void seedDoctorIfNeeded() {
        for (User user : store.users.values()) {
            if ("DOCTOR".equals(user.getRole())) {
                return;
            }
        }

        Doctor doctor = new Doctor(
                nextUserId(),
                "Dr. Andre",
                "doctor",
                SecurityUtil.createStoredPassword("doctor123")
        );
        putUser(doctor);
    }

    private static class DataStore implements Serializable {

        private static final long serialVersionUID = 1L;

        private int nextUserId = 1;
        private int nextRecordId = 1;
        private int nextPrescriptionId = 1;
        private int nextFollowUpId = 1;
        private final Map<Integer, User> users = new LinkedHashMap<Integer, User>();
        private final Map<Integer, PatientRecord> patientRecords = new LinkedHashMap<Integer, PatientRecord>();
        private final Map<Integer, Prescription> prescriptions = new LinkedHashMap<Integer, Prescription>();
        private final Map<Integer, FollowUp> followUps = new LinkedHashMap<Integer, FollowUp>();
    }
}
