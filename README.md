# AntiRightClick
This is a simple plugin written in Java 7, which fixes the following bug (Click to watch):

[![Watch the demo on YouTube](https://img.youtube.com/vi/PgYCARfMMv4/0.jpg)](https://youtu.be/PgYCARfMMv4)

You simply have to put this plugin into your plugin folder of Bukkit/Spigot.

**API**

If the counter was reached, the ``AntiRightClickEvent`` will be called. For default, an autokick is enabled - you are able to deactivate this in the config.yml

Example to use:
```Java
package your.package;

import ch.pineirohosting.arc.events.AntiRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AntiRightClickListener implements Listener {

    @EventHandler
    public void onDetection(final AntiRightClickEvent event) {
        event.getPlayer().kickPlayer("§cYou have been kicked for using the Rightclick-Bug, counter:"
            + event.getCounter());
    }

}

```

**Supported Versions:**
 - 1.8.x
 - 1.9.x
 - 1.10.x
 - 1.11.x
 - 1.12 - 1.12.1
 
 This plugin was successfully tested with ViaVersion.
 
 Please don't hestitate to open an issue, in case of an error
 
 **Note**
 
 This plugin will kick players, which are changing their shaders - a bugfix will come soon.
 We are looking forward hearing suggestions to this bug 
