package scenes.main.presenter;

import controller.SceneController;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import project.Project;
import project.ProjectManager;
import scenes.main.view.IMainView;
import scenes.main.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

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
     * Shows a confirmation dialog asking the user if he really wants to delete the project
     * If yes, the ProjectManager removes the project entirely
     */
    public void deleteProject() {
        String selectedProject = this.view.getProjectList().getSelectionModel().getSelectedItem();

        // If any project is selected on the list
        if (selectedProject != null) {
            // Show confirmation dialog
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Deleting project \"" + selectedProject + "\"");
            alert.setContentText("Do you really want to delete this project? All your settings and split configurations will be deleted permanently.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // User chose yes, delete project
                ProjectManager projectManager = this.sceneController.getProjectManager();

                // Attempt to delete project, if successful, update list, if not, show error dialog
                if(projectManager.removeProject(projectManager.getProjectByName(selectedProject))) {
                    this.updateProjectList();
                } else {
                    this.sceneController.showErrorDialog("Couldn't delete project", "The project couldn't be deleted because it apparently doesn't exist.");
                }
            }
        } else {
            this.sceneController.showInfoDialog("No project selected", "Please select the project you want to delete.");
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
