package arda.morkoc.api.events;

import arda.morkoc.api.model.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * FoxClaims Claim Create Event
 * Bir claim oluşturulduğunda tetiklenir
 */
public class FoxClaimsCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Claim claim;
    private final Player player;

    public FoxClaimsCreateEvent(Claim claim, Player player) {
        this.claim = claim;
        this.player = player;
    }

    /**
     * Oluşturulan claim'i döndürür
     * @return Claim nesnesi
     */
    public Claim getClaim() {
        return claim;
    }

    /**
     * Claim'i oluşturan oyuncuyu döndürür
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