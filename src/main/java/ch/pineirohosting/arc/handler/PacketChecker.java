package ch.pineirohosting.arc.handler;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.events.AntiRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class PacketChecker {

    static CheckResult checkPacket(final String packetName, final Player player, int counter, final String lastPacket) {
        if (player.isDead())
            return new CheckResult(counter, packetName);
        if (AntiRightClick.getInstance().getPacketsToCheck().contains(packetName)) {
            if (counter != 0)
                counter = 0;
        } else if (packetName.equalsIgnoreCase("PacketPlayInKeepAlive") &&
                !lastPacket.equalsIgnoreCase("PacketPlayInSettings")) {
            counter++;
            final double counterAmount = counter / AntiRightClick.getInstance().getCounter();
            if ((counterAmount == Math.floor(counterAmount)) && !Double.isInfinite(counterAmount)
                    && counterAmount != 0) {
                Bukkit.getScheduler().runTask(AntiRightClick.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(new AntiRightClickEvent(player, (int) counterAmount));
                    }
                });
            }
        }
        return new CheckResult(counter,
                (packetName.equalsIgnoreCase("PacketPlayInKeepAlive") &&
                        lastPacket.equalsIgnoreCase("PacketPlayInSettings") ? lastPacket : packetName));
    }

}
