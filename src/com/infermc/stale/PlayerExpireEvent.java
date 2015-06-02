package com.infermc.stale;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Called when a players data is about to expire.
 * @author Thomas Edwards (MajesticFudgie)
 */
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

    /**
     * Check if the event has been cancelled.
     * @return
     * Current state of the event.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Get a list of players who's data is about to expire.
     * @return
     * List of players.
     */
    public List<OfflinePlayer> getPlayers(){
        return players;
    }

    /* Setters */

    // Cancel removing data for all listed players

    /**
     * Cancel or uncancel the event.
     * @param state
     * New cancel state.
     */
    public void setCancelled(boolean state) {
        cancelled = state;
    }

    /**
     * Removes a player from the list of expiring players, ideal to skip protected players.
     * @param player
     * The player to skip expiry.
     */
    public void skipPlayer(OfflinePlayer player) {
        if (players.contains(player)) {
            players.remove(player);
        }
    }

    // Essential stuff.
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
