package com.infermc.stale;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class StaleAPI extends JavaPlugin implements Listener {
    private long threshold = 60; /* Default length of time since they last played */

    public void onEnable() {
        getLogger().info("StaleAPI Loaded. :D");
        getServer().getPluginManager().registerEvents(this, this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Do something
                getLogger().info("Running player checker.");
                checkPlayers();
            }
        }, 0L, 120L);
    }
    // Skim through all players and check for expired ones.
    public void checkPlayers() {
        List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
        for (OfflinePlayer p : getServer().getOfflinePlayers()) {
            getLogger().info("Checking "+p.getUniqueId());
            long secondsSince = epoch() - p.getLastPlayed();
            if (secondsSince >= threshold) {
                getLogger().info(p.getUniqueId()+" hasn't been on for "+secondsSince+" seconds, Higher than "+threshold);
                players.add(p);
            } else {
                getLogger().info(p.getUniqueId()+" was last on "+secondsSince+" ago!");
            }
        }

        if (players.size() > 0) {
            PlayerExpiredEvent event = new PlayerExpiredEvent(players);
            getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // Remove the player data
                getLogger().info("Removing data for all players.");
            }
        }
    }

    public void onExpire(PlayerExpiredEvent event) {
        getLogger().info("Expire event called, Im a child plugin.");
    }

    private long epoch() {
        return System.currentTimeMillis()/1000;
    }
}
