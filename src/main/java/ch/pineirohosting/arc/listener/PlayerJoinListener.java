package ch.pineirohosting.arc.listener;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.handler.IncomingPacketDecoder;
import ch.pineirohosting.arc.util.NMSUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private static final String DECODER_NAME = "decoder";
    private static final String ARC_HANDLER_NAME = "arc";

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final IncomingPacketDecoder decoder = new IncomingPacketDecoder(event.getPlayer());
        NMSUtil.getPlayersNettyChannel(event.getPlayer()).pipeline().addAfter(DECODER_NAME, ARC_HANDLER_NAME, decoder);
        AntiRightClick.getInstance().getPlayersChannelHandler().put(event.getPlayer(), decoder);
    }

}
