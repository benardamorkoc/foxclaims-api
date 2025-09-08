package arda.morkoc.api.events;

import arda.morkoc.api.model.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * FoxClaims Claim Delete Event
 * Bir claim silindiğinde tetiklenir
 */
public class FoxClaimsDeleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Claim claim;
    private final Player player;

    public FoxClaimsDeleteEvent(Claim claim, Player player) {
        this.claim = claim;
        this.player = player;
    }

    /**
     * Silinen claim'i döndürür
     * @return Claim nesnesi
     */
    public Claim getClaim() {
        return claim;
    }

    /**
     * Claim'i silen oyuncuyu döndürür
     * @return Player nesnesi
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}