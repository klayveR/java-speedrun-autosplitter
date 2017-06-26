package speedrunapi;

import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameBoxWorker extends SwingWorker<List<String>, String> {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // Games list
    private List<Game> games;
    private ComboBox<String> comboBox;

    public GameBoxWorker(List<Game> games, ComboBox<String> comboBox) {
        this.games = games;
        this.comboBox = comboBox;
    }

    @Override
    protected List<String> doInBackground() throws Exception {
        // Disable game selection dropdown when update starts
        this.comboBox.setDisable(true);

        List<String> gameNames = new ArrayList<>();

        for (Game game : this.games) {
            gameNames.add(game.getName());
            publish(game.getName());
        }

        return gameNames;
    }

    @Override
    protected void process(List<String> stringChunk) {
        this.comboBox.getItems().addAll(stringChunk);
    }

    @Override
    protected void done() {
        this.comboBox.setDisable(false);
    }
}
