package service;

import data.FollowUpRepository;
import data.PatientRecordRepository;
import model.FollowUp;
import model.PatientRecord;

import java.time.LocalDate;
import java.util.List;

public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final PatientRecordRepository recordRepository;

    public FollowUpService() {
        this.followUpRepository = new FollowUpRepository();
        this.recordRepository = new PatientRecordRepository();
    }

    public void setFollowUpDate(PatientRecord record, LocalDate date) {
        record.setNextFollowUpDate(date);
        record.setAlertSent(false);
        if (recordRepository.updateRecord(record)) {
            checkAndGenerateAlert(record);
        }
    }

    public void checkAndGenerateAlerts() {
        List<PatientRecord> all = recordRepository.findAll();
        for (PatientRecord record : all) {
            checkAndGenerateAlert(record);
        }
    }

    private void checkAndGenerateAlert(PatientRecord record) {
        if (record.getNextFollowUpDate() == null) return;
        LocalDate today = LocalDate.now();
        LocalDate alertThreshold = record.getNextFollowUpDate().minusDays(14);
        if (!today.isBefore(alertThreshold) && !record.isAlertSent()) {
            FollowUp existingAlert = followUpRepository.findByRecordId(record.getRecordId());
            if (existingAlert != null) {
                boolean sameDate = record.getNextFollowUpDate().equals(existingAlert.getFollowUpDate());
                boolean samePatient = record.getFullName().equals(existingAlert.getPatientName());
                if (sameDate && samePatient) {
                    record.setAlertSent(true);
                    recordRepository.updateRecord(record);
                    return;
                }

                followUpRepository.markAsRead(existingAlert.getFollowUpId());
            }

            followUpRepository.createAlert(
                    record.getRecordId(),
                    record.getFullName(),
                    record.getNextFollowUpDate()
            );
            record.setAlertSent(true);
            recordRepository.updateRecord(record);
        }
    }

    public List<FollowUp> getUnreadAlerts() {
        return followUpRepository.findUnread();
    }

    public void markAlertRead(int followUpId) {
        followUpRepository.markAsRead(followUpId);
    }

    public FollowUp getAlertForRecord(int recordId) {
        return followUpRepository.findByRecordId(recordId);
    }
}
