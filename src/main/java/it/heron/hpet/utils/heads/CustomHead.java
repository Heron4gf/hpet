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

    /**
     * Constructs a CustomHead with the specified Base64-encoded skin texture string.
     *
     * @param value Base64-encoded string representing the Minecraft skin texture
     */
    public CustomHead(String value) {
        super(value); // value is the Base64 skin texture string
    }

    /**
     * Generates a player head item with a custom skin texture based on the provided Base64-encoded string.
     *
     * @return an {@link ItemStack} representing a player head with the custom skin applied; if the Base64 string is invalid, returns a default player head
     */
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
     * Extracts the skin texture URL from a Base64-encoded Minecraft skin JSON string.
     *
     * The input must decode to a JSON string in the format: {"textures":{"SKIN":{"url":"<skin_url>"}}}.
     *
     * @param base64 Base64-encoded skin texture JSON string
     * @return URL pointing to the extracted skin texture
     * @throws MalformedURLException if the input does not match the expected format or the URL is invalid
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
