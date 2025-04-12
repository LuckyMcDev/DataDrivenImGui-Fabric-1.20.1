package de.lucky.datadrivenimgui.config;

import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DDIGParser {

    private static final Pattern CHAINED_PATTERN = Pattern.compile("^DDIG\\.button\\((.+)\\)\\.runs\\((.+)\\)$");
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^DDIG\\.(\\w+)\\((.*)\\)$");
    private static final Pattern BEGIN_PATTERN = Pattern.compile("^DDIG\\.begin\\((.*)\\)$");

    /**
     * Parses the DSL script and executes commands using the provided event.
     */
    public static void parseAndExecute(String script, DataDrivenImGuiEvent event) {
        DataDrivenImGuiWindow currentWindow = null;
        String[] lines = script.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) continue;

            // Support DDIG.button(...).runs(...)
            Matcher chain = CHAINED_PATTERN.matcher(line);
            if (chain.find()) {
                if (currentWindow != null) {
                    String label = stripQuotes(chain.group(1));
                    String command = stripQuotes(chain.group(2));
                    currentWindow.addButton(label, () -> runMinecraftCommand(command));
                }
                continue;
            }

            // Single DSL commands
            Matcher matcher = COMMAND_PATTERN.matcher(line);
            if (!matcher.find()) {
                System.err.println("Invalid DSL syntax: " + line);
                continue;
            }

            String command = matcher.group(1).toLowerCase();
            String params = matcher.group(2).trim();

            switch (command) {
                case "begin":
                    if (currentWindow != null && currentWindow.isOpen()) {
                        currentWindow.endWindow();
                    }
                    String title = stripQuotes(params);
                    if (!WindowVisibility.isVisible(title)) {
                        currentWindow = null;
                    } else {
                        currentWindow = event.beginWindow(title);
                    }
                    break;

                case "text":
                    if (currentWindow != null) {
                        currentWindow.addText(stripQuotes(params));
                    }
                    break;

                case "sameLine":
                    if (currentWindow != null) {
                        currentWindow.sameline();
                    }
                    break;

                case "button":
                    if (currentWindow != null) {
                        String label = stripQuotes(params);
                        currentWindow.addButton(label, () -> {
                            System.out.println("Button '" + label + "' clicked (default)");
                        });
                    }
                    break;

                case "end":
                    if (currentWindow != null) {
                        currentWindow.endWindow();
                        currentWindow = null;
                    }
                    break;

                default:
                    System.err.println("Unknown DSL command: " + command);
            }
        }

        if (currentWindow != null && currentWindow.isOpen()) {
            currentWindow.endWindow();
        }
    }

    private static void runMinecraftCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.networkHandler != null) {
            if (command.startsWith("/")) command = command.substring(1);
            client.player.networkHandler.sendChatCommand(command);
        } else {
            System.err.println("Cannot run command, player or network handler is null");
        }
    }

    private static String stripQuotes(String s) {
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static List<String> getWindowTitles(String script) {
        List<String> titles = new ArrayList<>();
        String[] lines = script.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("DDIG.begin(")) {
                Matcher m = BEGIN_PATTERN.matcher(line);
                if (m.find()) {
                    String title = stripQuotes(m.group(1));
                    titles.add(title);
                }
            }
        }
        return titles;
    }

    public static String loadScriptFromFile(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
