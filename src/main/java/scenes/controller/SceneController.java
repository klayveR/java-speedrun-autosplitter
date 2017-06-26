package scenes.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import project.ProjectManager;
import scenes.main.presenter.MainPresenter;
import game.GameManager;

public class SceneController {
    private Stage stage;

    // Window height and width
    private static final int STAGE_HEIGHT = 480;
    private static final int STAGE_WIDTH = 720;

    // Manager
    private ProjectManager projectManager;
    private GameManager gameManager;

    // Presenter
    private MainPresenter mainPresenter;

    public SceneController(Stage stage) {
        this.stage = stage;
        this.projectManager = new ProjectManager();
        this.gameManager = new GameManager();

        this.buildStage();
    }

    /**
     * Builds the initial window of the program
     *
     * @return true, if the window has been initialized and is showing
     */
    private boolean buildStage() {
        if (this.stage != null) {
            this.toMain();

            this.stage.setTitle("Speedrun Autosplitter");
            this.stage.setHeight(STAGE_HEIGHT);
            this.stage.setWidth(STAGE_WIDTH);
            this.stage.show();

            if (this.stage.isShowing())
                return true;
        }

        return false;
    }

    /**
     * Changes the scenes of the window to the scenes.main view
     */
    private void toMain() {
        if (this.mainPresenter == null) {
            this.mainPresenter = new MainPresenter(this);
        }

        this.stage.setScene(this.mainPresenter.getView().getScene());
    }

    /**
     * Shows a dialog, accessible from every scene
     *
     * @param alertType  The type of alert
     * @param title      Title of the dialog window
     * @param headerText Header text
     * @param text       Message
     */
    public void showDialog(Alert.AlertType alertType, String title, String headerText, String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(text);

        alert.showAndWait();
    }

    public void showInfoDialog(String headerText, String text) {
        this.showDialog(Alert.AlertType.INFORMATION, "Information", headerText, text);
    }

    public void showErrorDialog(String headerText, String text) {
        this.showDialog(Alert.AlertType.ERROR, "Error", headerText, text);
    }

    public void showWarnDialog(String headerText, String text) {
        this.showDialog(Alert.AlertType.WARNING, "Warning", headerText, text);
    }

    public ProjectManager getProjectManager() {
        return this.projectManager;
    }

    public GameManager getGameManager() { return this.gameManager; }
}
