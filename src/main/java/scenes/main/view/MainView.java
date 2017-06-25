package scenes.main.view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import scenes.main.presenter.MainPresenter;

public class MainView implements IMainView {
    private Scene scene;
    private MainPresenter presenter;

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

        Button newProjectButton = new Button("Create new project");
        newProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.createNewProject());

        Button loadProjectButton = new Button("Load existing project");
        loadProjectButton.addEventHandler(ActionEvent.ACTION, event -> presenter.loadProject());

        GridPane.setConstraints(newProjectButton, 0, 0, 1, 1);
        GridPane.setConstraints(loadProjectButton, 0, 1, 1, 1);

        root.getChildren().addAll(newProjectButton, loadProjectButton);

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
}
