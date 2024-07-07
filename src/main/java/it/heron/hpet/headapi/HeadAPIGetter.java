package it.heron.hpet.headapi;

import org.bukkit.inventory.ItemStack;
import tsp.headdb.core.api.HeadAPI;

public class HeadAPIGetter {
    public static ItemStack getHead(String string) {
        return HeadAPI.getHeadById(Integer.parseInt(string.replace("HDB:", ""))).get().getItem(null);
    }
}
