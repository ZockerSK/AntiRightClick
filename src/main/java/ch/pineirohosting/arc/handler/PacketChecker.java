package ch.pineirohosting.arc.handler;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.events.AntiRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

class PacketChecker {
    private static final Pattern PACKET_PLAY_IN_SETTINGS_PATTERN =
            Pattern.compile("PacketPlayInSettings", Pattern.CASE_INSENSITIVE);
    private static final Pattern PACKET_PLAY_IN_KEEP_ALIVE_PATTERN =
            Pattern.compile("PacketPlayInKeepAlive", Pattern.CASE_INSENSITIVE);

    static CheckResult checkPacket(final String packetName, final Player player, int counter, final String lastPacket) {
        if (player.isDead() || player.isOnGround())
            return new CheckResult(counter, packetName);
        if (AntiRightClick.getInstance().getPacketsToCheck().contains(packetName)) {
            if (counter != 0)
                counter = 0;
        } else if (PACKET_PLAY_IN_KEEP_ALIVE_PATTERN.matcher(packetName).matches() &&
                !PACKET_PLAY_IN_SETTINGS_PATTERN.matcher(packetName).matches()) {
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
                (PACKET_PLAY_IN_KEEP_ALIVE_PATTERN.matcher(packetName).matches() &&
                        PACKET_PLAY_IN_SETTINGS_PATTERN.matcher(lastPacket).matches() ? lastPacket : packetName));
    }

}
