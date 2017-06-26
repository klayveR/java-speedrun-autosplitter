package game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import api.game.GamesAPIParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // Games file name
    private static final String GAMES_FILE = "games.save";

    // Games
    private List<Game> games = new ArrayList<>();

    public GameManager() {
        this.loadGames();
    }

    /**
     * This method is called when the thread accessing the speedrun.com REST API finished
     *
     * @param games List of games that should be added to the GameManagers game list
     */
    public void onGamesUpdateDone(List<Game> games) {
        this.games = games;

        // Save games to file
        this.saveGamesFile();
    }

    /**
     * Starts a new thread which accesses the speedrun.com REST API and parses all games into a list
     * This can take a long time!
     */
    public void updateGames() {
        GamesAPIParser gamesAPIParser = new GamesAPIParser(this);
        gamesAPIParser.execute();
    }

    /**
     * Loads existing games into memory
     */
    private void loadGames() {
        File gamesFile = new File(GAMES_FILE);
        if (gamesFile.exists() && !gamesFile.isDirectory()) {
            loadGamesFile();
        } else {
            saveGamesFile();
        }
    }

    /**
     * Loads games from the games list file
     */
    private void loadGamesFile() {
        try {
            FileInputStream fis = new FileInputStream(GAMES_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.games = (List<Game>) ois.readObject();
            ois.close();

            log.info("Game file loaded successfully");
        } catch (IOException e) {
            // TODO
            // Project file is most likely corrupted
            log.error("An error ocurred while reading from the games file", e);
        } catch (ClassNotFoundException e) {
            // This should not happen...
            log.error("An error ocurred while reading from the games file", e);
        }
    }

    /**
     * Saves any changes done to the games list to file
     */
    public void saveGamesFile() {
        try {
            FileOutputStream fos = new FileOutputStream(GAMES_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.games);
            oos.close();

            log.info("Game file saved successfully");
        } catch (IOException e) {
            // TODO
            // Might be permissions, whatever, we'll see
            log.error("An error ocurred while writing to the projects file", e);
        }
    }

    public Game getGame(String gameName) {
        for (Game game : this.games) {
            if (game.getName().equals(gameName)) {
                return game;
            }
        }

        return null;
    }

    public List<Game> getGames() {
        return this.games;
    }
}
