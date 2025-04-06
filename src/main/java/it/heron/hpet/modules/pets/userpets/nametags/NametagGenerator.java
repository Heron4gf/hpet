package it.heron.hpet.modules.pets.userpets.nametags;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.utils.ProtocolVersionRetriever;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class NametagGenerator {

    private static final String DEFAULT_NAME_FORMAT = getDefaultNameFormat();
    private static String getDefaultNameFormat() {
        return PetPlugin.getInstance().getConfig().getString("nametags.format", "{name} {level}");
    }

    private static final boolean ENABLE_NAMETAGS = areNametagsEnabled();
    private static boolean areNametagsEnabled() {
        return PetPlugin.getInstance().getConfig().getBoolean("nametags.enable", true);
    }

    private static final int MINIMUM_DISPLAY_PROTOCOL_VERSION = 762;

    public static INametag getFormattedNametag(String text) {
        INametag nametag = getNametag(Component.text(text)); // do some formatting based on config
        changeNametagFormatted(nametag, text);
        return nametag;
    }

    public static void changeNametagFormatted(INametag nametag, String text) {
        Component formatted = MiniMessage.miniMessage().deserialize(
                DEFAULT_NAME_FORMAT, Placeholder.unparsed("{name}", text));
        nametag.setName(formatted);
    }

    private static INametag getNametag(Component text) {
        if(!ENABLE_NAMETAGS) {
            return new NoNametag();
        }
        int protocolVersion = ProtocolVersionRetriever.PROTOCOL_VERSION; // example value
        if(protocolVersion >= MINIMUM_DISPLAY_PROTOCOL_VERSION) {
            return new DisplayNametag(text);
        } else {
            return new ArmorstandNametag(text);
        }

    }


}
