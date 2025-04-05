package de.lucky.imguijs.kubejs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lucky.imguijs.config.UIConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KubeJSConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path KUBEJS_CONFIG_PATH = Paths.get("kubejs", "config", "imgui_ui_config.json");

    public static UIConfig loadIfPresent(UIConfig fallback) {
        if (Files.exists(KUBEJS_CONFIG_PATH)) {
            try {
                String json = Files.readString(KUBEJS_CONFIG_PATH);
                return GSON.fromJson(json, UIConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fallback;
    }
}
