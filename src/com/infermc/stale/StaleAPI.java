package com.infermc.stale;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaleAPI extends JavaPlugin implements Listener {
    private long threshold = 60; /* Default length of time since they last played */
    private long delay = 1200L;

    public void onEnable() {
        getLogger().info("StaleAPI Loaded. :D");
        getServer().getPluginManager().registerEvents(this, this);

        BukkitScheduler scheduler = getServer().getScheduler();

        // Schedule a check after the first minute. Then repeated at the specified delay.
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                checkPlayers();
            }
        }, 1200L, delay);
    }
    // Skim through all players and check for expired ones.
    public void checkPlayers() {
        getLogger().info("Checking for expired players.");
        List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        // Populate the list with players that haven't been on for the specified threshold.
        for (OfflinePlayer p : getServer().getOfflinePlayers()) {
            long secondsSince = epoch() - (p.getLastPlayed()/1000);
            if (secondsSince >= threshold) {
                players.add(p);
            }
        }

        // If there's any players. Run the event.
        if (players.size() > 0) {
            PlayerExpiredEvent event = new PlayerExpiredEvent(players);
            getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // Remove the player data, Assuming the event isn't cancelled!
                getLogger().info("Removing data for all "+players.size()+" players.");

                // Get the main world folder.
                File BaseFolder = new File(getServer().getWorlds().get(0).getWorldFolder(), "playerdata");
                for (OfflinePlayer p : players) {
                    // Get and remove the players file.
                    File playerFile = new File(BaseFolder, p.getUniqueId()+".dat");
                    if (playerFile.exists()) {
                        playerFile.delete();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onExpire(PlayerExpiredEvent event) {
        getLogger().info("Expire event called, Im a child plugin.");
    }

    private long epoch() {
        return System.currentTimeMillis()/1000;
    }
}
