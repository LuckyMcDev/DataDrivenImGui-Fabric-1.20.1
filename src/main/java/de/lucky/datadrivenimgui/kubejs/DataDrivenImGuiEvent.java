package de.lucky.datadrivenimgui.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataDrivenImGuiEvent extends EventJS {
    // A static list to hold all registered callbacks.
    private static final List<Consumer<DataDrivenImGuiEvent>> CALLBACKS = new ArrayList<>();

    // Call this method from KubeJS scripts to register a drawing callback.
    public static void draw(Consumer<DataDrivenImGuiEvent> callback) {
        CALLBACKS.add(callback);
    }

    // This method is called during the render phase to execute all callbacks.
    public static void dispatch() {
        DataDrivenImGuiEvent event = new DataDrivenImGuiEvent();
        for (Consumer<DataDrivenImGuiEvent> callback : CALLBACKS) {
            try {
                callback.accept(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Optionally, you can clear the callbacks if needed:
    public static void clearCallbacks() {
        CALLBACKS.clear();
    }
}
