package de.lucky.datadrivenimgui.mixin;

import de.lucky.datadrivenimgui.config.ConfigManager;
import de.lucky.datadrivenimgui.config.ElementConfig;
import de.lucky.datadrivenimgui.config.UIConfig;
import de.lucky.datadrivenimgui.config.WindowConfig;
import de.lucky.datadrivenimgui.ui.UIElementHandler;
import de.lucky.datadrivenimgui.imgui.ImGuiImpl;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import imgui.ImGui;

@Mixin(GameRenderer.class)
public class GameRenderMixin {

    @Inject(method = "render", at = @At("RETURN"))
    public void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        UIConfig config = ConfigManager.loadUIConfig();

        ImGuiImpl.draw(io -> {
            if (config.windows != null) {
                for (WindowConfig window : config.windows) {
                    if (ImGui.begin(window.title)) {
                        if (window.elements != null) {
                            for (ElementConfig element : window.elements) {
                                // Use the UIElementHandler to handle the element
                                UIElementHandler.handleElement(element, config);
                            }
                        }
                    }
                    ImGui.end();
                }
            }
        });
    }
}
