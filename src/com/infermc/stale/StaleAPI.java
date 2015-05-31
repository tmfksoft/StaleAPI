package com.infermc.stale;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Thomas on 31/05/2015.
 */
public class StaleAPI extends JavaPlugin implements Listener {
    public void onEnable() {
        getLogger().info("StaleAPI Loaded. :D");
        getServer().getPluginManager().registerEvents(new PlayerDataExpired(), this);
    }
}
