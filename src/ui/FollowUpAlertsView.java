package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.FollowUp;
import model.PatientRecord;
import service.FollowUpService;
import service.PatientService;

import java.time.LocalDate;
import java.util.List;

public class FollowUpAlertsView {

    private final FollowUpService followUpService = new FollowUpService();
    private final PatientService patientService = new PatientService();
    private final ObservableList<FollowUp> tableData = FXCollections.observableArrayList();

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setStyle(Theme.baseStyle());

        HBox header = Theme.buildHeader("MediClinic — Follow-Up Alerts");
        Button back = new Button("← Dashboard");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;");
        back.setOnAction(e -> SceneManager.getInstance().showDoctorDashboard());
        header.getChildren().add(back);
        root.setTop(header);

        root.setCenter(buildContent());
        loadAlerts();

        return new Scene(root, 900, 650);

    }

    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = Theme.heading("Follow-Up Alerts");
        Label subtitle = new Label("Patients with a follow-up appointment within the next 14 days.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.TEXT_MUTED + ";");

        TableView<FollowUp> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, subtitle, table);
        return content;
    }

    @SuppressWarnings("unchecked")
    private TableView<FollowUp> buildTable() {
        TableView<FollowUp> tv = new TableView<>(tableData);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No pending follow-up alerts."));

        TableColumn<FollowUp, Integer> idCol = new TableColumn<>("Alert ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("followUpId"));
        idCol.setMaxWidth(80);

        TableColumn<FollowUp, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<FollowUp, LocalDate> dateCol = new TableColumn<>("Follow-Up Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("followUpDate"));

        TableColumn<FollowUp, Void> openCol = new TableColumn<>("Open Record");
        openCol.setCellFactory(col -> new TableCell<FollowUp, Void>() {
            private final Button btn = new Button("Open");
            {
                Theme.applyPrimary(btn);
                btn.setOnAction(e -> {
                    FollowUp fu = getTableView().getItems().get(getIndex());
                    PatientRecord record = patientService.getRecord(fu.getRecordId());
                    if (record != null) SceneManager.getInstance().showPatientRecord(record);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        TableColumn<FollowUp, Void> dismissCol = new TableColumn<>("Dismiss");
        dismissCol.setCellFactory(col -> new TableCell<FollowUp, Void>() {
            private final Button btn = new Button("Mark Read");
            {
                Theme.applySecondary(btn);
                btn.setOnAction(e -> {
                    FollowUp fu = getTableView().getItems().get(getIndex());
                    followUpService.markAlertRead(fu.getFollowUpId());
                    loadAlerts();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tv.getColumns().addAll(idCol, nameCol, dateCol, openCol, dismissCol);
        return tv;
    }

    private void loadAlerts() {
        List<FollowUp> alerts = followUpService.getUnreadAlerts();
        tableData.setAll(alerts);
    }
}
