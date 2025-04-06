package de.lucky.datadrivenimgui.mixin;

import de.lucky.datadrivenimgui.config.ConfigManager;
import de.lucky.datadrivenimgui.config.ElementConfig;
import de.lucky.datadrivenimgui.config.UIConfig;
import de.lucky.datadrivenimgui.config.WindowConfig;
import de.lucky.datadrivenimgui.kubejs.DataDrivenImGuiEvents;
import de.lucky.datadrivenimgui.ui.UIElementHandler;
import de.lucky.datadrivenimgui.imgui.ImGuiImpl;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

@Mixin(Screen.class)
public class ImGuiRenderingMixin {

    // Stores which windows are visible
    private static final Map<String, boolean[]> windowVisibility = new HashMap<>();

    // Persist the text editor instance and the text buffer
    private static final TextEditor textEditor = new TextEditor();
    private static StringBuilder textBuffer = new StringBuilder();
    private static String lastText = "";  // Track the last state of the text to avoid appending

    // Path to the ImGui config file
    private static final String configFilePath = Paths.get("config", "imgui_ui_config.json").toString(); // Set this to your actual file path
    private static int MAX_TEXT_LENGTH = 2000;

    // Flag to control the visibility of the config editor
    private static boolean configEditorVisible = false;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        UIConfig config = ConfigManager.loadUIConfig();

        // Load file content into the text editor
        if (textBuffer.length() == 0) {  // Only load the file once to avoid re-reading each frame
            loadFileContent();

        }

        ImGuiImpl.draw(io -> {
            // Menu bar to toggle windows
            if (ImGui.beginMainMenuBar()) {
                if (ImGui.beginMenu("Windows")) {
                    if (config.windows != null) {
                        for (WindowConfig window : config.windows) {
                            String title = window.title != null ? window.title : "Unnamed Window";
                            windowVisibility.putIfAbsent(title, new boolean[]{true});
                            boolean[] visible = windowVisibility.get(title);
                            if (ImGui.menuItem(title, "", visible[0])) {
                                visible[0] = !visible[0]; // toggle on click
                            }
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

            // Ensure that the editor reflects the current state of the buffer
            if (!lastText.equals(textBuffer.toString())) {
                textEditor.setText(getCappedText(textBuffer.toString()));  // Only update the editor if the text has changed
                lastText = textBuffer.toString();  // Update the lastText with the current buffer
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

            // Render windows if visible
            if (config.windows != null) {
                for (WindowConfig window : config.windows) {
                    String title = window.title != null ? window.title : "Unnamed Window";
                    windowVisibility.putIfAbsent(title, new boolean[]{true});
                    boolean[] visible = windowVisibility.get(title);

                    if (visible[0]) {
                        boolean open = ImGui.begin(title);
                        if (open && window.elements != null) {
                            for (ElementConfig element : window.elements) {
                                UIElementHandler.handleElement(element, config);
                            }
                        }
                        ImGui.end(); // Always call end, regardless of open
                    }
                }
            }
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

