import controller.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private SceneController sceneController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.sceneController = new SceneController(primaryStage);
    }

    @Override
    public void stop() {
        // On close, save the changes to any projects
        this.sceneController.getProjectManager().saveProjectsFile();
    }
}
