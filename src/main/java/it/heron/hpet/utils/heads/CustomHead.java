package it.heron.hpet.utils.heads;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class CustomHead extends HeadFromString {

    public CustomHead(String value) {
        super(value); // value is the Base64 skin texture string
    }

    @Override
    public ItemStack generate() {
        // Create a player head ItemStack
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        try {
            // Convert the Base64 string into the skin URL
            URL skinUrl = getUrlFromBase64(value);

            // Create a new player profile with a random UUID and empty name
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "");

            // Apply the skin URL to the profile
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(skinUrl);
            profile.setTextures(textures);

            // Set the profile (and thus the texture) on the skull meta
            skullMeta.setOwnerProfile(profile);
            head.setItemMeta(skullMeta);
        } catch (MalformedURLException e) {
            // In production you might want to log this error.
            e.printStackTrace();
        }

        return head;
    }

    /**
     * Converts a Base64 encoded skin texture string into a URL.
     * Expected JSON format: {"textures":{"SKIN":{"url":"<skin_url>"}}}
     *
     * @param base64 the Base64 encoded texture
     * @return the URL pointing to the skin texture
     * @throws MalformedURLException if the URL is malformed or the data is in an unexpected format
     */
    private URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        // The expected fixed parts of the decoded JSON
        String prefix = "{\"textures\":{\"SKIN\":{\"url\":\"";
        String suffix = "\"}}}";
        if (!decoded.startsWith(prefix) || !decoded.endsWith(suffix)) {
            throw new MalformedURLException("Invalid Base64 skin texture format");
        }
        // Extract the URL portion from the decoded JSON
        String urlString = decoded.substring(prefix.length(), decoded.length() - suffix.length());
        return new URL(urlString);
    }
}
