package it.heron.hpet.main.utils;

import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;

public class ProtocolVersionRetriever {

    public static final int PROTOCOL_VERSION = getProtocolVersion();

    // Step 1: Define the mapping of Minecraft versions to protocol numbers
    private static final Map<String, Integer> versionToProtocolMap = new HashMap<>();

    static {
        versionToProtocolMap.put("1.8", 47);
        versionToProtocolMap.put("1.9", 107);
        versionToProtocolMap.put("1.10", 210);
        versionToProtocolMap.put("1.11", 315);
        versionToProtocolMap.put("1.12", 335);
        versionToProtocolMap.put("1.13", 393);
        versionToProtocolMap.put("1.14", 477);
        versionToProtocolMap.put("1.15", 573);
        versionToProtocolMap.put("1.16", 735);
        versionToProtocolMap.put("1.17", 755);
        versionToProtocolMap.put("1.18", 757);
        versionToProtocolMap.put("1.19", 759);
        versionToProtocolMap.put("1.20", 763);

    }

    // Step 2: Method to retrieve the server's Minecraft version
    private static String getServerMinecraftVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion(); // e.g., "1.16.5-R0.1-SNAPSHOT"
        return bukkitVersion.split("-")[0]; // Extracts "1.16.5"
    }


    private static int getProtocolVersion() {
        String serverVersion = getServerMinecraftVersion();
        for (Map.Entry<String, Integer> entry : versionToProtocolMap.entrySet()) {
            if (serverVersion.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new IllegalStateException("Unsupported server version: " + serverVersion);
    }
}