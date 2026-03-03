package service;

import data.PrescriptionRepository;
import model.Prescription;

import java.time.LocalDate;
import java.util.List;

public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService() {
        this.prescriptionRepository = new PrescriptionRepository();
    }

    public Prescription createPrescription(int recordId, String diagnosis, String medication, String dosage) {
        Prescription prescription = new Prescription(0, recordId, diagnosis, medication, dosage, LocalDate.now());
        int id = prescriptionRepository.save(prescription);
        return new Prescription(id, recordId, diagnosis, medication, dosage, LocalDate.now());
    }

    public List<Prescription> getPrescriptionsForRecord(int recordId) {
        return prescriptionRepository.findByRecordId(recordId);
    }
}
