package ch.pineirohosting.arc.handler;

import ch.pineirohosting.arc.util.NMSUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.entity.Player;

import java.util.List;

public class IncomingPacketDecoder extends MessageToMessageDecoder<Object> {

    private final Player player;
    private int counter;

    public IncomingPacketDecoder(final Player player) {
        this.player = player;
    }

    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final Object packet,
                          final List<Object> list) throws Exception {
        if (packet.getClass().getCanonicalName().startsWith("net.minecraft.server." + NMSUtil.VERSION + ".Packet"))
            counter = PacketChecker.checkPacket(packet.getClass().getSimpleName(), this.player, this.counter);
        list.add(packet);
    }
}
