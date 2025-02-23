package net.ovonsame;

import com.google.gson.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.*;

public class HeadsFetcher {
    private static final String API_URL = "https://minecraft-heads.com/scripts/api.php";
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Fetches all heads data from the API and returns a map where the key is the head name and the value is a pair of UUID and tags.
     */
    @SuppressWarnings("unused")
    public static Map<String, Map<String, Object>> fetchAllHeads() {
        Map<String, Map<String, Object>> headsMap = new HashMap<>();
        List<String> categories = List.of("Alphabet", "Animals", "Blocks", "Decoration", "Food-Drinks", "Humans", "Humanoid", "Miscellaneous", "Monsters", "Plants");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            for (String category : categories) {
                String url = String.format("%s?tags=true&cat=%s", API_URL, category.toLowerCase());
                String response = client.execute(new HttpGet(url), new BasicResponseHandler());
                JsonArray headsArray = GSON.fromJson(response, JsonArray.class);

                for (JsonElement element : headsArray) {
                    JsonObject head = element.getAsJsonObject();
                    Map<String, Object> headData = new HashMap<>();
                    headData.put("uuid", UUID.fromString(head.get("uuid").getAsString()));
                    headData.put("tags", Arrays.asList(head.get("tags").getAsString().split(",")));
                    headsMap.put(head.get("name").getAsString(), headData);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException("Failed to fetch heads data", e);
        }
        return headsMap;
    }
}

