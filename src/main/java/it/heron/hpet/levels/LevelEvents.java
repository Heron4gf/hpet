package it.heron.hpet.levels;

import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UserPet;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class LevelEvents implements Listener {

    private static HashMap<UUID, Long> cool = new HashMap<>();

    @EventHandler
    void onWalk(PlayerMoveEvent event) {
        if(event.isCancelled()) return;
        Player p = event.getPlayer();
        if(cool.containsKey(p.getUniqueId()) && cool.get(p.getUniqueId()) > System.currentTimeMillis()) return;
        cool.put(p.getUniqueId(), System.currentTimeMillis()+3000);
        check(Pet.getApi().getUserPet(p));
    }

    private void check(UserPet upet) {
        if(upet == null) return;
        if(currentStat(upet) < getMaxStat(upet)) return;
        Pet.getApi().incrementLevel(upet.getOwner());
    }

    public static int getMaxStat(UserPet upet) {
        if(upet.getType().getLtype() == LType.NONE) return Integer.MAX_VALUE;
        double d = upet.getType().getLtype().getValue()*1.2*upet.getLevel();
        return (int)d;
    }
    public static int currentStat(UserPet upet) {
        Player p = upet.getOwner();
        switch(upet.getType().getLtype()) {
            case JUMP:
                return p.getStatistic(Statistic.JUMP);
            case WALK:
                return p.getStatistic(Statistic.WALK_ONE_CM)/100;
            case KILL:
                return p.getStatistic(Statistic.KILL_ENTITY, (EntityType)upet.getType().getLtype().getObject());
            case MINE:
                return p.getStatistic(Statistic.MINE_BLOCK, (Material)upet.getType().getLtype().getObject());
            case CAKE_EATEN:
                return p.getStatistic(Statistic.CAKE_SLICES_EATEN);
            case DEATHS:
                return p.getStatistic(Statistic.DEATHS);
            case PLAYER_KILLS:
                return p.getStatistic(Statistic.PLAYER_KILLS);
        }
        return 0;
    }


}
