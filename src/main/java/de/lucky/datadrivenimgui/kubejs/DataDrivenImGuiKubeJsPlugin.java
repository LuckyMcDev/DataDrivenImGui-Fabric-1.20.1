package de.lucky.datadrivenimgui.kubejs;

import de.lucky.datadrivenimgui.DataDrivenImGui;
import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import imgui.ImGui;

public class DataDrivenImGuiKubeJsPlugin extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        DataDrivenImGuiEventInterface.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("ImGui", ImGui.class);
    }
}
