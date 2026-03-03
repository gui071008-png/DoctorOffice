package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.PatientRecord;
import service.PatientService;

import java.time.LocalDate;
import java.util.List;

public class SearchPatientView {

    private final PatientService patientService = new PatientService();
    private TableView<PatientRecord> table;
    private final ObservableList<PatientRecord> tableData = FXCollections.observableArrayList();

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setStyle(Theme.baseStyle());

        HBox header = Theme.buildHeader("MediClinic — Search Patient");
        addBackButton(header);
        root.setTop(header);

        VBox content = buildContent();
        root.setCenter(content);

        loadAll();

        return new Scene(root, 900, 650);
    }

    private void addBackButton(HBox header) {
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand;");
        back.setOnAction(e -> SceneManager.getInstance().showDoctorDashboard());
        header.getChildren().add(back);
    }

    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = Theme.heading("Patient Search");

        HBox searchBar = buildSearchBar();

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, searchBar, table);
        return content;
    }

    private HBox buildSearchBar() {
        TextField searchField = new TextField();
        searchField.setStyle(Theme.inputStyle());
        searchField.setPromptText("Search by first name or last name...");
        searchField.setPrefWidth(360);

        Button searchBtn = new Button("Search");
        Theme.applyPrimary(searchBtn);

        Button clearBtn = new Button("Show All");
        Theme.applySecondary(clearBtn);

        searchBtn.setOnAction(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                loadAll();
            } else {
                List<PatientRecord> results = patientService.searchPatients(query);
                tableData.setAll(results);
            }
        });

        searchField.setOnAction(e -> searchBtn.fire());

        clearBtn.setOnAction(e -> {
            searchField.clear();
            loadAll();
        });

        HBox bar = new HBox(10, searchField, searchBtn, clearBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private TableView<PatientRecord> buildTable() {
        TableView<PatientRecord> tv = new TableView<>(tableData);
        tv.setStyle("-fx-border-color: " + Theme.BORDER + "; -fx-border-radius: 6;");
        tv.setPlaceholder(new Label("No patients found."));
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PatientRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        idCol.setMaxWidth(60);

        TableColumn<PatientRecord, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<PatientRecord, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<PatientRecord, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<PatientRecord, LocalDate> followUpCol = new TableColumn<>("Next Follow-Up");
        followUpCol.setCellValueFactory(new PropertyValueFactory<>("nextFollowUpDate"));

        TableColumn<PatientRecord, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<PatientRecord, Void>() {
            private final Button btn = new Button("View Record");
            {
                Theme.applyPrimary(btn);
                btn.setOnAction(e -> {
                    PatientRecord record = getTableView().getItems().get(getIndex());
                    SceneManager.getInstance().showPatientRecord(record);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tv.getColumns().addAll(idCol, firstCol, lastCol, emailCol, followUpCol, actionCol);
        tv.setRowFactory(t -> {
            TableRow<PatientRecord> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    SceneManager.getInstance().showPatientRecord(row.getItem());
                }
            });
            return row;
        });

        return tv;
    }

    private void loadAll() {
        tableData.setAll(patientService.getAllPatients());
    }
}
