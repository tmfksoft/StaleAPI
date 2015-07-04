package com.infermc.stale;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adds Methods to manually expire players and handles automated expiry.
 * @author Thomas Edwards (MajesticFudgie)
 */
public class StaleAPI extends JavaPlugin implements Listener {
    private long defaultThreshold = 2592000;

    private long threshold = defaultThreshold; // 30 Days, In seconds.
    BukkitScheduler scheduler = getServer().getScheduler();
    private int task;

    private static Permission perms = null;

    // Load the config and start the task.
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // Try and load the config etc
        loadConfig();

        if (gotVault()) {
            if (!setupPermissions()) {
                getLogger().warning("Unable to setup permissions provider 'Vault' :(");
                getLogger().warning("StaleAPI will be unable to check for exemptions.");
            }
        } else {
            getLogger().info("Your server doesn't have Vault. StaleAPI will be unable to check for exempt players.");
        }
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
        long defaultDelay = 3600;
        long delay = getConfig().getLong("delay", defaultDelay);
        task = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                checkPlayers();
            }
        }, 1200L, delay *20);
        getLogger().info("StaleAPI Ready!");
    }

    // Public so other plugins can make us reload. (Stupid idea but someone may find a use)
    /**
     * Force StaleAPI to reload
     */
    @SuppressWarnings("unused")
    public void reloadConfiguration() {
        onDisable(); /* Act as if we're disabling */
        loadConfig(); /* Now load the config */
    }

    // Skim through all players and check for expired ones.
    private void checkPlayers() {
        getLogger().info("Checking for expired players.");
        List<OfflinePlayer> players = new ArrayList<>();

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
            getLogger().info(players.size()+" potential players found for expiry.");
            expirePlayers(players);
        } else {
            getLogger().info("No potential players found for expiry.");
        }
    }

    // PUBLIC, So other plugins can force expire players.

    /* Forcibly expires a player */

    /**
     * Manually Expire a specific player.
     * @param player
     * The player to expire
     */
    @SuppressWarnings("unused")
    public void expirePlayer(OfflinePlayer player) {
        List<OfflinePlayer> players = new ArrayList<>();
        players.add(player);
        expirePlayers(players);
    }

    /**
     * Manually Expire a list of players.
     * @param players
     * The players to expire.
     */
    public void expirePlayers(List<OfflinePlayer> players) {
        // Remove exempt players.
        Iterator<OfflinePlayer> i = players.iterator();
        Boolean remove;
        String defaultWorld = getServer().getWorlds().get(0).getName();
        while (i.hasNext()) {
            OfflinePlayer p = i.next();
            // Check their perms
            remove = false;
            if (gotVault() && perms != null) {
                if (perms.playerHas(defaultWorld,p, "stale.exempt")) remove=true;
            }
            // Check if they're Operator.
            if (p.isOp()) remove=true;

            // Remove them?
            if(remove) i.remove();
        }

        if (players.size() <= 0) {
            // Called when all possible players are exempt.
            getLogger().info("All potential players are exempt. Skipping.");
            return;
        }

        PlayerExpireEvent event = new PlayerExpireEvent(players);
        getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            // Tell other plugins player data is being removed.
            PlayerExpiredEvent expiredEvent = new PlayerExpiredEvent(event.getPlayers());
            getServer().getPluginManager().callEvent(expiredEvent);

            // Remove the player data, Assuming the event isn't cancelled!

            // Get the main world folder.
            File BaseFolder = new File(getServer().getWorlds().get(0).getWorldFolder(), "playerdata");

            for (OfflinePlayer p : event.getPlayers()) {
                // Get and remove the players file.
                File playerFile = new File(BaseFolder, p.getUniqueId()+".dat");
                if (playerFile.exists()) {
                    if(!playerFile.delete()) {
                        getLogger().warning("Unable to remove .dat file for "+p.getName()+"!");
                        getLogger().warning("Please try removing manually and check your directory/file permissions!");
                    }
                }
            }

            // Get how many were skipped.
            int skipped = players.size()-event.getPlayers().size();
            getLogger().info(event.getPlayers().size()+" players have expired. "+skipped+" players were skipped.");
        } else {
            getLogger().info("PlayerExpireEvent was cancelled. No players will expire.");
        }
    }

    /**
     * Checks if the server has the Vault plugin.
     * @return
     * Whether the Vault plugin is enabled or not. (False if it doesn't exist)
     */
    public boolean gotVault() {
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }

    // Try and setup perms.
    private boolean setupPermissions() {
        //RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (rsp != null) {
            perms = rsp.getProvider();
        }
        return perms != null;
    }

    // Maths!
    private long epoch() {
        return System.currentTimeMillis()/1000;
    }
}
