package util;

import model.PatientRecord;
import model.Prescription;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class PdfExportUtil {

    private PdfExportUtil() {}

    /**
     * Exports a prescription as a plain-text .txt file (PDF library not included by default).
     * Swap this for iText or Apache PDFBox if a true PDF is required.
     */
    public static String exportPrescriptionAsText(PatientRecord record, Prescription prescription, String outputPath) {
        String path = outputPath + "/RX_" + record.getLastName() + "_" + LocalDate.now() + ".txt";
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("========================================\n");
            writer.write("           MEDICLINIC — PRESCRIPTION\n");
            writer.write("========================================\n\n");
            writer.write("Patient:    " + record.getFullName() + "\n");
            writer.write("DOB:        " + (record.getDateOfBirth() != null ? record.getDateOfBirth() : "N/A") + "\n");
            writer.write("Date:       " + prescription.getDateIssued() + "\n\n");
            writer.write("Diagnosis:  " + prescription.getDiagnosis() + "\n\n");
            writer.write("Medication: " + prescription.getMedication() + "\n");
            writer.write("Dosage:     " + prescription.getDosage() + "\n\n");
            writer.write("========================================\n");
            writer.write("Issued by: Dr. Andre\n");
            writer.write("MediClinic Private Practice\n");
            writer.write("========================================\n");
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
