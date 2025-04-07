// File: src/main/java/de/lucky/datadrivenimgui/WindowVisibility.java
package de.lucky.datadrivenimgui.config;

import java.util.HashMap;
import java.util.Map;

public class WindowVisibility {
    private static final Map<String, Boolean> visibilityMap = new HashMap<>();

    public static boolean isVisible(String title) {
        return visibilityMap.getOrDefault(title, true);
    }

    public static void toggle(String title) {
        visibilityMap.put(title, !isVisible(title));
    }

    public static Map<String, Boolean> getVisibilityMap() {
        return visibilityMap;
    }
}
