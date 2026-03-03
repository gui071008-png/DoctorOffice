package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Theme {

    public static final String PRIMARY_BLUE = "#4A90D9";
    public static final String LIGHT_BLUE = "#EAF4FF";
    public static final String HEADER_BLUE = "#2C6EAB";
    public static final String WHITE = "#FFFFFF";
    public static final String TEXT_DARK = "#1A1A2E";
    public static final String TEXT_MUTED = "#6B7280";
    public static final String DANGER = "#E05252";
    public static final String SUCCESS = "#3AAA5A";
    public static final String BORDER = "#D0E4F7";

    private Theme() {}

    public static String baseStyle() {
        return "-fx-background-color: " + WHITE + ";";
    }

    public static void applyPrimary(Button button) {
        button.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + HEADER_BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> applyPrimary(button));
    }

    public static void applySecondary(Button button) {
        button.setStyle(
                "-fx-background-color: " + WHITE + ";" +
                        "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 7 19 7 19;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: " + PRIMARY_BLUE + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-cursor: hand;"
        );
    }

    public static void applyDanger(Button button) {
        button.setStyle(
                "-fx-background-color: " + DANGER + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
    }

    public static Label heading(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        return label;
    }

    public static Label subheading(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        return label;
    }

    public static Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold;");
        return label;
    }

    public static Label errorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: " + DANGER + "; -fx-font-size: 12px;");
        label.setVisible(false);
        return label;
    }

    public static HBox buildHeader(String title) {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle("-fx-background-color: " + HEADER_BLUE + ";");

        StackPane icon = buildClinicIcon();

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(icon, titleLabel, spacer);
        return header;
    }

    private static StackPane buildClinicIcon() {
        StackPane pane = new StackPane();
        pane.setPrefSize(36, 36);

        Circle circle = new Circle(18, Color.web("#FFFFFF", 0.2));

        Line vertical = new Line(18, 8, 18, 28);
        vertical.setStroke(Color.WHITE);
        vertical.setStrokeWidth(2.5);

        Line horizontal = new Line(9, 18, 27, 18);
        horizontal.setStroke(Color.WHITE);
        horizontal.setStrokeWidth(2.5);

        pane.getChildren().addAll(circle, vertical, horizontal);
        return pane;
    }

    public static String inputStyle() {
        return "-fx-background-color: " + WHITE + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 7 10 7 10;" +
                "-fx-font-size: 13px;";
    }

    public static String cardStyle() {
        return "-fx-background-color: " + WHITE + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 18;";
    }
}
