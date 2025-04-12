package de.lucky.datadrivenimgui.mixin;

import de.lucky.datadrivenimgui.imgui.ImGuiImpl;
import de.lucky.datadrivenimgui.script.ImGuiScriptExecutor;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Paths;

@Mixin(GameRenderer.class)
public abstract class ImGuiRenderingMixin {

    @Shadow public abstract void loadProjectionMatrix(Matrix4f projectionMatrix);
    private static final String configFilePath = Paths.get("config", "gui.groovy").toString();


    @Inject(method = "render", at = @At("RETURN"))
    public void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {

        if (ImGuiScriptExecutor.isImGuiEnabled()) {
            // Only render ImGui if it's enabled
            ImGuiImpl.draw(io -> {
                ImGuiScriptExecutor.executeImGuiScript(configFilePath);
                // Your ImGui rendering code
            });
        }
    }
}
