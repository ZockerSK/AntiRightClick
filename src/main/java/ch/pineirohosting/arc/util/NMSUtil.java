package ch.pineirohosting.arc.util;

import ch.pineirohosting.arc.AntiRightClick;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class NMSUtil {

    private static final String PATH = Bukkit.getServer().getClass().getPackage().getName();
    public static final String VERSION = PATH.substring(PATH.lastIndexOf(".") + 1, PATH.length());
    private static Method HANDLE_METHOD;
    private static Field PLAYER_CONNECTION_FIELD;
    private static Field NETWORK_MANAGER_FIELD;
    private static Field CHANNEL_FIELD;

    static {
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
            HANDLE_METHOD = craftPlayerClass.getMethod("getHandle");
            final Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + VERSION + ".EntityPlayer");
            PLAYER_CONNECTION_FIELD = entityPlayerClass.getField("playerConnection");
            final Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + VERSION + ".PlayerConnection");
            NETWORK_MANAGER_FIELD = playerConnectionClass.getField("networkManager");
            final Class<?> networkManagerClass = Class.forName("net.minecraft.server." + VERSION + ".NetworkManager");
            CHANNEL_FIELD = networkManagerClass.getField("channel");
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
          AntiRightClick.getInstance().getLogger().log(Level.SEVERE, "There was an error while initializing the NMS classes!", e);
        }
    }

    public static Channel getPlayersNettyChannel(final Player player) {
        try {
            final Object entityPlayer = HANDLE_METHOD.invoke(player);
            final Object playerConnection = PLAYER_CONNECTION_FIELD.get(entityPlayer);
            final Object networkManger = NETWORK_MANAGER_FIELD.get(playerConnection);
            return (Channel) CHANNEL_FIELD.get(networkManger);
        } catch (final  IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
