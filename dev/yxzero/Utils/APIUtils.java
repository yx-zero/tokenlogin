package dev.yxzero.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class APIUtils {

    public static String[] getProfileInfo(String token) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            request.setHeader("Authorization", "Bearer " + token);
            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
                String username = json.get("name").getAsString();
                String uuid = json.get("id").getAsString();
                return new String[]{username, uuid};
            }
        }
    }

    public static boolean validateSession(String token) {
        try {
            String[] profile = getProfileInfo(token);
            Session session = Minecraft.getMinecraft().getSession();
            return profile[0].equals(session.getUsername()) &&
                    profile[1].equals(session.getPlayerID());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkOnline(String uuid) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://api.slothpixel.me/api/players/" + uuid);
            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
                return json.get("online").getAsBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int changeName(String newName, String token) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut("https://api.minecraftservices.com/minecraft/profile/name/" + newName);
            request.setHeader("Authorization", "Bearer " + token);
            try (CloseableHttpResponse response = client.execute(request)) {
                return response.getStatusLine().getStatusCode();
            }
        }
    }

    public static int changeSkin(String url, String token) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");
            request.setHeader("Authorization", "Bearer " + token);
            request.setHeader("Content-Type", "application/json");
            String jsonString = String.format("{\"variant\": \"classic\", \"url\": \"%s\"}", url);
            request.setEntity(new StringEntity(jsonString));
            try (CloseableHttpResponse response = client.execute(request)) {
                return response.getStatusLine().getStatusCode();
            }
        }
    }
}
