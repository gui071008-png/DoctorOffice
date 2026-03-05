package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import model.PatientRecord;
import model.Prescription;
import service.FollowUpService;
import service.PatientService;
import service.PrescriptionService;
import util.PdfExportUtil;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class PatientRecordView {

    private final PatientRecord record;
    private final PatientService patientService = new PatientService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final FollowUpService followUpService = new FollowUpService();

    public PatientRecordView(PatientRecord record) {
        this.record = record;
    }

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setStyle(Theme.baseStyle());

        HBox header = Theme.buildHeader("Patient Record — " + record.getFullName());
        addNavButtons(header);
        root.setTop(header);

        TabPane tabs = buildTabs();
        root.setCenter(tabs);

        return new Scene(root, 900, 650);

    }

    private void addNavButtons(HBox header) {
        Button back = new Button("← Search");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;");
        back.setOnAction(e -> SceneManager.getInstance().showSearchPatient());
        header.getChildren().add(back);
    }

    private TabPane buildTabs() {
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-background-color: white;");
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab profileTab = new Tab("Profile", buildProfileTab());
        Tab diagnosisTab = new Tab("Diagnosis & Prescription", buildDiagnosisTab());
        Tab followUpTab = new Tab("Follow-Up Date", buildFollowUpTab());
        Tab prescriptionsTab = new Tab("Prescription History", buildPrescriptionHistoryTab());
        Tab dangerTab = new Tab("⚠ Danger Zone", buildDangerTab());

        tabs.getTabs().addAll(profileTab, diagnosisTab, followUpTab, prescriptionsTab, dangerTab);
        return tabs;
    }

    private ScrollPane buildProfileTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(24));

        Label title = Theme.subheading("Patient Information");

        TextField firstNameField = styledField(record.getFirstName());
        TextField lastNameField = styledField(record.getLastName());
        TextField emailField = styledField(record.getEmail());
        TextField passwordField = styledField(record.getPassword());
        passwordField.setEditable(false);
        DatePicker dobPicker = new DatePicker(record.getDateOfBirth());
        dobPicker.setStyle(Theme.inputStyle());
        TextField heightField = styledField(String.valueOf(record.getHeight()));
        TextField weightField = styledField(String.valueOf(record.getWeight()));
        TextArea symptomsArea = styledArea(record.getSymptoms());

        Label statusLabel = Theme.errorLabel();
        statusLabel.setVisible(false);

        Button saveBtn = new Button("Save Changes");
        Theme.applyPrimary(saveBtn);

        saveBtn.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                showStatus(statusLabel, "First name, last name, and email are required.", false);
                return;
            }

            if (!util.ValidationUtil.isValidEmail(email)) {
                showStatus(statusLabel, "Please enter a valid email address.", false);
                return;
            }

            double height = 0, weight = 0;
            try {
                if (!heightField.getText().trim().isEmpty()) height = Double.parseDouble(heightField.getText().trim());
                if (!weightField.getText().trim().isEmpty()) weight = Double.parseDouble(weightField.getText().trim());
            } catch (NumberFormatException ex) {
                showStatus(statusLabel, "Height and weight must be numbers.", false);
                return;
            }

            record.setFirstName(firstName);
            record.setLastName(lastName);
            record.setEmail(email);
            record.setDateOfBirth(dobPicker.getValue());
            record.setHeight(height);
            record.setWeight(weight);
            record.setSymptoms(symptomsArea.getText().trim());

            boolean saved = patientService.saveRecord(record);
            showStatus(
                    statusLabel,
                    saved ? "Record saved successfully." : "Unable to save record. Email may already exist.",
                    saved
            );
        });

        form.getChildren().addAll(
                title,
                row("First Name", firstNameField), row("Last Name", lastNameField),
                row("Email", emailField), row("Password", passwordField),
                row("Date of Birth", dobPicker),
                row("Height (cm)", heightField), row("Weight (kg)", weightField),
                row("Symptoms", symptomsArea),
                statusLabel, saveBtn
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane buildDiagnosisTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(24));

        Label title = Theme.subheading("Add Diagnosis & Prescription");

        TextArea diagnosisArea = styledArea(record.getDiagnosis());
        TextField medicationField = styledField("");
        TextField dosageField = styledField("");

        Label statusLabel = Theme.errorLabel();
        statusLabel.setVisible(false);

        Button saveBtn = new Button("Save Prescription");
        Theme.applyPrimary(saveBtn);

        Button exportBtn = new Button("Export as Text File");
        Theme.applySecondary(exportBtn);
        exportBtn.setDisable(true);

        final Prescription[] lastPrescription = {null};

        saveBtn.setOnAction(e -> {
            String diagnosis = diagnosisArea.getText().trim();
            String medication = medicationField.getText().trim();
            String dosage = dosageField.getText().trim();

            if (diagnosis.isEmpty() || medication.isEmpty() || dosage.isEmpty()) {
                showStatus(statusLabel, "Diagnosis, medication, and dosage are required.", false);
                return;
            }

            record.setDiagnosis(diagnosis);
            boolean recordSaved = patientService.saveRecord(record);
            if (!recordSaved) {
                showStatus(statusLabel, "Unable to save diagnosis. Email may already exist.", false);
                return;
            }

            Prescription prescription = prescriptionService.createPrescription(
                    record.getRecordId(), diagnosis, medication, dosage
            );
            lastPrescription[0] = prescription;

            showStatus(statusLabel, "Prescription saved.", true);
            exportBtn.setDisable(false);
        });

        exportBtn.setOnAction(e -> {
            if (lastPrescription[0] == null) return;
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Export Folder");
            File dir = chooser.showDialog(null);
            if (dir != null) {
                String path = PdfExportUtil.exportPrescriptionAsText(record, lastPrescription[0], dir.getAbsolutePath());
                if (path != null) {
                    showStatus(statusLabel, "Exported to: " + path, true);
                } else {
                    showStatus(statusLabel, "Export failed.", false);
                }
            }
        });

        HBox btnRow = new HBox(10, saveBtn, exportBtn);

        form.getChildren().addAll(
                title,
                row("Diagnosis", diagnosisArea),
                row("Medication", medicationField),
                row("Dosage", dosageField),
                statusLabel, btnRow
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane buildFollowUpTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(24));

        Label title = Theme.subheading("Set Follow-Up Date");

        DatePicker datePicker = new DatePicker(record.getNextFollowUpDate());
        datePicker.setStyle(Theme.inputStyle());

        Label note = new Label("An alert will be generated 14 days before the scheduled follow-up.");
        note.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");

        Label statusLabel = Theme.errorLabel();
        statusLabel.setVisible(false);

        Button saveBtn = new Button("Save Follow-Up Date");
        Theme.applyPrimary(saveBtn);

        saveBtn.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            if (date == null) {
                showStatus(statusLabel, "Please select a date.", false);
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                showStatus(statusLabel, "Follow-up date must be in the future.", false);
                return;
            }
            followUpService.setFollowUpDate(record, date);
            showStatus(statusLabel, "Follow-up date saved.", true);
        });

        form.getChildren().addAll(title, row("Follow-Up Date", datePicker), note, statusLabel, saveBtn);

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    @SuppressWarnings("unchecked")
    private ScrollPane buildPrescriptionHistoryTab() {
        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = Theme.subheading("Prescription History");

        List<Prescription> prescriptions = prescriptionService.getPrescriptionsForRecord(record.getRecordId());
        ObservableList<Prescription> data = FXCollections.observableArrayList(prescriptions);

        TableView<Prescription> tv = new TableView<>(data);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No prescriptions yet."));

        TableColumn<Prescription, String> diagCol = new TableColumn<>("Diagnosis");
        diagCol.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        TableColumn<Prescription, String> medCol = new TableColumn<>("Medication");
        medCol.setCellValueFactory(new PropertyValueFactory<>("medication"));

        TableColumn<Prescription, String> dosCol = new TableColumn<>("Dosage");
        dosCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));

        TableColumn<Prescription, LocalDate> dateCol = new TableColumn<>("Date Issued");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateIssued"));

        tv.getColumns().addAll(diagCol, medCol, dosCol, dateCol);
        VBox.setVgrow(tv, Priority.ALWAYS);

        content.getChildren().addAll(title, tv);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane buildDangerTab() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = Theme.subheading("Danger Zone");
        Label warning = new Label("Deleting a patient record is permanent and cannot be undone.");
        warning.setStyle("-fx-text-fill: " + Theme.DANGER + "; -fx-font-size: 13px;");

        Button deleteBtn = new Button("Delete Patient Record");
        Theme.applyDanger(deleteBtn);

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete patient: " + record.getFullName() + "?");
            confirm.setContentText("This will permanently remove the patient's account and all associated records.");
            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    patientService.deletePatient(record.getRecordId());
                    SceneManager.getInstance().showSearchPatient();
                }
            });
        });

        content.getChildren().addAll(title, warning, deleteBtn);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private HBox row(String labelText, javafx.scene.Node field) {
        Label label = Theme.fieldLabel(labelText);
        label.setMinWidth(140);
        HBox row = new HBox(10, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        if (field instanceof Region) {
            ((Region) field).setMaxWidth(Double.MAX_VALUE);
        }
        return row;
    }

    private TextField styledField(String value) {
        TextField tf = new TextField(value != null ? value : "");
        tf.setStyle(Theme.inputStyle());
        return tf;
    }

    private TextArea styledArea(String value) {
        TextArea ta = new TextArea(value != null ? value : "");
        ta.setStyle(Theme.inputStyle());
        ta.setPrefRowCount(3);
        ta.setWrapText(true);
        return ta;
    }

    private void showStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (success ? Theme.SUCCESS : Theme.DANGER) + ";");
        label.setVisible(true);
    }
}
