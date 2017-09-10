package ch.pineirohosting.arc.handler;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.events.AntiRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketChecker {

    public static int checkPacket(final String packetName, final Player player, int counter) {
        if (player.isDead())
            return 0;
        if (AntiRightClick.getInstance().getPacketsToCheck().contains(packetName)) {
            if (counter != 0)
                counter = 0;
        } else if (packetName.equalsIgnoreCase("PacketPlayInKeepAlive")) {
            counter++;
            if (counter == AntiRightClick.getInstance().getCounter())
                Bukkit.getPluginManager().callEvent(new AntiRightClickEvent(player, counter));
        }
        return counter;
    }

}
