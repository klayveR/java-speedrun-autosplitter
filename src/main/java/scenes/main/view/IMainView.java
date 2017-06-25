package scenes.main.view;

import scenes.main.presenter.MainPresenter;
import javafx.scene.Scene;

public interface IMainView {
    void build();

    Scene getScene();

    void setPresenter(MainPresenter mainPresenter);
}
