package scenes.main.presenter;

import org.controlsfx.control.textfield.TextFields;
import scenes.controller.SceneController;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import project.Project;
import project.ProjectManager;
import scenes.main.view.IMainView;
import scenes.main.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import game.Game;
import game.GameBoxWorker;

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
        this.updateGameBox();
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
    public void createProject() {
        String gameName = this.view.getGameBox().getValue();
        String categoryName = this.view.getCategoryBox().getValue();

        if(gameName != null && !gameName.isEmpty()) {
            Project project = new Project(gameName);

            if (categoryName != null && !categoryName.isEmpty())
                project.setCategoryName(categoryName);

            // Add project, if successful load project and switch scenes
            if (this.sceneController.getProjectManager().addProject(project)) {
                this.sceneController.getProjectManager().loadProject(project);
                this.updateProjectList();
                this.toProjectScene();
            } else {
                this.sceneController.showWarnDialog("Couldn't create project", "The project couldn't be created because another project with the same name already exists.");
            }
        } else {
            this.sceneController.showWarnDialog("Couldn't create project", "To create a new project, you have to enter a game/project name. The project name can be anything, it doesn't necessarily have to be one of the games in the dropdown. Specifying a category is optional.");
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
                if (projectManager.removeProject(projectManager.getProjectByName(selectedProject))) {
                    this.updateProjectList();
                } else {
                    this.sceneController.showErrorDialog("Couldn't delete project", "The project couldn't be deleted because it doesn't exist or is currently the loaded project.");
                }
            }
        } else {
            this.sceneController.showInfoDialog("No project selected", "Please select the project you want to delete.");
        }
    }

    /**
     * Initiates an update of games and categories
     */
    public void updateGames() {
        this.sceneController.getGameManager().updateGames();
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

    /**
     * Updates the list of games
     */
    public void updateGameBox() {
        GameBoxWorker gbw = new GameBoxWorker(this.sceneController.getGameManager().getGames(), this.view.getGameBox());
        gbw.execute();
    }

    /**
     * Updates the category selection depending on which game has been selected
     */
    public void updateCategoryBox() {
        String gameValue = this.view.getGameBox().getValue();
        ComboBox<String> categoryBox = this.view.getCategoryBox();

        if (this.view.getGameBox().getItems().contains(gameValue)) {
            Game selectedGame = this.sceneController.getGameManager().getGame(gameValue);
            if (selectedGame != null) {
                categoryBox.setDisable(true);
                categoryBox.getItems().clear();
                categoryBox.getItems().addAll(selectedGame.getCategories());
                categoryBox.setDisable(false);

                // Update autocomplete items
                TextFields.bindAutoCompletion(categoryBox.getEditor(), categoryBox.getItems());
            }
        } else {
            if (!categoryBox.isDisable())
                categoryBox.setDisable(true);
        }
    }

    public IMainView getView() {
        return this.view;
    }
}
