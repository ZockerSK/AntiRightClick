package ch.pineirohosting.arc.util;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSUtil {

    private static final String PATH = Bukkit.getServer().getClass().getPackage().getName();
    public static final String VERSION = PATH.substring(PATH.lastIndexOf(".") + 1, PATH.length());

    public static Channel getPlayersNettyChannel(final Player player) {
        try {
            final Method handleMethod = player.getClass().getMethod("getHandle");
            final Object entityPlayer = handleMethod.invoke(player);
            final Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            final Object networkManger = playerConnection.getClass().getField("networkManager").get(playerConnection);
            return (Channel) networkManger.getClass().getField("channel").get(networkManger);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

}
