package de.lucky.imguijs.mixin;


import de.lucky.imguijs.imgui.ImGuiImpl;
import imgui.ImGui;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderMixin {

    @Inject(method = "render", at = @At("RETURN"))
    public void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {

        ImGuiImpl.draw(io -> {
            ImGui.begin("Hello");
        });
    }
}
