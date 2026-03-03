package ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.PatientRecord;
import model.User;

public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Stage primaryStage;
    private User currentUser;

    private SceneManager() {}

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void showLogin() {
        currentUser = null;
        setScene(new LoginView().build());
    }

    public void showDoctorDashboard() {
        setScene(new DoctorDashboardView().build());
    }

    public void showPatientPortal() {
        setScene(new PatientPortalView(currentUser).build());
    }

    public void showSearchPatient() {
        setScene(new SearchPatientView().build());
    }

    public void showPatientRecord(PatientRecord record) {
        setScene(new ui.PatientRecordView(record).build());
    }

    public void showCreatePatient() {
        setScene(new CreatePatientView().build());
    }

    public void showFollowUpAlerts() {
        setScene(new FollowUpAlertsView().build());
    }

    private void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }
}
