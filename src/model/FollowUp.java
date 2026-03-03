package model;

import java.io.Serializable;
import java.time.LocalDate;

public class FollowUp implements Serializable {

    private static final long serialVersionUID = 1L;

    private int followUpId;
    private int recordId;
    private String patientName;
    private LocalDate followUpDate;
    private boolean read;

    public FollowUp(int followUpId, int recordId, String patientName, LocalDate followUpDate, boolean read) {
        this.followUpId = followUpId;
        this.recordId = recordId;
        this.patientName = patientName;
        this.followUpDate = followUpDate;
        this.read = read;
    }

    public int getFollowUpId() { return followUpId; }
    public int getRecordId() { return recordId; }
    public String getPatientName() { return patientName; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
