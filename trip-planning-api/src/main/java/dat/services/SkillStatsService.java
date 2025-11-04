package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SkillStatsService {

    private static final String BASE_URL = "https://apiprovider.cphbusinessapps.dk/api/v1/skills/stats?slugs=";

    public JsonNode getSkillStats(List<String> slugs) throws IOException {
        if (slugs == null || slugs.isEmpty()) {
            return null;
        }

        String joinedSlugs = String.join(",", slugs);
        String url = BASE_URL + joinedSlugs;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() == 200) {
            try (InputStream responseStream = connection.getInputStream()) {
                return objectMapper.readTree(responseStream).get("data");
            }
        } else {
            throw new IOException("Error fetching skill stats from API: " + connection.getResponseCode());
        }
    }
}
