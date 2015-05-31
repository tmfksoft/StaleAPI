package com.infermc.stale;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Thomas on 31/05/2015.
 */
public class StaleAPI extends JavaPlugin implements Listener {
    public void onEnable() {
        getLogger().info("StaleAPI Loaded. :D");
        getServer().getPluginManager().registerEvents(new PlayerDataExpired(), this);
    }
    // Skim through all players and check for expired ones.
    public void checkPlayers() {
        List<UUID> players = new ArrayList<UUID>();
        PlayerDataExpired event = new PlayerDataExpired(players);
        getServer().getPluginManager().callEvent(event);
        if (!event.isCanceled()) {

        }
    }

    public void onExpire(PlayerDataExpired event) {
    }
}
