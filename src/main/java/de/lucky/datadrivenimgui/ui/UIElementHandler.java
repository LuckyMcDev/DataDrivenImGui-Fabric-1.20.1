package de.lucky.datadrivenimgui.ui;

import de.lucky.datadrivenimgui.config.ConfigManager;
import de.lucky.datadrivenimgui.config.UIConfig;
import de.lucky.datadrivenimgui.config.ElementConfig;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class UIElementHandler {

    // Handle different types of UI elements
    public static void handleElement(ElementConfig element, UIConfig config) {
        switch (element.type.toLowerCase()) {
            case "text":
                ImGui.text(element.content != null ? element.content : "");
                break;
            case "button":
                handleButton(element, config);
                break;
            case "checkbox":
                handleCheckbox(element, config);
                break;
            case "sliderint":
                handleSliderInt(element, config);
                break;
            case "sliderfloat":
                handleSliderFloat(element, config);
                break;
            case "inputtext":
                handleInputText(element, config);
                break;
            case "coloredit3":
                handleColorEdit3(element, config);
                break;
            case "treenode":
                handleTreeNode(element, config);
                break;
            default:
                ImGui.text("Unknown element type: " + element.type);
        }
    }

    private static void handleButton(ElementConfig element, UIConfig config) {
        if (ImGui.button(element.label != null ? element.label : "Button")) {
            // If a command is defined for the button, execute it
            if (element.commandToRun != null && !element.commandToRun.isEmpty()) {
                executeCommand(element.commandToRun);
            }
        }
    }

    private static void handleCheckbox(ElementConfig element, UIConfig config) {
        // Ensure the checkbox state is correct before rendering
        boolean[] checked = new boolean[] { element.checked != null ? element.checked : false };

        // Render the checkbox and check if it was clicked
        if (ImGui.checkbox(element.label != null ? element.label : "Checkbox", checked[0])) {
            element.checked = checked[0];  // Save the checkbox state back to the config
            ConfigManager.saveUIConfig(config);  // Save the updated config

            // If the checkbox is now checked, run the associated command
            if (element.checked && element.commandToRun != null && !element.commandToRun.isEmpty()) {
                executeCommand(element.commandToRun);
            }
        }
    }

    private static void handleSliderInt(ElementConfig element, UIConfig config) {
        int[] intValue = new int[] { element.defaultValue != null ? element.defaultValue.intValue() : 0 };
        int min = element.min != null ? element.min.intValue() : 0;
        int max = element.max != null ? element.max.intValue() : 100;
        if (ImGui.sliderInt(element.label != null ? element.label : "Slider Int", intValue, min, max)) {
            element.defaultValue = (float) intValue[0];
            ConfigManager.saveUIConfig(config);  // Save the updated config
        }
    }

    private static void handleSliderFloat(ElementConfig element, UIConfig config) {
        float[] floatValue = new float[] { element.defaultValue != null ? element.defaultValue : 0.0f };
        float min = element.min != null ? element.min : 0.0f;
        float max = element.max != null ? element.max : 1.0f;
        if (ImGui.sliderFloat(element.label != null ? element.label : "Slider Float", floatValue, min, max)) {
            element.defaultValue = floatValue[0];
            ConfigManager.saveUIConfig(config);  // Save the updated config
        }
    }

    private static void handleInputText(ElementConfig element, UIConfig config) {
        // Handle input text, using a buffer to store input dynamically
        String initialText = element.defaultText != null ? element.defaultText : "";
        ImString imString = new ImString(initialText);  // Initialize ImString with the current text

        // Create a fixed buffer for the input
        boolean inputChanged = ImGui.inputText(element.label != null ? element.label : "Input Text", imString);

        // If the text was modified, update the config value
        if (inputChanged) {
            element.defaultText = imString.get();  // Get the updated string from ImString
            // Save config to file to persist the updated value
            ConfigManager.saveUIConfig(ConfigManager.UI_CONFIG);
        }
    }

    private static void handleColorEdit3(ElementConfig element, UIConfig config) {
        float[] color = new float[3];
        if (element.defaultColor != null && element.defaultColor.size() >= 3) {
            color[0] = element.defaultColor.get(0);
            color[1] = element.defaultColor.get(1);
            color[2] = element.defaultColor.get(2);
        } else {
            color[0] = color[1] = color[2] = 1.0f;
        }
        if (ImGui.colorEdit3(element.label != null ? element.label : "Color Edit", color)) {
            element.defaultColor.set(0, color[0]);
            element.defaultColor.set(1, color[1]);
            element.defaultColor.set(2, color[2]);
            ConfigManager.saveUIConfig(config);  // Save the updated config
        }
    }

    private static void handleTreeNode(ElementConfig element, UIConfig config) {
        if (ImGui.treeNode(element.label != null ? element.label : "Tree Node")) {
            ImGui.text(element.content != null ? element.content : "Tree Node Content");
            ImGui.treePop();
        }
    }

    private static void executeCommand(String commandToRun) {
        // Check if the player is online and connected
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            networkHandler.sendChatCommand(commandToRun);  // Send the command to the server
        }
    }
}
