package scenes.main.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import scenes.main.presenter.MainPresenter;

public class MainView implements IMainView {
    private Scene scene;
    private MainPresenter presenter;

    // Nodes
    private ListView<String> projectList;

    public MainView() {
        this.build();
    }

    /**
     * Builds the scenes elements
     */
    @Override
    public void build() {
        GridPane root = new GridPane();
        root.setPadding(new Insets(5));
        root.setHgap(5);
        root.setVgap(5);
        root.setAlignment(Pos.CENTER);

        TextField newProjectText = new TextField();
        newProjectText.setPromptText("Project name");

        Button newProjectButton = new Button("Create project");
        newProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.createProject(newProjectText.getText()));

        this.projectList = new ListView<>();

        Button loadProjectButton = new Button("Load project");
        loadProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.loadProject());

        GridPane.setConstraints(this.projectList, 0, 0, 2, 1);
        GridPane.setConstraints(loadProjectButton, 0, 1, 2, 1);
        GridPane.setConstraints(newProjectText, 0, 2, 1, 1);
        GridPane.setConstraints(newProjectButton, 1, 2, 1, 1);

        root.getChildren().addAll(newProjectText, newProjectButton, this.projectList, loadProjectButton);

        this.scene = new Scene(root);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void setPresenter(MainPresenter mainPresenter) {
        this.presenter = mainPresenter;
    }

    public ListView<String> getProjectList() {
        return this.projectList;
    }
}
