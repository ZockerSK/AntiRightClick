package ch.pineirohosting.arc.stats;

import ch.pineirohosting.arc.AntiRightClick;
import ch.pineirohosting.arc.util.OSUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import javax.management.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class Stats {

    private SSLContext context;
    private final boolean send;

    public Stats() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException,
            KeyManagementException {
        if (AntiRightClick.getInstance().getConfig().contains("stats.timestamp")) {
            final long timestamp = AntiRightClick.getInstance().getConfig().getLong("stats.timestamp");
            if (timestamp - System.currentTimeMillis() >= 604800000) {
                send = false;
                return;
            } else {
                send = true;
            }
        } else {
            send = true;
        }

        final String javaVersion = System.getProperty("java.version");
        final int subVersion = Integer.parseInt(javaVersion.split("_")[1]);
        final int version = Integer.parseInt(javaVersion.split("1.")[1].substring(0, 1));
        if ((version == 7 && subVersion < 111) || (version == 8 && subVersion < 101)) {
            Bukkit.getConsoleSender().sendMessage("§c" + AntiRightClick.getInstance().getPrefix() +
                    "You are using a very old Java version! (" + javaVersion + ") - please consider to update!");

            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            final Certificate certificate;
            try (final InputStream stream =
                         new URL("https://letsencrypt.org/certs/lets-encrypt-x3-cross-signed.der").openStream()) {
                certificate = certificateFactory.generateCertificate(stream);
            }

            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), null);
        } else {
            context = null;
        }
    }

    public void send() throws IOException, MalformedObjectNameException, AttributeNotFoundException, MBeanException,
            ReflectionException, InstanceNotFoundException {
        if (!send)
            return;

        final String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        if (osArch.equalsIgnoreCase("x86_64"))
            osArch = "amd64";

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type",
                "OperatingSystem"), "TotalPhysicalMemorySize");

        final String osVersion = System.getProperty("os.version");
        final String javaVersion = System.getProperty("java.version");
        final int coreCount = Runtime.getRuntime().availableProcessors();
        final long maxMemory = Long.parseLong(attribute.toString());
        final String cpuName = OSUtil.getProcessorName();
        final int cpuMHz = OSUtil.getCPUMHz();

        final JsonObject stats = new JsonObject();

        final boolean notFound = AntiRightClick.getInstance().getConfig()
                .getString("stats.uuid") == null;

        stats.addProperty("osName", osName);
        stats.addProperty("osArch", osArch);
        stats.addProperty("osVersion", osVersion);
        stats.addProperty("javaVersion", javaVersion);
        stats.addProperty("coreCount", coreCount);
        stats.addProperty("maxMemory", maxMemory);
        stats.addProperty("cpuName", cpuName);
        stats.addProperty("cpuMHz", cpuMHz);
        stats.addProperty("uuid", (notFound ? "not-found" : AntiRightClick.getInstance().getConfig()
                .getString("stats.uuid")));

        final HttpsURLConnection connection = (HttpsURLConnection)
                new URL("https://arc.zockersk.ovh/update-stats").openConnection();
        if (context != null)
            connection.setSSLSocketFactory(connection.getSSLSocketFactory());
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        try (final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(stats.toString());
        }

        if (connection.getResponseCode() == 500)
            throw new IllegalStateException("Stats server returned 500.. An error is not cool...");

        try (final InputStreamReader reader = new InputStreamReader(
                (connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream()))) {
            final JsonObject response = (JsonObject) new JsonParser().parse(reader);
            final String uuid = response.get("uuid").getAsString();
            if (uuid.trim().isEmpty())
                throw new IllegalArgumentException("UUID is invalid! Did you change it?");
            if (notFound)
                AntiRightClick.getInstance().getConfig().set("stats.uuid", uuid);
            AntiRightClick.getInstance().getConfig().set("stats.timestamp", response.get("timestamp").getAsLong());
            AntiRightClick.getInstance().getConfig().save(new File("plugins/AntiRightClick/config.yml"));
        }


    }

}
