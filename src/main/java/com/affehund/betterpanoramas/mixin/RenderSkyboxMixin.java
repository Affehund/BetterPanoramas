package com.affehund.betterpanoramas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.affehund.betterpanoramas.client.BetterPanoramasConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.math.MathHelper;

/**
 * @author Affehund
 *
 */
@Mixin(RenderSkybox.class)
public abstract class RenderSkyboxMixin {
	@Shadow
	protected RenderSkyboxCube renderer;

	@Shadow
	protected float time;

	@Redirect(method = "render(FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderSkyboxCube;render(Lnet/minecraft/client/Minecraft;FFF)V"))
	protected void render(RenderSkyboxCube renderer, Minecraft client, float x, float y, float alpha) {
		float swaySpeed = BetterPanoramasConfig.CLIENT_CONFIG.SWAY_SPEED.get().floatValue();
		float initialSwayProgress = BetterPanoramasConfig.CLIENT_CONFIG.INITIAL_SWAY_PROGRESS.get().floatValue();
		float swayAngle = BetterPanoramasConfig.CLIENT_CONFIG.SWAY_ANGLE.get().floatValue();
		float verticalAngle = BetterPanoramasConfig.CLIENT_CONFIG.VERTICAL_ANGLE.get().floatValue();
		float rotationSpeed = BetterPanoramasConfig.CLIENT_CONFIG.ROTATION_SPEED.get().floatValue();
		float startingHorizontalAngle = BetterPanoramasConfig.CLIENT_CONFIG.STARTING_HORIZONTAL_ANGLE.get()
				.floatValue();

		float drawX = MathHelper.sin((this.time * swaySpeed * 0.001F) - ((float) Math.PI * initialSwayProgress))
				* swayAngle - verticalAngle;
		float drawY = -this.time * 0.1F * rotationSpeed - startingHorizontalAngle;
		this.renderer.render(client, drawX, drawY, alpha);
	}
}
