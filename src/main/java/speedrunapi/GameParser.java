package speedrunapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameParser extends SwingWorker<List<Game>, Game> {
    // Logger
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // Game Manager
    private GameManager gameManager;

    private static final String API_URL = "http://www.speedrun.com/api/v1/games";
    private static final int OFFSET_INCREASE = 200;
    private static final int BULK_OFFSET_INCREASE = 1000;
    private static final int GAME_AMOUNT_MIN = 9500; // This is a rough estimate of the amount of games

    public GameParser(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public List<Game> doInBackground() throws Exception {
        log.debug("Starting games worker");
        return this.parseGamesJson();
    }

    @Override
    protected void process(List<Game> gamesChunk) {
        log.debug("Games parsed in last chunk: " + gamesChunk.size() + ", total progress: " + getProgress() + "%");
    }

    @Override
    protected void done() {
        try {
            log.debug("Done with games worker, parsed " + get().size() + " games");
            this.gameManager.onGamesUpdateDone(get());
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
        int gameCount = this.countGames();
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
     * @return Amount of games
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
