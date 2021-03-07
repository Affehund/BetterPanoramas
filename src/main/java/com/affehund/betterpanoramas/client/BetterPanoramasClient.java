package com.affehund.betterpanoramas.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.affehund.betterpanoramas.BetterPanoramas;
import com.affehund.betterpanoramas.ModConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * @author Affehund
 *
 */
@OnlyIn(Dist.CLIENT)
public class BetterPanoramasClient {
	public final IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private static final Path FOLDER = Minecraft.getInstance().gameDir.toPath()
			.resolve(new File(ModConstants.MOD_ID).toPath());

	private boolean isTakingPanorama;
	private int imageNumber = 0;
	private Vector3d positionVector;

	private Vector3f[] rotations = new Vector3f[] { new Vector3f(0, 0, 0), new Vector3f(90, 0, 0),
			new Vector3f(180, 0, 0), new Vector3f(-90, 0, 0), new Vector3f(0, -90, 0), new Vector3f(0, 90, 0) };
	private String timestamp;

	private static HashMap<Path, DynamicTexture[]> skyboxMap = new HashMap<>();

	private static final KeyBinding takePanoramaKey = new KeyBinding(ModConstants.KEYBIND_NAME, GLFW.GLFW_KEY_G,
			"key.categories.misc");

	public void setup() {
		BetterPanoramas.LOGGER.info("Client setup");
		FORGE_EVENT_BUS.addListener(this::openGui);
		FORGE_EVENT_BUS.addListener(this::keyInput);
		FORGE_EVENT_BUS.addListener(this::renderWorldLast);
		FORGE_EVENT_BUS.addListener(this::modifyFOV);
		FORGE_EVENT_BUS.addListener(this::setupCamera);
		ClientRegistry.registerKeyBinding(takePanoramaKey);
		ModLoadingContext.get().registerConfig(Type.COMMON, BetterPanoramasConfig.CLIENT_CONFIG_SPEC,
				ModConstants.CLIENT_CONFIG_NAME);
	}

	private void openGui(GuiOpenEvent event) {
		if (event.getGui() != null && event.getGui() instanceof MainMenuScreen) {
			setPanorama((MainMenuScreen) event.getGui());
		}
	}

	private void keyInput(KeyInputEvent event) {
		if (takePanoramaKey.isPressed() && !isTakingPanorama) {
			Minecraft mc = Minecraft.getInstance();
			timestamp = DATE_FORMAT.format(System.currentTimeMillis()).toString();
			isTakingPanorama = true;
			imageNumber = 0;

			if (mc.getRenderViewEntity() != null) {
				positionVector = mc.getRenderViewEntity().getPositionVec();
			} else {
				positionVector = mc.player != null ? mc.player.getPositionVec() : Vector3d.ZERO;
			}
			BetterPanoramas.LOGGER.debug("Pressed key: " + takePanoramaKey.getTranslationKey());
		}
	}

	private void renderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.world != null && isTakingPanorama) {
			takeImage(FOLDER, imageNumber, imageNumber == (rotations.length - 2));
			isTakingPanorama = imageNumber < rotations.length;
			mc.gameSettings.hideGUI = isTakingPanorama;
		}
	}

	private void setupCamera(EntityViewRenderEvent.CameraSetup cameraSetup) {
		if (isTakingPanorama) {
			Vector3f rotation = rotations[imageNumber];
			cameraSetup.setYaw(rotation.getX());
			cameraSetup.setPitch(rotation.getY());
			cameraSetup.setRoll(rotation.getZ());
			cameraSetup.getInfo().setPosition(positionVector);
		}
	}

	private void modifyFOV(EntityViewRenderEvent.FOVModifier fovModifier) {
		if (isTakingPanorama) {
			fovModifier.setFOV(90);
		}
	}

	private void setPanorama(MainMenuScreen screen) {
		DynamicTexture[] textures = BetterPanoramasConfig.CLIENT_CONFIG.OVERRIDE_VANILLA_PANORAMA.get()
				? getPanoramaTextures()
				: null;
		MainMenuScreen.PANORAMA_RESOURCES = textures != null ? new CustomRenderSkyboxCube(textures)
				: new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
		if (screen != null) {
			screen.panorama = new RenderSkybox(MainMenuScreen.PANORAMA_RESOURCES);
		}
	}

	@Nullable
	private DynamicTexture[] getPanoramaTextures() {
		Random random = new Random();
		try {
			File FILE = FOLDER.toFile();
			if (!FILE.exists()) {
				if (!FILE.mkdirs()) {
					BetterPanoramas.LOGGER.error("Failed to create panorama save folder: {}", FOLDER.toAbsolutePath());
					return null;
				}
			}
			Path[] paths = Files.list(FOLDER).filter(path -> {
				for (int i = 0; i < 6; i++) {
					if (!path.resolve(String.format("panorama_%d.png", i)).toFile().exists()) {
						return false;
					}
				}
				return true;
			}).toArray(Path[]::new);

			if (paths.length == 0) {
				return null;
			} else {
				Path randomPanoramas = paths[random.nextInt(paths.length)];
				return skyboxMap.computeIfAbsent(randomPanoramas, (path) -> {
					try {
						DynamicTexture[] textures = new DynamicTexture[6];
						for (int i = 0; i < textures.length; i++) {
							InputStream stream = Files
									.newInputStream(path.resolve(String.format("panorama_%d.png", i)));
							NativeImage image = NativeImage.read(stream);
							textures[i] = new DynamicTexture(image);
							image.close();
							stream.close();
						}
						return textures;
					} catch (Exception e) {
						return null;
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void takeImage(Path folder, int stage, boolean sendSucessMessage) {
		Minecraft mc = Minecraft.getInstance();
		NativeImage image = ScreenShotHelper.createScreenshot(mc.getMainWindow().getFramebufferWidth(),
				mc.getMainWindow().getFramebufferHeight(), mc.getFramebuffer());
		saveImage(image, folder, stage, sendSucessMessage);
		imageNumber++;
	}

	private void saveImage(NativeImage image, Path folder, int stage, boolean sendSucessMessage) {
		Util.getRenderingService().execute(() -> {
			Minecraft mc = Minecraft.getInstance();
			try {
				Path subFolder = FOLDER.resolve(String.format("%s", timestamp));
				File subFile = subFolder.toFile();

				if (!subFile.exists() || !subFile.isDirectory()) {
					if (!subFile.mkdirs()) {
						throw new IOException(String.format("Couldn't create folder %s", subFolder.toAbsolutePath()));
					}
				}

				int width = image.getWidth();
				int height = image.getHeight();
				int x = 0;
				int y = 0;

				if (width > height) {
					x = (width - height) / 2;
					width = height;
				} else {
					y = (height - width) / 2;
					height = width;
				}

				NativeImage savedImage = new NativeImage(width, height, false);
				image.resizeSubRectTo(x, y, width, height, savedImage);
				savedImage.write(subFolder.resolve("panorama_" + stage + ".png"));

				BetterPanoramas.LOGGER.debug("Sucessfully saved image!");
				if (sendSucessMessage) {
					ITextComponent textComponent = (new StringTextComponent(subFile.getName()))
							.mergeStyle(TextFormatting.UNDERLINE).modifyStyle((style) -> style
									.setClickEvent(new ClickEvent(Action.OPEN_FILE, subFile.getAbsolutePath())));
					mc.execute(() -> mc.ingameGUI.getChatGUI()
							.printChatMessage(new TranslationTextComponent("betterpanoramas.success", textComponent)));
				}
			} catch (IOException e) {
				BetterPanoramas.LOGGER.warn("Couldn't save image", e);
				mc.execute(() -> mc.ingameGUI.getChatGUI()
						.printChatMessage(new TranslationTextComponent("betterpanoramas.failed")));
			} finally {
				image.close();
			}
		});
	}
}
