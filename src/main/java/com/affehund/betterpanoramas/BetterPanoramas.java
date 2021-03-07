package com.affehund.betterpanoramas;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.affehund.betterpanoramas.client.BetterPanoramasClient;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(ModConstants.MOD_ID)
public class BetterPanoramas {
	public static final Logger LOGGER = LogManager.getLogger(ModConstants.MOD_NAME);
	public static BetterPanoramas INSTANCE;

	public BetterPanoramas() {
		INSTANCE = this;
		if (FMLEnvironment.dist == Dist.CLIENT) {
			new BetterPanoramasClient().setup();
		} else {
			LOGGER.error("As {} is a client only mod and cannot run on a dedicated server, it will be disabled!",
					ModConstants.MOD_NAME);
		}
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
				() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	}

	public static ResourceLocation getModResourceLocation(String path) {
		return new ResourceLocation(ModConstants.MOD_ID, path);
	}
}
