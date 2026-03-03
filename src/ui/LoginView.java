package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.User;
import service.AuthenticationService;

public class LoginView {

    private final AuthenticationService authService = new AuthenticationService();

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + Theme.LIGHT_BLUE + ";");

        VBox card = buildLoginCard();
        root.setCenter(card);

        return new Scene(root, 900, 650);
    }

    private VBox buildLoginCard() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setStyle(Theme.cardStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16, 0, 0, 4);");

        VBox logoBox = buildLogo();

        Label title = Theme.heading("MediClinic");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + Theme.HEADER_BLUE + ";");

        Label subtitle = new Label("Patient Record Management System");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + Theme.BORDER + ";");

        ToggleGroup roleGroup = new ToggleGroup();
        RadioButton doctorBtn = new RadioButton("Doctor");
        RadioButton patientBtn = new RadioButton("Patient");
        doctorBtn.setToggleGroup(roleGroup);
        patientBtn.setToggleGroup(roleGroup);
        doctorBtn.setSelected(true);
        doctorBtn.setStyle("-fx-font-size: 13px;");
        patientBtn.setStyle("-fx-font-size: 13px;");

        HBox roleBox = new HBox(24, doctorBtn, patientBtn);
        roleBox.setAlignment(Pos.CENTER);

        Label idLabel = Theme.fieldLabel("Username / Email");
        TextField idField = new TextField();
        idField.setStyle(Theme.inputStyle());
        idField.setPromptText("Enter username or email");
        idField.setMaxWidth(Double.MAX_VALUE);

        Label passLabel = Theme.fieldLabel("Password");
        PasswordField passField = new PasswordField();
        passField.setStyle(Theme.inputStyle());
        passField.setPromptText("Enter password");
        passField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = Theme.errorLabel();
        errorLabel.setVisible(false);

        Button loginBtn = new Button("Log In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        Theme.applyPrimary(loginBtn);

        Label hint = new Label("Doctor default: username = doctor  |  password = doctor123");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");
        hint.setWrapText(true);
        hint.setAlignment(Pos.CENTER);

        loginBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String pass = passField.getText();
            String role = doctorBtn.isSelected() ? "DOCTOR" : "PATIENT";

            if (id.isEmpty() || pass.isEmpty()) {
                showError(errorLabel, "Please enter your credentials.");
                return;
            }

            User user = authService.validateCredentials(id, pass, role);
            if (user == null) {
                showError(errorLabel, "Invalid credentials. Please try again.");
                passField.clear();
            } else {
                SceneManager.getInstance().setCurrentUser(user);
                if ("DOCTOR".equals(role)) {
                    SceneManager.getInstance().showDoctorDashboard();
                } else {
                    SceneManager.getInstance().showPatientPortal();
                }
            }
        });

        passField.setOnAction(e -> loginBtn.fire());

        VBox formBox = new VBox(6, idLabel, idField, passLabel, passField);
        formBox.setFillWidth(true);

        card.getChildren().addAll(logoBox, title, subtitle, sep, roleBox, formBox, errorLabel, loginBtn, hint);
        return card;
    }

    private VBox buildLogo() {
        StackPane icon = new StackPane();
        icon.setPrefSize(60, 60);
        Circle bg = new Circle(30, Color.web(Theme.PRIMARY_BLUE));
        Line v = new Line(30, 14, 30, 46);
        v.setStroke(Color.WHITE);
        v.setStrokeWidth(4);
        Line h = new Line(14, 30, 46, 30);
        h.setStroke(Color.WHITE);
        h.setStrokeWidth(4);
        icon.getChildren().addAll(bg, v, h);

        VBox box = new VBox(8, icon);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }
}
