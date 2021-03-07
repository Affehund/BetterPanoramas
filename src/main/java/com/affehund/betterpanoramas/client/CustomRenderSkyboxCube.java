package com.affehund.betterpanoramas.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * @author Affehund
 *
 */
public class CustomRenderSkyboxCube extends RenderSkyboxCube {
	DynamicTexture[] textures;

	public CustomRenderSkyboxCube(DynamicTexture[] textures) {
		super(new ResourceLocation("unknown"));
		if (textures.length != 6)
			throw new IllegalArgumentException("You need 6 textures to render a SkyboxCube!");
		this.textures = textures;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(Minecraft mc, float pitch, float yaw, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.multMatrix(Matrix4f.perspective(85.0D,
				(float) mc.getMainWindow().getFramebufferWidth() / (float) mc.getMainWindow().getFramebufferHeight(),
				0.05F, 10.0F));
		RenderSystem.matrixMode(5888);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableCull();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		for (int j = 0; j < 4; ++j) {
			RenderSystem.pushMatrix();
			float f = (j % 2 / 2.0F - 0.5F) / 256.0F;
			float f1 = (j / 2 / 2.0F - 0.5F) / 256.0F;
			RenderSystem.translatef(f, f1, 0.0F);
			RenderSystem.rotatef(pitch, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(yaw, 0.0F, 1.0F, 0.0F);
			for (int k = 0; k < 6; ++k) {
				this.textures[k].bindTexture();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int l = Math.round(255.0F * alpha) / (j + 1);
				if (k == 0) {
					bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
				}
				if (k == 1) {
					bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, 1.0D, -1.0D).tex(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, -1.0D, -1.0D).tex(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
				}
				if (k == 2) {
					bufferbuilder.pos(1.0D, -1.0D, -1.0D).tex(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, 1.0D, -1.0D).tex(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, 1.0D, -1.0D).tex(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, -1.0D, -1.0D).tex(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
				}
				if (k == 3) {
					bufferbuilder.pos(-1.0D, -1.0D, -1.0D).tex(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, 1.0D, -1.0D).tex(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
				}
				if (k == 4) {
					bufferbuilder.pos(-1.0D, -1.0D, -1.0D).tex(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, -1.0D, -1.0D).tex(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
				}
				if (k == 5) {
					bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(-1.0D, 1.0D, -1.0D).tex(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, 1.0D, -1.0D).tex(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
					bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
				}
				tessellator.draw();
			}
			RenderSystem.popMatrix();
			RenderSystem.colorMask(true, true, true, false);
		}

		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.matrixMode(5889);
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderSystem.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
	}
}
