package ch.pineirohosting.arc.util;

import ch.pineirohosting.arc.AntiRightClick;
import com.google.common.io.CharStreams;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public class AutoUpdater {

    private String newVersion;

    public void checkForUpdate() throws IOException, URISyntaxException {
        final int localVersion = Integer.valueOf(AntiRightClick.getInstance().getDescription().getVersion()
                .replaceAll("\\.", ""));
        final HttpURLConnection connection = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
                .openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        int resourceID = 47045;
        connection.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4" +
                "&resource=" + resourceID).getBytes("UTF-8"));
        try (final InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            this.newVersion = CharStreams.toString(reader);
            final int onlineVersion = Integer.parseInt(this.newVersion.replaceAll("\\.", ""));
            if (onlineVersion > localVersion) {
                update();
            }
        }
    }

    private void update() throws IOException, URISyntaxException {
        if (!AntiRightClick.getInstance().getConfig().contains("autoUpdate.enable")) {
            AntiRightClick.getInstance().getConfig().set("autoUpdate.enable", true);
            AntiRightClick.getInstance().getConfig().set("autoUpdate.reloadAfterReload", true);
            AntiRightClick.getInstance().getConfig().set("autoUpdate.shutdownAfterReload", false);
            AntiRightClick.getInstance().getConfig().save(Paths.get("plugins/AntiRightClick/config.yml").toFile());
        }
        System.out.println(AntiRightClick.getInstance().getPrefix() + "An update was found!");
        if (AntiRightClick.getInstance().getConfig().getBoolean("autoUpdate")) {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://github.com/ZockerSK/" +
                            "AntiRightClick/releases/download/v%s/anti-right-click-%s.jar", this.newVersion,
                    this.newVersion)).openConnection();
            final Path newPath = Paths.get("update/AntiRightClick.jar");
            if (Files.exists(newPath))
                Files.delete(newPath);
            else
                newPath.toFile().mkdirs();

            Files.copy(connection.getInputStream(), newPath);
            try (final JarFile jarFile = new JarFile(newPath.toFile())) {
                // check if jar file is corrupt
            }
            final Path jarPath = Paths.get(AntiRightClick.getInstance().getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Files.delete(jarPath);
            Files.copy(newPath, jarPath);
            Bukkit.getScheduler().runTask(AntiRightClick.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (AntiRightClick.getInstance().getConfig().getBoolean("autoUpdate.reloadAfterUpdate"))
                        Bukkit.reload();
                    else if (AntiRightClick.getInstance().getConfig().getBoolean("autoUpdate.shutdownAfterUpdate"))
                        Bukkit.shutdown();
                    else
                        Bukkit.getConsoleSender().sendMessage("§a" + AntiRightClick.getInstance().getPrefix() +
                                "The plugin was updated.. You have to manually restart this server.");
                }
            });
        }
    }
}
