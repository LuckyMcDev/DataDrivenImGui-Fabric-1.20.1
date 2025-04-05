package de.lucky.imguijs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import imgui.ImGui;
import imgui.ImGuiIO;

public class ImGuiRenderEventJS extends EventJS {
    public final ImGui imGui;

    public ImGuiRenderEventJS(ImGui imGui) {
        this.imGui = imGui;
    }
}
