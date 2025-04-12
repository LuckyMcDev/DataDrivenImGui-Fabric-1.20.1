package de.lucky.datadrivenimgui.config;

import imgui.ImGui;

import java.awt.*;

public class DataDrivenImGuiWindow {

    /*
    Things from imgui to add:

    ImGui.beginPopup()
    ImGui.endPopup()

     */
    private boolean open;

    public DataDrivenImGuiWindow(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public DataDrivenImGuiWindow setNextWindowSize(float width, float height ) {
        ImGui.setNextWindowSize(width, height);
        return this;
    }

    public DataDrivenImGuiWindow addText(String text) {
        ImGui.text(text);
        return this;
    }

    public DataDrivenImGuiWindow sameline() {
        ImGui.sameLine();
        return this;
    }

    public DataDrivenImGuiWindow addButton(String label, Runnable callback) {
        if (ImGui.button(label) && callback != null) {
            callback.run();
        }
        return this;
    }

    public DataDrivenImGuiWindow addCheckbox(String label, boolean defaultValue) {
        boolean[] value = new boolean[]{defaultValue};
        ImGui.checkbox(label, value[0]);
        return this;
    }

    public void endWindow() {
        if (!this.isOpen()) return; // or check if already ended
        // Clean up the window resources only if it hasn’t been collapsed
        ImGui.end();
        // update window state, etc.
    }
}
