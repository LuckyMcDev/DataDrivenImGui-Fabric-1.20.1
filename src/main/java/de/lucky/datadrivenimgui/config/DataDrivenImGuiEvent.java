package de.lucky.datadrivenimgui.config;

import imgui.ImGui;

public class DataDrivenImGuiEvent {
    private DataDrivenImGuiWindow currentWindow;

    public DataDrivenImGuiWindow beginWindow(String title) {
        boolean opened = ImGui.begin(title);
        DataDrivenImGuiWindow window = new DataDrivenImGuiWindow(opened);
        if (opened) {
            currentWindow = window;
        }
        return window;
    }

    public void finishWindow() {
        if (currentWindow != null && currentWindow.isOpen()) {
            currentWindow.endWindow();
            currentWindow = null;
        }
    }
}
