package model;

import java.io.Serializable;
import java.time.LocalDate;

public class PatientRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private int recordId;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private double height;
    private double weight;
    private String symptoms;
    private String diagnosis;
    private LocalDate nextFollowUpDate;
    private boolean alertSent;

    public PatientRecord(int recordId, String firstName, String lastName, String email,
                         LocalDate dateOfBirth, double height, double weight,
                         String symptoms, String diagnosis,
                         LocalDate nextFollowUpDate, boolean alertSent) {
        this(recordId, 0, firstName, lastName, email, dateOfBirth, height, weight, symptoms, diagnosis,
                nextFollowUpDate, alertSent);
    }

    public PatientRecord(int recordId, int userId, String firstName, String lastName, String email,
                         LocalDate dateOfBirth, double height, double weight,
                         String symptoms, String diagnosis,
                         LocalDate nextFollowUpDate, boolean alertSent) {
        this.recordId = recordId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.nextFollowUpDate = nextFollowUpDate;
        this.alertSent = alertSent;
    }

    public int getRecordId() { return recordId; }
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail() { return email; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public String getSymptoms() { return symptoms; }
    public String getDiagnosis() { return diagnosis; }
    public LocalDate getNextFollowUpDate() { return nextFollowUpDate; }
    public boolean isAlertSent() { return alertSent; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setHeight(double height) { this.height = height; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    public void setAlertSent(boolean alertSent) { this.alertSent = alertSent; }
}
