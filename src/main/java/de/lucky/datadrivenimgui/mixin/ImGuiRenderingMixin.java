// File: src/main/java/de/lucky/datadrivenimgui/mixin/ImGuiRendererMixin.java
package de.lucky.datadrivenimgui.mixin;

import de.lucky.datadrivenimgui.config.DDIGParser;
import de.lucky.datadrivenimgui.config.WindowVisibility;
import de.lucky.datadrivenimgui.config.ConfigManager;
import de.lucky.datadrivenimgui.config.DataDrivenImGuiEvent;
import de.lucky.datadrivenimgui.imgui.ImGuiImpl;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Mixin(GameRenderer.class)
public class ImGuiRenderingMixin {

    // Flag to control the visibility of the config editor
    private static boolean configEditorVisible = false;

    // Persist the text editor instance and the text buffer
    private static final TextEditor textEditor = new TextEditor();
    private static StringBuilder textBuffer = new StringBuilder();
    private static String lastText = "";  // Track the last state of the text to avoid appending

    // Path to the ImGui config file
    private static final String configFilePath = Paths.get("config", "gui.js").toString();
    private static int MAX_TEXT_LENGTH = 2000;



    @Inject(method = "render", at = @At("RETURN"))
    public void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        // Load the DSL script from config folder
        String ddigScript = ConfigManager.loadGuiScript();

        // Now, render the DSL-defined UI
        ImGuiImpl.draw(io -> {

            // Build main menu bar for window toggling and config editor toggle
            if (ImGui.beginMainMenuBar()) {
                if (ImGui.beginMenu("Windows")) {
                    // Extract window titles from the DSL script
                    List<String> titles = DDIGParser.getWindowTitles(ddigScript);
                    for (String title : titles) {
                        boolean visible = WindowVisibility.isVisible(title);
                        if (ImGui.menuItem(title, "", visible)) {
                            WindowVisibility.toggle(title);
                        }
                    }
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Debug")) {
                    if (ImGui.menuItem("Toggle Config Editor", "", configEditorVisible)) {
                        configEditorVisible = !configEditorVisible; // Toggle visibility
                    }
                    ImGui.endMenu();
                }
                ImGui.endMainMenuBar();
            }

            // Render the editor if visible
            if (configEditorVisible) {
                ImGui.begin("Live Config Editor [ONLY WORKS WITHOUT KUBEJS]");

                // Optionally, add a button to save the file
                if (ImGui.button("Save")) {
                    saveFileContent();  // Call the save method when the button is clicked
                }
                ImGui.sameLine();
                if (ImGui.button("Show WhiteSpaces")) {
                    textEditor.setShowWhitespaces(true);  // Show whitespaces
                }
                ImGui.sameLine();
                if (ImGui.button("Hide WhiteSpaces")) {
                    textEditor.setShowWhitespaces(false);  // Hide whitespaces
                }

                ImGui.beginChild("textEditor");
                textEditor.render("ConfigTextEditor");  // Render the editor
                ImGui.endChild();
                // Get the text from the editor and update the buffer only if changed
                String currentText = textEditor.getText();
                if (!currentText.equals(textBuffer.toString())) {
                    textBuffer.setLength(0);  // Reset the buffer to avoid appending
                    textBuffer.append(currentText);  // Update the buffer with the new text
                }

                ImGui.end();
            }


            DataDrivenImGuiEvent event = new DataDrivenImGuiEvent();
            DDIGParser.parseAndExecute(ddigScript, event);
        });


    }
    // Method to load the content of the file into the text editor
    private void loadFileContent() {
        try {
            // Read the content of the file into a string
            String fileContent = new String(Files.readAllBytes(Paths.get(configFilePath)));
            textBuffer.setLength(0);  // Clear the previous buffer
            textBuffer.append(fileContent);  // Load the content into the buffer
        } catch (IOException e) {
            e.printStackTrace();  // Handle file read error (log or show error in game)
        }
    }

    // Method to cap the text to a maximum length
    private String getCappedText(String text) {
        if (text.length() > MAX_TEXT_LENGTH) {
            return text.substring(0, MAX_TEXT_LENGTH);  // Limit the length to MAX_TEXT_LENGTH
        }
        return text;
    }

    // Method to save the content of the text buffer to the file
    private void saveFileContent() {
        try {
            // Write the content of the textBuffer to the file
            Files.write(Paths.get(configFilePath), textBuffer.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("File saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();  // Handle file write error (log or show error in game)
        }
    }
}
