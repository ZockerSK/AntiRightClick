package ch.pineirohosting.arc.listener;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.events.AntiRightClickEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AntiRightClickListener implements Listener {

    public AntiRightClickListener() {
        this.kickMessage = generateKickMessage();
    }

    private final String kickMessage;

    @EventHandler
    public void onDetection(final AntiRightClickEvent event) {
        event.getPlayer().kickPlayer(kickMessage);
    }

    private String generateKickMessage() {
        return ChatColor.translateAlternateColorCodes('&', StringUtils.join(AntiRightClick.getInstance()
                .getConfig().getList("autokick.message").iterator(), "\n"));
    }

}
