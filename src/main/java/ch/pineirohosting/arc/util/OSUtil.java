package ch.pineirohosting.arc.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class OSUtil {

    private static OSType getOSType() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows"))
            return OSType.WINDOWS;
        else if (osName.contains("Linux"))
            return OSType.LINUX;
        else
            return OSType.UNKNOWN;
    }

    private enum OSType {
        WINDOWS,
        LINUX,
        UNKNOWN
    }

    public static String getProcessorName() throws IOException {
        if (getOSType() == OSType.WINDOWS) {
            final Process process = new ProcessBuilder("cmd", "/c", "reg", "query",
                    "HKEY_LOCAL_MACHINE\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0", "/v",
                    "ProcessorNameString", "|", "findstr", "/ri", "\"REG_SZ\"").start();
            try (final Scanner scanner = new Scanner(process.getInputStream())) {
                String line = scanner.nextLine();
                if (line.contains("    ")) {
                    line = line.split("    ")[3];
                    return line;
                }
            }
        } else {
            try (final FileReader reader = new FileReader("/proc/cpuinfo")) {
                try (final Scanner scanner = new Scanner(reader)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("model name")) {
                            return line.split(":")[1].trim();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static int getCPUMHz() throws IOException {
        if (getOSType() == OSType.WINDOWS) {
            final Process process = new ProcessBuilder("cmd", "/c", "reg", "query",
                    "HKEY_LOCAL_MACHINE\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0", "/v",
                    "~MHz", "|", "findstr", "/ri", "\"REG_DWORD\"").start();
            try (final Scanner scanner = new Scanner(process.getInputStream())) {
                String line = scanner.nextLine();
                if (line.contains("    ")) {
                    line = line.split("    ")[3];
                    return Integer.decode(line);
                }
            }
        } else {
            try (final FileReader reader = new FileReader("/proc/cpuinfo")) {
                try (final Scanner scanner = new Scanner(reader)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("cpu MHz")) {
                            line = line.split(":")[1].trim();
                            return (int) Double.parseDouble(line);
                        }
                    }
                }
            }
        }
        return -1;
    }
}
