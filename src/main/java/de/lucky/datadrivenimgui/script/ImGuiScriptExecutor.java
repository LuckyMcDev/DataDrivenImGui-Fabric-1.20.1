package de.lucky.datadrivenimgui.script;

import de.lucky.datadrivenimgui.util.CommandUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Executes JavaScript code that renders ImGui UI elements.
 */
public class ImGuiScriptExecutor {
    // Flag to control whether ImGui rendering is enabled
    private static boolean isImGuiEnabled = true;

    public static void executeImGuiScript(String filePath) {
        try {
            // Read the content of the ImGui script file
            String script = new String(Files.readAllBytes(Paths.get(filePath)));

            // Only execute if ImGui is enabled
            if (isImGuiEnabled) {
                // Create a GroovyShell instance
                GroovyShell shell = new GroovyShell();

                shell.setVariable("CommandUtils", new CommandUtils());

                shell.setVariable("ImGui", ImGui.class);

                //shell.setVariable("MinecraftClient", MinecraftClient.class);

                // Try to execute the script
                try {
                    shell.evaluate(script);
                } catch (Exception e) {
                    // Log the error but prevent crash
                    System.err.println("Error executing ImGui script: " + e.getMessage());
                    e.printStackTrace();

                    // Disable ImGui rendering on error
                    isImGuiEnabled = false;
                }
            }
        } catch (Exception e) {
            // Catch any exception related to file reading or script execution
            System.err.println("Error reading or executing ImGui script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to manually re-enable ImGui rendering once the script is fixed
    public static void enableImGuiRendering() {
        isImGuiEnabled = true;
    }

    // Method to manually check if ImGui rendering is enabled
    public static boolean isImGuiEnabled() {
        return isImGuiEnabled;
    }
}
