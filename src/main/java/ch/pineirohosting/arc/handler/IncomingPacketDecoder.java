package ch.pineirohosting.arc.handler;

import ch.pineirohosting.arc.util.NMSUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class IncomingPacketDecoder extends MessageToMessageDecoder<Object> {

    private static final Pattern STARTS_WITH_NMS_REGEX;

    static {
        STARTS_WITH_NMS_REGEX = Pattern.compile("(net.minecraft.server." + NMSUtil.VERSION + ".Packet).*");
    }

    private final Player player;
    private int counter;
    private String lastPacket = "";

    public IncomingPacketDecoder(final Player player) {
        this.player = player;
    }

    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final Object packet,
                          final List<Object> list) {
        if (STARTS_WITH_NMS_REGEX.matcher(packet.getClass().getCanonicalName()).matches()) {
            final CheckResult result = PacketChecker.checkPacket(packet.getClass().getSimpleName(),
                    this.player, this.counter, this.lastPacket);
            this.counter = result.getCounter();
            this.lastPacket = result.getLastPacket();
        }
        list.add(packet);
    }
}
