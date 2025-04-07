package de.lucky.datadrivenimgui.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DDIGParser {

    // Pattern to match DSL commands like: DDIG.begin("My Window")
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^DDIG\\.(\\w+)\\((.*)\\)$");
    // Pattern to extract window titles from lines starting with DDIG.begin("...")
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

            // For simplicity, assume one command per line.
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
                case "button":
                    if (currentWindow != null) {
                        String label = stripQuotes(params);
                        // For simplicity, assign a default callback that prints a message.
                        currentWindow.addButton(label, () -> {
                            System.out.println("Button '" + label + "' pressed.");
                        });
                    }
                    break;
                case "runs":
                    // You could attach a callback here if you wish.
                    System.out.println("Button command: " + stripQuotes(params));
                    break;
                case "endwindow":
                    if (currentWindow != null) {
                        currentWindow.endWindow();
                        currentWindow = null;
                    }
                    break;
                default:
                    System.err.println("Unknown DSL command: " + command);
            }
        }
        // Ensure any open window is closed
        if (currentWindow != null && currentWindow.isOpen()) {
            currentWindow.endWindow();
        }
    }

    private static String stripQuotes(String s) {
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * Extracts all window titles from the DSL script.
     */
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

    // Optional: if you want to load the script from file directly.
    public static String loadScriptFromFile(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
