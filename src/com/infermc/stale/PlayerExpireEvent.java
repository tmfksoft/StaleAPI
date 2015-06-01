package com.infermc.stale;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public final class PlayerExpireEvent extends Event {

    // Vars
    private boolean cancelled = false;
    private List<OfflinePlayer> players;

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public PlayerExpireEvent(List<OfflinePlayer> plist) {
        players = plist;
    }

    /* Getters */

    // Is the event cancelled?
    public boolean isCancelled() {
        return cancelled;
    }
    public List<OfflinePlayer> getPlayers(){
        return players;
    }

    /* Setters */

    // Cancel removing data for all listed players
    public void setCancelled(boolean state) {
        cancelled = state;
    }

    // Essential stuff.
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
