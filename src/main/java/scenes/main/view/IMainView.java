package scenes.main.view;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import scenes.main.presenter.MainPresenter;
import javafx.scene.Scene;

public interface IMainView {
    void build();

    GridPane buildLoadUI();

    VBox buildCreateUI();

    Scene getScene();

    void setPresenter(MainPresenter mainPresenter);

    ListView<String> getProjectList();

    ComboBox<String> getGameBox();

    ComboBox<String> getCategoryBox();

    ProgressIndicator getUpdateGamesProgress();

    Label getUpdateGamesLabel();
}
