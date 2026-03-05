package app;

import data.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.SceneManager;

// install SQLLite
// check how to apply changes in classes in data to stop using .ser to change to sqlite

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.getInstance().initialize();
        SceneManager.getInstance().init(primaryStage);
        SceneManager.getInstance().showLogin();
        primaryStage.setTitle("MediClinic — Patient Record Management");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
