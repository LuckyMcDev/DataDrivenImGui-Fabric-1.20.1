// File: src/main/java/de/lucky/datadrivenimgui/config/ConfigManager.java
package de.lucky.datadrivenimgui.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Path CONFIG_PATH = Paths.get("config", "gui.js");
    private static String cachedScript = "";

    public static String loadGuiScript() {
        try {
            cachedScript = Files.readString(CONFIG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            cachedScript = "";
        }
        return cachedScript;
    }

    public static void reloadGuiScript() {
        loadGuiScript();
    }
}
