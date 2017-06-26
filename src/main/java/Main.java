import controller.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedrunapi.Game;
import speedrunapi.GameManager;
import speedrunapi.GameParser;

public class Main extends Application {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

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
