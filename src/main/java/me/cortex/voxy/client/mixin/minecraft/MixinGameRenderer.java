package me.cortex.voxy.client.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.VoxyRenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GameRenderer.class}, priority = 1100)
public class MixinGameRenderer {
    @WrapMethod(method = "getDepthFar()F")
    public float getDepthFar(Operation<Float> original) {
        if (VoxyConfig.CONFIG.isRenderingEnabled()) {
            return Math.max(original.call(), VoxyConfig.CONFIG.sectionRenderDistance * 32F * 4F);
        }
        return original.call();
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void voxy$captureProjectionMatrix(CallbackInfo ci) {
        // Capture the vanilla projection matrix before it gets modified
        // This includes viewbobbing and other effects injected by Minecraft
        var gameRenderer = (GameRenderer)(Object)this;
        var projMatrix = gameRenderer.getProjectionMatrix(1.0F); // 1.0F for current tick
        VoxyRenderSystem.setCapturedVanillaProjection(projMatrix);
    }
}
