package api.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.Game;
import game.GameManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.controller.SceneController;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GamesAPIParser extends SwingWorker<List<Game>, Game> {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // Game Manager
    private SceneController sceneController;

    // Count of games available from API/parsed
    private int gameCount = 0;
    private int parsedGames = 0;

    private static final String API_URL = "http://www.speedrun.com/api/v1/games";
    private static final int OFFSET_INCREASE = 200; // Offset increase per loop while getting games
    private static final int BULK_OFFSET_INCREASE = 1000; // Offset increase per loop while getting game count

    // This is roughly the latest known amount of games available for parsing
    // It's intentionally lower than the actual amount in case games get deleted from the API
    private static final int GAME_AMOUNT_MIN = 9800;

    public GamesAPIParser(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    @Override
    public List<Game> doInBackground() throws Exception {
        log.debug("Starting games worker");

        // Set the current updating status to true
        this.sceneController.getGameManager().setUpdating(true);

        if (this.sceneController.getMainPresenter() != null) {
            ProgressIndicator updateGamesProgress = this.sceneController.getMainPresenter().getView().getUpdateGamesProgress();
            Label updateGamesLabel = this.sceneController.getMainPresenter().getView().getUpdateGamesLabel();

            // Show and initialize progress % and label to default values
            Platform.runLater(() -> {
                updateGamesProgress.setVisible(true);
                updateGamesProgress.setProgress(0.0);

                updateGamesLabel.setVisible(true);
                updateGamesLabel.setText("Fetching data from speedrun.com...");
            });
        }

        return this.parseGamesJson();
    }

    @Override
    protected void process(List<Game> gamesChunk) {
        parsedGames += gamesChunk.size();

        // Update progress % and progress label
        if (this.sceneController.getMainPresenter() != null) {
            Platform.runLater(() -> {
                this.sceneController.getMainPresenter().getView().getUpdateGamesProgress().setProgress((float) getProgress() / (float) 100);
                this.sceneController.getMainPresenter().getView().getUpdateGamesLabel().setText(parsedGames + "/" + this.gameCount + " parsed");
            });
        }
        log.debug("Games parsed in last chunk: " + gamesChunk.size() + ", total progress: " + getProgress() + "%");
    }

    @Override
    protected void done() {
        try {
            log.debug("Done with games worker, parsed " + get().size() + " games");

            // Make it possible to click the update button again and hide the progress label
            Platform.runLater(() -> {
                this.sceneController.getGameManager().setUpdating(false);
                this.sceneController.getMainPresenter().getView().getUpdateGamesLabel().setVisible(false);
            });

            this.sceneController.getGameManager().onGamesUpdateDone(get());
        } catch (Exception ignore) {
            // Documentation says to ignore this, so I will
        }
    }

    /**
     * Iterates through chunks of the speedrun.com REST API games data and parses the JSON into game objects
     *
     * @return Complete list of game objects from speedrun.com
     */
    public List<Game> parseGamesJson() {
        // Count amount of games available in REST API
        gameCount = this.countGames();
        log.debug("Games to parse: " + gameCount);

        // Games list
        List<Game> games = new ArrayList<>();

        // Current offset
        int offset = 0;

        // boolean to check if theres more data available
        boolean moreDataAvailable = true;

        // Loop as long as more game data is available
        while (moreDataAvailable) {
            try {
                // Initiate connection
                URL url = new URL(API_URL + "?embed=categories&max=200&offset=" + offset);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                // Read Json response
                String output;
                while ((output = br.readLine()) != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject json = (JsonObject) jsonParser.parse(output);

                    if (json.has("data")) {
                        JsonArray dataArray = (JsonArray) json.get("data");
                        if (dataArray.size() > 0) {
                            for (JsonElement dataElement : dataArray) {
                                // Create game object
                                Game game = new Game();

                                JsonObject dataObject = (JsonObject) dataElement;
                                JsonObject gameNames = (JsonObject) dataObject.get("names");
                                String name = gameNames.get("international").getAsString();
                                game.setName(name); // Set game name

                                JsonObject categories = (JsonObject) dataObject.get("categories");
                                JsonArray categoriesData = (JsonArray) categories.get("data");

                                // Adding game categories
                                for (JsonElement category : categoriesData) {
                                    JsonObject categoryObject = (JsonObject) category;
                                    String categoryName = categoryObject.get("name").getAsString();
                                    game.addCategory(categoryName);
                                }

                                games.add(game);
                                publish(game); // Async process
                            }

                            setProgress(100 * games.size() / gameCount);

                            offset += OFFSET_INCREASE;
                            moreDataAvailable = true;
                        } else {
                            moreDataAvailable = false;
                            log.info("Updated " + games.size() + " games successfully");
                        }
                    }
                }

                // Close connection
                conn.disconnect();
            } catch (MalformedURLException e) {
                // TODO
                e.printStackTrace();
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        }

        return games;
    }

    /**
     * Counts the amount of games the speedrun.com REST API has
     *
     * @return Amount of games that are available from the API
     */
    public int countGames() {
        int offset = GAME_AMOUNT_MIN;
        int games = 0;
        boolean moreDataAvailable = true;

        while (moreDataAvailable) {
            try {
                // Initiate connection
                URL url = new URL(API_URL + "?_bulk=yes&max=1000&offset=" + offset);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                // Read Json response
                String output;
                while ((output = br.readLine()) != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject json = (JsonObject) jsonParser.parse(output);

                    // Get the amount of games in the current data batch
                    if (json.has("pagination")) {
                        JsonObject paginationArray = (JsonObject) json.get("pagination");
                        int gameCount = paginationArray.get("size").getAsInt();

                        // If the gameCount equals the offset increase, there's more data available
                        if (gameCount == BULK_OFFSET_INCREASE) {
                            offset += BULK_OFFSET_INCREASE;
                        } else {
                            games = offset + gameCount; // Calculating the final amount of games
                            moreDataAvailable = false;
                        }
                    }
                }

                // Close connection
                conn.disconnect();
            } catch (MalformedURLException e) {
                // TODO
                e.printStackTrace();
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        }

        return games;
    }
}
