import controller.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new SceneController(primaryStage);
    }

    @Override
    public void stop() {
        // Nothing ever happens...
    }
}
