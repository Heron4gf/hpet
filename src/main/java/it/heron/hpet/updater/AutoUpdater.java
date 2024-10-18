package it.heron.hpet.updater;

import it.heron.hpet.main.PetPlugin;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoUpdater {

    public static final String UPDATE_LINK = "https://hdev.it/sh?wty=6a77a9ba";
    public static final String LATEST_VERSION_CHECK_LINK = "https://api.spiget.org/v2/resources/93891/versions/latest";

    public static boolean isThisLatestUpdate() {
        return currentVersion().equalsIgnoreCase(latestVersion());
    }


    public static String currentVersion() {
        return PetPlugin.getInstance().getDescription().getVersion();
    }

    public static String latestVersion() {
        try {
            // Create a URL object from the Spiget API link
            URL url = new URL(LATEST_VERSION_CHECK_LINK);

            // Open a connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Check if the response code is 200 (HTTP OK)
            if (conn.getResponseCode() == 200) {
                // Read the response from the input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // Close the connections
                in.close();
                conn.disconnect();

                // Parse the response as JSON
                JSONObject jsonResponse = new JSONObject(content.toString());

                // Return the "name" field, which is the latest version string
                return jsonResponse.getString("name");
            } else {
                Bukkit.getLogger().info("Failed to fetch latest version. HTTP response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return null or some default value in case of failure
        return "unknown";
    }
}
