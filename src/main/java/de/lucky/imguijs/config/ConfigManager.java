package de.lucky.imguijs.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lucky.imguijs.kubejs.KubeJSConfigLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "imgui_ui_config.json");

    // Cached config object
    public static UIConfig UI_CONFIG = null;

    public static UIConfig loadUIConfig() {
        Path kubejsConfigPath = Paths.get("kubejs", "config", "imgui_ui_config.json");

        try {
            if (isKubeJSInstalled() && Files.exists(kubejsConfigPath)) {
                String json = Files.readString(kubejsConfigPath);
                UI_CONFIG = GSON.fromJson(json, UIConfig.class);
            } else if (Files.exists(CONFIG_PATH)) {
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
        defaultWindow.elements = new java.util.ArrayList<>();

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

        config.windows = java.util.Collections.singletonList(defaultWindow);
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

    private static UIConfig loadUIConfigInternal() {
        UIConfig baseConfig;
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                baseConfig = GSON.fromJson(json, UIConfig.class);
            } else {
                baseConfig = createDefaultUIConfig();
                saveUIConfig(baseConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
            baseConfig = createDefaultUIConfig();
        }

        // Override with KubeJS config if it exists
        return KubeJSConfigLoader.loadIfPresent(baseConfig);
    }

    private static boolean isKubeJSInstalled() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("kubejs");
    }

    public static void maybeWriteExampleKubeJSScript() {
        if (!isKubeJSInstalled()) return;

        Path markerFile = Paths.get("config", "imgui_generated_marker.txt"); // marker to track if we've written the script
        if (Files.exists(markerFile)) return; // already ran once

        Path scriptPath = Paths.get("kubejs", "startup_scripts", "imgui_example.js");

        try {
            if (!Files.exists(scriptPath.getParent())) {
                Files.createDirectories(scriptPath.getParent());
            }

            String exampleScript = """
            // imgui_example.js - Example ImGuiJS configuration
            let config = {
                windows: [
                    {
                        title: "KubeJS Window",
                        elements: [
                            {
                                type: "text",
                                content: "Hello from KubeJS!"
                            },
                            {
                                type: "button",
                                label: "Say Hello",
                                commandToRun: "say Hello from ImGuiJS + KubeJS!"
                            },
                            {
                                type: "checkbox",
                                label: "Auto Hello",
                                commandToRun: "say This checkbox is enabled!"
                            }
                        ]
                    }
                ]
            };

            JsonIO.write('kubejs/config/imgui_ui_config.json', config);
            """;

            Files.writeString(scriptPath, exampleScript);

            // write marker so we know it's been generated
            Files.writeString(markerFile, "ImGuiJS example script has been written.");

            System.out.println("[ImGuiJS] Example KubeJS ImGui script written to startup_scripts/imgui_example.js");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
