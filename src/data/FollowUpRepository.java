package data;

import model.FollowUp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FollowUpRepository {

    private final DatabaseManager databaseManager;

    public FollowUpRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void createAlert(int recordId, String patientName, LocalDate followUpDate) {
        FollowUp followUp = new FollowUp(
                databaseManager.nextFollowUpId(),
                recordId,
                patientName,
                followUpDate,
                false
        );
        databaseManager.putFollowUp(followUp);
        databaseManager.save();
    }

    public boolean alertExistsForRecord(int recordId) {
        for (FollowUp followUp : databaseManager.getFollowUps()) {
            if (followUp.getRecordId() == recordId && !followUp.isRead()) {
                return true;
            }
        }
        return false;
    }

    public List<FollowUp> findUnread() {
        List<FollowUp> list = new ArrayList<FollowUp>();
        for (FollowUp followUp : databaseManager.getFollowUps()) {
            if (!followUp.isRead()) {
                list.add(copy(followUp));
            }
        }
        Collections.sort(list, Comparator.comparing(FollowUp::getFollowUpDate));
        return list;
    }

    public void markAsRead(int followUpId) {
        for (FollowUp followUp : databaseManager.getFollowUps()) {
            if (followUp.getFollowUpId() == followUpId) {
                followUp.setRead(true);
                databaseManager.putFollowUp(followUp);
                databaseManager.save();
                return;
            }
        }
    }

    public FollowUp findByRecordId(int recordId) {
        FollowUp selected = null;
        for (FollowUp followUp : databaseManager.getFollowUps()) {
            if (followUp.getRecordId() == recordId && !followUp.isRead()) {
                if (selected == null || followUp.getFollowUpDate().isBefore(selected.getFollowUpDate())) {
                    selected = followUp;
                }
            }
        }
        return selected == null ? null : copy(selected);
    }

    private FollowUp copy(FollowUp followUp) {
        return new FollowUp(
                followUp.getFollowUpId(),
                followUp.getRecordId(),
                followUp.getPatientName(),
                followUp.getFollowUpDate(),
                followUp.isRead()
        );
    }
}
