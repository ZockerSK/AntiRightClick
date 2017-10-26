package ch.pineirohosting.arc;

import ch.pineirohosting.arc.handler.IncomingPacketDecoder;
import ch.pineirohosting.arc.listener.AntiRightClickListener;
import ch.pineirohosting.arc.listener.PlayerJoinListener;
import ch.pineirohosting.arc.stats.Stats;
import ch.pineirohosting.arc.util.AutoUpdater;
import ch.pineirohosting.arc.util.NMSUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.management.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiRightClick extends JavaPlugin {

    private final String prefix = "[" + this.getClass().getSimpleName() + "] ";

    private static AntiRightClick instance;
    private final int counter = this.getConfig().getInt("counter");
    private final List<String> packetsToCheck = Arrays.asList("PacketPlayInFlying", // 1.8.x
            "PacketPlayInPosition", "PacketPlayInPositionLook"); // 1.9.x - 1.12.x
    private final Map<Player, IncomingPacketDecoder> playersChannelHandler = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if (!(NMSUtil.VERSION.contains("1_8") || NMSUtil.VERSION.contains("1_9") ||
                NMSUtil.VERSION.contains("1_10") || NMSUtil.VERSION.contains("1_11") ||
                NMSUtil.VERSION.contains("1_12"))) {
            this.getServer().getConsoleSender().sendMessage(this.prefix + "This plugin doesn't support " +
                    NMSUtil.VERSION + ", disabling the plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        if (this.getConfig().getBoolean("autokick.enable")) {
            this.getServer().getPluginManager().registerEvents(new AntiRightClickListener(), this);
        }

        for (Player player : this.getServer().getOnlinePlayers()) {
            NMSUtil.getPlayersNettyChannel(player).pipeline()
                    .addAfter("decoder", "arc", this.playersChannelHandler.get(player));
        }

        this.getServer().getConsoleSender().sendMessage(
                this.prefix + "Plugin by GalaxyHDm & Zocker_SK successfully loaded!");
        this.getServer().getConsoleSender().sendMessage(
                this.prefix + "Please visit for more information: https://github.com/ZockerSK/AntiRightClick");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getConfig().getBoolean("stats.enable")) {
                    try {
                        new Stats().send();
                    } catch (IOException | KeyStoreException | CertificateException | KeyManagementException
                            | NoSuchAlgorithmException | ReflectionException | AttributeNotFoundException
                            | InstanceNotFoundException | MalformedObjectNameException | MBeanException
                            | InterruptedException e) {
                        System.err.println(prefix + "An error occurred while submitting stats...");
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                try {
                    new AutoUpdater().checkForUpdate();
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDisable() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            NMSUtil.getPlayersNettyChannel(player).pipeline().remove(this.playersChannelHandler.get(player));
        }
    }

    public static AntiRightClick getInstance() {
        return instance;
    }

    public int getCounter() {
        return counter;
    }

    public List<String> getPacketsToCheck() {
        return packetsToCheck;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<Player, IncomingPacketDecoder> getPlayersChannelHandler() {
        return playersChannelHandler;
    }
}
