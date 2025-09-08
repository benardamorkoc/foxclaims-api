package arda.morkoc.api.events;

import arda.morkoc.api.model.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FoxClaimsDeleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Claim claim;
    private final Player player;

    public FoxClaimsDeleteEvent(Claim claim, Player player) {
        this.claim = claim;
        this.player = player;
    }

    public Claim getClaim() {
        return claim;
    }

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