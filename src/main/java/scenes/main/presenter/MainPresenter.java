package scenes.main.presenter;

import controller.SceneController;
import scenes.main.view.IMainView;
import scenes.main.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainPresenter {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // View & Scene Controller
    private IMainView view;
    private SceneController sceneController;

    public MainPresenter(SceneController sceneController) {
        this.sceneController = sceneController;

        this.view = new MainView();
        this.view.setPresenter(this);
    }

    /**
     * Changes the scenes to a project scene
     */
    public void toProject() {
        // TODO
    }

    /**
     * Creates a new project
     */
    public void createNewProject() {
        // TODO
    }

    /**
     * Loads an existing project
     */
    public void loadProject() {
        // TODO
    }

    /**
     * @return View of Main scenes
     */
    public IMainView getView() {
        return this.view;
    }
}
