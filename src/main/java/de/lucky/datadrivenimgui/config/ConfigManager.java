package de.lucky.datadrivenimgui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "imgui_ui_config.json");

    // Cached config object
    public static UIConfig UI_CONFIG = null;

    public static UIConfig loadUIConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                UI_CONFIG = GSON.fromJson(json, UIConfig.class);
            } else {
                UI_CONFIG = createDefaultUIConfig();
                saveUIConfig(UI_CONFIG);
            }
        } catch (IOException e) {
            e.printStackTrace();
            UI_CONFIG = createDefaultUIConfig();
        }

        return UI_CONFIG;
    }

    private static UIConfig createDefaultUIConfig() {
        UIConfig config = new UIConfig();
        WindowConfig defaultWindow = new WindowConfig();
        defaultWindow.title = "Configured Window";
        defaultWindow.elements = new ArrayList<>();

        // Default text element
        ElementConfig textElement = new ElementConfig();
        textElement.type = "text";
        textElement.content = "Hello from Config!";
        defaultWindow.elements.add(textElement);

        // Default button element
        ElementConfig buttonElement = new ElementConfig();
        buttonElement.type = "button";
        buttonElement.label = "Click Me";
        defaultWindow.elements.add(buttonElement);

        config.windows = Collections.singletonList(defaultWindow);
        return config;
    }

    public static void saveUIConfig(UIConfig config) {
        try {
            if (!Files.exists(CONFIG_PATH.getParent())) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
