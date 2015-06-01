package com.infermc.stale;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaleAPI extends JavaPlugin implements Listener {
    private long defaultThreshold = 2592000;
    private long defaultDelay = 3600;

    private long threshold = defaultThreshold; // 30 Days, In seconds.
    private long delay = defaultDelay; // 1 hour in ticks.
    BukkitScheduler scheduler = getServer().getScheduler();
    private int task;

    // Load the config and start the task.
    public void onEnable() {
        getLogger().info("StaleAPI Loaded. :D");
        getServer().getPluginManager().registerEvents(this, this);

        // Try and load the config etc
        loadConfig();
    }
    public void onDisable() {
        // Cancel the task.
        scheduler.cancelTask(task);
    }

    // Loads the config and starts the task.
    private void loadConfig() {
        // Schedule a check after the first minute. Then repeated at the specified delay.
        saveDefaultConfig();
        threshold = getConfig().getLong("threshold",defaultThreshold);
        delay = getConfig().getLong("delay",defaultDelay);
        task = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                checkPlayers();
            }
        }, 1200L, delay*20);
    }
    // Public so other plugins can make us reload. (Stupid idea but someone may find a use)
    public void reloadConfig() {
        onDisable(); /* Act as if we're disabling */
        loadConfig(); /* Now load the config */
    }

    // Skim through all players and check for expired ones.
    private void checkPlayers() {
        getLogger().info("Checking for expired players.");
        List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        // Populate the list with players that haven't been on for the specified threshold.
        for (OfflinePlayer p : getServer().getOfflinePlayers()) {
            long secondsSince = epoch() - (p.getLastPlayed()/1000);
            if (!p.isOnline()) {
                /* Assuming they're not online */
                if (secondsSince >= threshold) {
                    players.add(p);
                }
            }
        }

        // If there's any players. Run the event.
        if (players.size() > 0) {
            expirePlayers(players);
        }
    }

    // PUBLIC, So other plugins can force expire players.

    /* Forcibly expires a player */
    public void expirePlayer(OfflinePlayer player) {
        List<OfflinePlayer> players = new ArrayList<>();
        players.add(player);
        expirePlayers(players);
    }
    public void expirePlayers(List<OfflinePlayer> players) {
        // Remove exempt players.
        for (OfflinePlayer p : players) {
            if (getServer().getPlayer(p.getUniqueId()).hasPermission("stale.exempt")) {
                players.remove(p);
            }
        }

        PlayerExpireEvent event = new PlayerExpireEvent(players);
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

    private long epoch() {
        return System.currentTimeMillis()/1000;
    }
}
