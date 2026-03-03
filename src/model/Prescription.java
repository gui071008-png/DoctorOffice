package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    private int prescriptionId;
    private int recordId;
    private String diagnosis;
    private String medication;
    private String dosage;
    private LocalDate dateIssued;

    public Prescription(int prescriptionId, int recordId, String diagnosis,
                        String medication, String dosage, LocalDate dateIssued) {
        this.prescriptionId = prescriptionId;
        this.recordId = recordId;
        this.diagnosis = diagnosis;
        this.medication = medication;
        this.dosage = dosage;
        this.dateIssued = dateIssued;
    }

    public int getPrescriptionId() { return prescriptionId; }
    public int getRecordId() { return recordId; }
    public String getDiagnosis() { return diagnosis; }
    public String getMedication() { return medication; }
    public String getDosage() { return dosage; }
    public LocalDate getDateIssued() { return dateIssued; }
}
