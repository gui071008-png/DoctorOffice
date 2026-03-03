package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Patient;
import model.PatientRecord;
import model.User;
import service.FollowUpService;
import service.PatientService;
import util.ValidationUtil;

public class PatientPortalView {

    private final User user;
    private final PatientService patientService = new PatientService();
    private final FollowUpService followUpService = new FollowUpService();

    public PatientPortalView(User user) {
        this.user = user;
    }

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setStyle(Theme.baseStyle());

        HBox header = Theme.buildHeader("MediClinic — Patient Portal");
        Button logout = new Button("Logout");
        logout.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: white; -fx-border-radius: 4; -fx-padding: 4 12 4 12;");
        logout.setOnAction(e -> SceneManager.getInstance().showLogin());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(logout);
        root.setTop(header);

        if (!(user instanceof Patient)) {
            root.setCenter(new Label("Access error: not a patient account."));
            return new Scene(root, 900, 650);
        }
        Patient patient = (Patient) user;

        PatientRecord record = patientService.getRecord(patient.getPatientRecordId());
        if (record == null) {
            root.setCenter(new Label("No record found for your account."));
            return new Scene(root, 900, 650);
        }

        TabPane tabs = buildTabs(record);
        root.setCenter(tabs);

        return new Scene(root, 900, 650);
    }

    private TabPane buildTabs(PatientRecord record) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab infoTab = new Tab("My Information", buildInfoTab(record));
        Tab editTab = new Tab("Edit Contact Info", buildEditTab(record));
        Tab appointmentTab = new Tab("Next Appointment", buildAppointmentTab(record));

        tabs.getTabs().addAll(infoTab, editTab, appointmentTab);
        return tabs;
    }

    private ScrollPane buildInfoTab(PatientRecord record) {
        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = Theme.heading("My Personal Information");

        content.getChildren().addAll(
                title,
                infoRow("Full Name", record.getFullName()),
                infoRow("Email", record.getEmail()),
                infoRow("Date of Birth", record.getDateOfBirth() != null ? record.getDateOfBirth().toString() : "Not recorded"),
                infoRow("Height (cm)", record.getHeight() > 0 ? String.valueOf(record.getHeight()) : "Not recorded"),
                infoRow("Weight (kg)", record.getWeight() > 0 ? String.valueOf(record.getWeight()) : "Not recorded"),
                infoRow("Symptoms", record.getSymptoms() != null && !record.getSymptoms().isEmpty() ? record.getSymptoms() : "None recorded")
        );

        Label note = new Label("Medical history, diagnosis, and prescriptions are managed by your doctor.");
        note.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-wrap-text: true;");
        content.getChildren().add(note);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane buildEditTab(PatientRecord record) {
        VBox form = new VBox(14);
        form.setPadding(new Insets(24));

        Label title = Theme.heading("Edit Contact Information");
        Label restriction = new Label("You may only update your contact email.");
        restriction.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");

        TextField emailField = new TextField(record.getEmail());
        emailField.setStyle(Theme.inputStyle());

        Label statusLabel = Theme.errorLabel();
        statusLabel.setVisible(false);

        Button saveBtn = new Button("Save");
        Theme.applyPrimary(saveBtn);

        saveBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (!ValidationUtil.isValidEmail(email)) {
                statusLabel.setText("Please enter a valid email address.");
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.DANGER + ";");
                statusLabel.setVisible(true);
                return;
            }
            record.setEmail(email);
            boolean saved = patientService.saveRecord(record);
            statusLabel.setText(saved
                    ? "Email updated successfully."
                    : "Unable to update email. It may already exist in the system.");
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (saved ? Theme.SUCCESS : Theme.DANGER) + ";");
            statusLabel.setVisible(true);
        });

        form.getChildren().addAll(
                title, restriction,
                fieldRow("Email", emailField),
                statusLabel, saveBtn
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane buildAppointmentTab(PatientRecord record) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = Theme.heading("Next Appointment");

        if (record.getNextFollowUpDate() != null) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(20));
            card.setMaxWidth(400);
            card.setStyle(
                    "-fx-background-color: " + Theme.LIGHT_BLUE + ";" +
                            "-fx-border-color: " + Theme.PRIMARY_BLUE + ";" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;"
            );

            Label dateLabel = new Label(record.getNextFollowUpDate().toString());
            dateLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Theme.HEADER_BLUE + ";");

            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(), record.getNextFollowUpDate()
            );
            String daysText = daysUntil > 0 ? "In " + daysUntil + " day(s)" : daysUntil == 0 ? "Today!" : "Passed";
            Label daysLabel = new Label(daysText);
            daysLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");

            card.getChildren().addAll(dateLabel, daysLabel);
            content.getChildren().addAll(title, card);
        } else {
            Label noAppt = new Label("No appointment scheduled. Please contact your doctor.");
            noAppt.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");
            content.getChildren().addAll(title, noAppt);
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private HBox infoRow(String label, String value) {
        Label labelNode = Theme.fieldLabel(label);
        labelNode.setMinWidth(160);
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.TEXT_DARK + ";");
        valueNode.setWrapText(true);
        HBox row = new HBox(10, labelNode, valueNode);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-border-color: transparent transparent " + Theme.BORDER + " transparent;");
        return row;
    }

    private HBox fieldRow(String labelText, javafx.scene.Node field) {
        Label label = Theme.fieldLabel(labelText);
        label.setMinWidth(120);
        HBox row = new HBox(10, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        if (field instanceof Region) {
            ((Region) field).setMaxWidth(Double.MAX_VALUE);
        }
        return row;
    }
}
