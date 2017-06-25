package scenes.main.presenter;

import controller.SceneController;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import project.Project;
import scenes.main.view.IMainView;
import scenes.main.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        this.updateProjectList();
    }

    /**
     * Changes the scenes to a project scene
     */
    private void toProjectScene() {
        // TODO
        log.debug("Project successfully created/loaded, changing scenes");
    }

    /**
     * Creates a new project
     */
    public void createProject(String projectName) {
        Project project = new Project(projectName);

        // Add project, if successful load project and switch scenes
        if (this.sceneController.getProjectManager().addProject(project)) {
            this.sceneController.getProjectManager().loadProject(project);
            this.updateProjectList();
            this.toProjectScene();
        } else {
            this.sceneController.showWarnDialog("Couldn't create project", "The project couldn't be created because another project with the same name already exists.");
        }
    }

    /**
     * Loads an existing project
     */
    public void loadProject() {
        // Get selected project
        String selectedProject = this.view.getProjectList().getSelectionModel().getSelectedItem();

        if (selectedProject != null) {
            // If project could be loaded successfully, switch scenes
            if (this.sceneController.getProjectManager().loadProject(selectedProject)) {
                this.toProjectScene();
            } else {
                this.sceneController.showErrorDialog("Couldn't load project", "The project couldn't be loaded because it doesn't exist.");
            }
        } else {
            this.sceneController.showInfoDialog("No project selected", "Please select the project you want to load from the list. If you don't have a project yet, create one!");
        }
    }

    /**
     * Updates the project list with the loaded projects from the ProjectManager
     */
    public void updateProjectList() {
        List<Project> projects = this.sceneController.getProjectManager().getProjects();
        ObservableList<String> projectListItems = this.view.getProjectList().getItems();

        // Clear list
        projectListItems.clear();

        // Add each project name to view list
        for (Project project : projects) {
            projectListItems.add(project.getProjectName());
        }
    }

    public IMainView getView() {
        return this.view;
    }
}
