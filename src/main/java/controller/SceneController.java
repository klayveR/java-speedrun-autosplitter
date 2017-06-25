package controller;

import project.Project;
import project.ProjectManager;
import scenes.main.presenter.MainPresenter;
import javafx.stage.Stage;

public class SceneController {
    private Stage stage;

    // Window height and width
    private static final int STAGE_HEIGHT = 480;
    private static final int STAGE_WIDTH = 720;

    // Project manager
    private ProjectManager projectManager;

    // Presenter
    private MainPresenter mainPresenter;

    public SceneController(Stage stage) {
        this.stage = stage;
        this.projectManager = new ProjectManager();

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

    public ProjectManager getProjectManager() {
        return this.projectManager;
    }
}
