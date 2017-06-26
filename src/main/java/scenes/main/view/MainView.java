package scenes.main.view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import scenes.main.AutoCompleteComboBoxListener;
import scenes.main.presenter.MainPresenter;

public class MainView implements IMainView {
    private Scene scene;
    private MainPresenter presenter;

    // Nodes
    private ListView<String> projectList;
    private ComboBox<String> gameBox;
    private ComboBox<String> categoryBox;

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

        // Load stuff on the left side
        GridPane loadRoot = new GridPane();
        loadRoot.setHgap(5);
        loadRoot.setVgap(5);

        projectList = new ListView<>();

        Button loadProjectButton = new Button("Load project");
        loadProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.loadProject());

        Button deleteProjectButton = new Button("Remove project");
        deleteProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.deleteProject());

        loadRoot.add(this.projectList, 0, 0, 2, 1);
        loadRoot.add(loadProjectButton, 0, 1);
        loadRoot.add(deleteProjectButton, 1, 1);

        // Create stuff on the right side
        VBox createRoot = new VBox(5);

        gameBox = new ComboBox<>();
        gameBox.setPromptText("Select your game");
        new AutoCompleteComboBoxListener<>(gameBox);

        gameBox.valueProperty().addListener((observableValue, string, gameName) -> presenter.updateCategoryBox());

        categoryBox = new ComboBox<>();
        categoryBox.setPromptText("Select run category");
        categoryBox.setDisable(true);
        new AutoCompleteComboBoxListener<>(categoryBox);

        Button newProjectButton = new Button("Create project");
        newProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.createProject());

        createRoot.getChildren().addAll(gameBox, categoryBox, newProjectButton);

        // Root GridPane Constraints
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(50);
        root.getColumnConstraints().add(column);

        column = new ColumnConstraints();
        column.setPercentWidth(50);
        root.getColumnConstraints().add(column);

        root.add(loadRoot, 0, 0);
        root.add(createRoot, 1, 0);

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

    public ComboBox<String> getGameBox() {
        return this.gameBox;
    }

    public ComboBox<String> getCategoryBox() {
        return this.categoryBox;
    }
}
