package scenes.main.view;

import javafx.scene.control.ListView;
import scenes.main.presenter.MainPresenter;
import javafx.scene.Scene;

public interface IMainView {
    void build();

    Scene getScene();

    void setPresenter(MainPresenter mainPresenter);

    ListView<String> getProjectList();
}
