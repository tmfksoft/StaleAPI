package com.infermc.stale;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

/**
 * Created by Thomas on 31/05/2015.
 */
public final class PlayerDataExpired extends Event {
    private boolean canceled = false;
    private List<UUID> players;
    private static final HandlerList handlers = new HandlerList();

    public PlayerDataExpired(List<UUID> plist) {
        players = plist;
    }

    /* Getters */
    public boolean isCanceled() {
        return canceled;
    }
    public List<UUID> getPlayers(){
        return players;
    }

    /* Setters */
    public void setCanceled(boolean state) {
        canceled = state;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
