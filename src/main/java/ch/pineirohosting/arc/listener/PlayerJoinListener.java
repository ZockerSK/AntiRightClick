package ch.pineirohosting.arc.listener;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.handler.IncomingPacketDecoder;
import ch.pineirohosting.arc.util.NMSUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        IncomingPacketDecoder decoder = new IncomingPacketDecoder(event.getPlayer());
        NMSUtil.getPlayersNettyChannel(event.getPlayer()).pipeline().addAfter("decoder", "arc", decoder);
        AntiRightClick.getInstance().getPlayersChannelHandler().put(event.getPlayer(), decoder);
    }

}
