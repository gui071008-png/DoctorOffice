package data;

import model.Prescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PrescriptionRepository {

    private final DatabaseManager databaseManager;

    public PrescriptionRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public int save(Prescription prescription) {
        int prescriptionId = databaseManager.nextPrescriptionId();
        Prescription stored = new Prescription(
                prescriptionId,
                prescription.getRecordId(),
                prescription.getDiagnosis(),
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getDateIssued()
        );
        databaseManager.putPrescription(stored);
        databaseManager.save();
        return prescriptionId;
    }

    public List<Prescription> findByRecordId(int recordId) {
        List<Prescription> list = new ArrayList<Prescription>();
        for (Prescription prescription : databaseManager.getPrescriptions()) {
            if (prescription.getRecordId() == recordId) {
                list.add(copy(prescription));
            }
        }
        Collections.sort(list, Comparator.comparing(Prescription::getDateIssued).reversed());
        return list;
    }

    private Prescription copy(Prescription prescription) {
        return new Prescription(
                prescription.getPrescriptionId(),
                prescription.getRecordId(),
                prescription.getDiagnosis(),
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getDateIssued()
        );
    }
}
