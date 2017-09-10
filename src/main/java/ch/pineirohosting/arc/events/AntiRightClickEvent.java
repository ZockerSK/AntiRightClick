package ch.pineirohosting.arc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AntiRightClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int counter;

    public AntiRightClickEvent(final Player player, final int counter) {
        this.player = player;
        this.counter = counter;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCounter() {
        return counter;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
