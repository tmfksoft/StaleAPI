package com.infermc.stale;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Called when a players data has already expired. Intended to tell plugins to remove their player data. (Cannot be cancelled)
 * @author Thomas Edwards (MajesticFudgie)
 */
public final class PlayerExpiredEvent extends Event {

    private List<OfflinePlayer> players;

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public PlayerExpiredEvent(List<OfflinePlayer> plist) {
        players = plist;
    }

    /* Getters */

    /**
     * Get a list of players who's data is about to expire.
     * @return
     * List of players.
     */
    public List<OfflinePlayer> getPlayers(){
        return players;
    }

    // Essential stuff.
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
