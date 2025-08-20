package arda.morkoc.api.events;

import arda.morkoc.api.model.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClaimCreateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Claim claim;
    private final Player sender;

    public ClaimCreateEvent(Claim claim, Player sender) {
        this.claim = claim;
        this.sender = sender;
    }

    public Claim getClaim() {
        return claim;
    }

    public Player getSender() {
        return sender;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}