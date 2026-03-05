package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import model.FollowUp;
import service.FollowUpService;

import java.util.List;

public class DoctorDashboardView {

    private final FollowUpService followUpService = new FollowUpService();

    public Scene build() {
        followUpService.checkAndGenerateAlerts();

        BorderPane root = new BorderPane();
        root.setStyle(Theme.baseStyle());

        HBox header = Theme.buildHeader("MediClinic — Doctor Dashboard");
        addLogoutButton(header);
        root.setTop(header);

        VBox content = buildContent();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        root.setCenter(scroll);

        return new Scene(root);
    }

    private void addLogoutButton(HBox header) {
        Button logout = new Button("Logout");
        logout.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 7 19 7 19;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: white;" +
                        "-fx-border-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        logout.setOnAction(e -> SceneManager.getInstance().showLogin());
        header.getChildren().add(logout);
    }

    private VBox buildContent() {
        VBox content = new VBox(24);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + Theme.LIGHT_BLUE + ";");

        Label welcome = Theme.heading("Welcome, Dr. Andre");

        List<FollowUp> alerts = followUpService.getUnreadAlerts();
        if (!alerts.isEmpty()) {
            HBox alertBanner = buildAlertBanner(alerts.size());
            content.getChildren().add(alertBanner);
        }

        Label actionsLabel = Theme.subheading("Quick Actions");

        HBox actionsRow = buildActionCards();

        content.getChildren().addAll(welcome, actionsLabel, actionsRow);
        return content;
    }

    private HBox buildAlertBanner(int count) {
        HBox banner = new HBox(12);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(12, 16, 12, 16));
        banner.setStyle(
                "-fx-background-color: #FFF3CD;" +
                        "-fx-border-color: #F0AD4E;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        Label icon = new Label("⚠");
        icon.setStyle("-fx-font-size: 18px; -fx-text-fill: #856404;");
        Label msg = new Label(count + " follow-up alert(s) require your attention.");
        msg.setStyle("-fx-font-size: 13px; -fx-text-fill: #856404; -fx-font-weight: bold;");
        Button view = new Button("View Alerts");
        Theme.applyPrimary(view);
        view.setOnAction(e -> SceneManager.getInstance().showFollowUpAlerts());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        banner.getChildren().addAll(icon, msg, spacer, view);
        return banner;
    }

    private HBox buildActionCards() {
        HBox row = new HBox(16);
        row.setFillHeight(true);

        row.getChildren().addAll(
                buildCard("🔍", "Search Patient",
                        "Find a patient by name or surname.",
                        () -> SceneManager.getInstance().showSearchPatient()),
                buildCard("➕", "Create Patient Account",
                        "Register a new patient in the system.",
                        () -> SceneManager.getInstance().showCreatePatient()),
                buildCard("🔔", "Follow-Up Alerts",
                        "Review upcoming follow-up appointments.",
                        () -> SceneManager.getInstance().showFollowUpAlerts())
        );

        return row;
    }

    private VBox buildCard(String emoji, String title, String description, Runnable action) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(260);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + Theme.BORDER + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;"
        );

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 28px;");

        Label titleLabel = Theme.subheading(title);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");
        descLabel.setWrapText(true);

        Button btn = new Button("Open →");
        Theme.applyPrimary(btn);
        btn.setOnAction(e -> action.run());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(emojiLabel, titleLabel, descLabel, spacer, btn);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: " + Theme.LIGHT_BLUE + ";" +
                        "-fx-border-color: " + Theme.PRIMARY_BLUE + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(74,144,217,0.15), 12, 0, 0, 4);" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + Theme.BORDER + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;"
        ));

        return card;
    }
}
