package com.affehund.betterpanoramas.client;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

/**
 * @author Affehund
 *
 */
public class BetterPanoramasConfig {
	public static class BetterPanoramasClientConfig {
		public final BooleanValue OVERRIDE_VANILLA_PANORAMA;
		public final DoubleValue SWAY_SPEED;
		public final DoubleValue SWAY_ANGLE;
		public final DoubleValue INITIAL_SWAY_PROGRESS;
		public final DoubleValue ROTATION_SPEED;
		public final DoubleValue VERTICAL_ANGLE;
		public final DoubleValue STARTING_HORIZONTAL_ANGLE;

		public BetterPanoramasClientConfig(ForgeConfigSpec.Builder builder) {
			builder.comment("Better Panoramas Client Config").push("general");
			OVERRIDE_VANILLA_PANORAMA = builder
					.comment("This sets whether the vanilla panorama will be overriden by the new panoramas.")
					.define("override_vanilla_panorama", true);
			SWAY_SPEED = builder.comment("This sets the sway speed.").defineInRange("sway_speed", 1.0, -100.0, 100.0);
			SWAY_ANGLE = builder.comment("This sets the sway angle.").defineInRange("sway_angle", 5.0, -360, 360.0);
			INITIAL_SWAY_PROGRESS = builder.comment("This sets the initial sway progress.")
					.defineInRange("initial_sway_progress", 0.0, -100.0, 100.0);
			ROTATION_SPEED = builder.comment("This sets the rotation speed.").defineInRange("rotation_speed", 1.0,
					-100.0, 100.0);
			VERTICAL_ANGLE = builder.comment("This sets the vertical angle.").defineInRange("vertical_angle", -25.0,
					-360.0, 360.0);
			STARTING_HORIZONTAL_ANGLE = builder.comment("This sets the starting horizontal angle.")
					.defineInRange("starting_horizontal_angle", 0, -360.0, 360.0);
			builder.pop();
		}
	}

	public static final ForgeConfigSpec CLIENT_CONFIG_SPEC;
	public static final BetterPanoramasClientConfig CLIENT_CONFIG;
	static {
		final Pair<BetterPanoramasClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
				.configure(BetterPanoramasClientConfig::new);
		CLIENT_CONFIG_SPEC = specPair.getRight();
		CLIENT_CONFIG = specPair.getLeft();
	}
}
