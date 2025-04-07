package de.lucky.datadrivenimgui.config;

import imgui.ImGui;

public class DataDrivenImGuiWindow {
    private boolean open;

    public DataDrivenImGuiWindow(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public DataDrivenImGuiWindow addText(String text) {
        ImGui.text(text);
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
        if (open) {
            ImGui.end();
            open = false;
        }
    }
}
