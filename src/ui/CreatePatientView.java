package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import service.PatientService;
import util.ValidationUtil;

public class CreatePatientView {

    private final PatientService patientService = new PatientService();

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setStyle(Theme.baseStyle());

        HBox header = Theme.buildHeader("MediClinic — Create Patient Account");
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;");
        back.setOnAction(e -> SceneManager.getInstance().showDoctorDashboard());
        header.getChildren().add(back);
        root.setTop(header);

        ScrollPane scroll = new ScrollPane(buildForm());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + Theme.LIGHT_BLUE + ";");
        root.setCenter(scroll);

        return new Scene(root);
    }

    private VBox buildForm() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30));
        form.setMaxWidth(600);

        Label title = Theme.heading("New Patient Registration");

        TextField firstNameField = styledField("First name");
        TextField lastNameField = styledField("Last name");
        TextField emailField = styledField("patient@email.com");
        PasswordField passField = new PasswordField();
        passField.setStyle(Theme.inputStyle());
        passField.setPromptText("Minimum 6 characters");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setStyle(Theme.inputStyle());
        confirmPassField.setPromptText("Repeat password");

        Label statusLabel = Theme.errorLabel();
        statusLabel.setVisible(false);

        Button createBtn = new Button("Create Patient Account");
        Theme.applyPrimary(createBtn);
        createBtn.setMaxWidth(Double.MAX_VALUE);

        createBtn.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmPassField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                show(statusLabel, "All fields are required.", false);
                return;
            }
            if (!ValidationUtil.isValidEmail(email)) {
                show(statusLabel, "Please enter a valid email address.", false);
                return;
            }
            if (!ValidationUtil.isValidPassword(pass)) {
                show(statusLabel, "Password must be at least 6 characters.", false);
                return;
            }
            if (!pass.equals(confirm)) {
                show(statusLabel, "Passwords do not match.", false);
                return;
            }

            int recordId = patientService.createPatient(firstName, lastName, email, pass);
            if (recordId == -1) {
                show(statusLabel, "Email already exists in the system.", false);
            } else {
                show(statusLabel, "Patient account created successfully. Record ID: " + recordId, true);
                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                passField.clear();
                confirmPassField.clear();
            }
        });

        form.getChildren().addAll(
                title,
                row("First Name *", firstNameField),
                row("Last Name *", lastNameField),
                row("Email *", emailField),
                row("Password *", passField),
                row("Confirm Password *", confirmPassField),
                statusLabel, createBtn
        );

        return form;
    }

    private HBox row(String labelText, javafx.scene.Node field) {
        Label label = Theme.fieldLabel(labelText);
        label.setMinWidth(160);
        HBox row = new HBox(10, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        if (field instanceof Region) {
            ((Region) field).setMaxWidth(Double.MAX_VALUE);
        }
        return row;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setStyle(Theme.inputStyle());
        tf.setPromptText(prompt);
        return tf;
    }

    private void show(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (success ? Theme.SUCCESS : Theme.DANGER) + ";");
        label.setVisible(true);
    }
}
