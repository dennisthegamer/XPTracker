package com.example.xptracker;

import com.example.xptracker.config.XPTrackerConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class XPTrackerClient implements ClientModInitializer {

	public static final String MOD_ID = "xptracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final KeyMapping.Category KEYBIND_CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(MOD_ID, "xptracker")
	);

	private static KeyMapping toggleTrackingKey;
	private static KeyMapping resetTrackingKey;
	private boolean wasPaused;

	@Override
	public void onInitializeClient() {
		// Load config
		XPTrackerConfig.getInstance();

		// Register keybinding
		toggleTrackingKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.xptracker.toggleTracking",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_J,
				KEYBIND_CATEGORY
		));

		resetTrackingKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.xptracker.resetTracking",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				KEYBIND_CATEGORY
		));

		// Register XP tracking on each client tick
		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

		// When joining a world, try to restore saved session or show hint
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			XPSessionTracker tracker = XPSessionTracker.getInstance();
			tracker.reset();

			boolean restored = false;
			if (XPTrackerConfig.getInstance().persistSessions) {
				restored = tracker.loadFromDisk();
				if (restored) {
					LOGGER.info("World joined -- restored saved XP session");
				}
			}

			final boolean wasRestored = restored;
			client.execute(() -> {
				if (client.player != null) {
					if (wasRestored) {
						client.player.sendSystemMessage(
								Component.translatable("xptracker.chat.restored",
										toggleTrackingKey.getTranslatedKeyMessage())
										.withStyle(style -> style.withColor(0x55FF55))
						);
					} else {
						client.player.sendSystemMessage(
								Component.translatable("xptracker.chat.press_to_start",
										toggleTrackingKey.getTranslatedKeyMessage())
										.withStyle(style -> style.withColor(0xFFD700))
						);
					}
				}
			});
		});

		// When leaving a world, save session if enabled, then reset
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			XPSessionTracker tracker = XPSessionTracker.getInstance();
			if (tracker.isTracking() && XPTrackerConfig.getInstance().persistSessions) {
				tracker.saveToDisk();
				LOGGER.info("XP session saved to disk");
			}
			tracker.reset();
			wasPaused = false;
		});

		// Register HUD overlay
		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(MOD_ID, "xp_hud"),
				new XPTrackerHudElement()
		);

		LOGGER.info("XPTracker loaded!");
	}

	private void onClientTick(Minecraft mc) {
		XPSessionTracker tracker = XPSessionTracker.getInstance();

		// Handle keybind toggle
		if (toggleTrackingKey.consumeClick()) {
			boolean active = tracker.toggleTracking();
			if (mc.player != null) {
				if (active) {
					mc.player.sendSystemMessage(
							Component.translatable("xptracker.chat.started")
									.withStyle(style -> style.withColor(0x55FF55)));
				} else {
					mc.player.sendSystemMessage(
							Component.translatable("xptracker.chat.paused")
									.withStyle(style -> style.withColor(0xFFAA00)));
				}
			}
		}

		// Handle reset keybind
		while (resetTrackingKey.consumeClick()) {
			if (tracker.isTracking()) {
				sendSessionSummary(mc);
				tracker.reset();
				tracker.deleteSavedSession();
				HudEffects.triggerResetMessage();
				wasPaused = false;

				if (mc.player != null) {
					mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				}
			}
		}

		// Auto-pause when game is paused (Escape menu), auto-resume when unpaused
		if (tracker.isTracking()) {
			boolean gamePaused = mc.isPaused();
			if (gamePaused && !wasPaused) {
				tracker.pause();
			} else if (!gamePaused && wasPaused && !tracker.isPaused()) {
				// This case shouldn't happen since we pause above,
				// but guard against it
			} else if (!gamePaused && wasPaused) {
				tracker.resume();
			}
			wasPaused = gamePaused;
		}

		// Tick the tracker
		tracker.tick(mc.isPaused());
		HudEffects.tick();
	}

	private void sendSessionSummary(Minecraft mc) {
		if (mc.player == null) return;

		XPTrackerConfig config = XPTrackerConfig.getInstance();
		if (!config.showSessionSummary) return;

		XPSessionTracker tracker = XPSessionTracker.getInstance();

		// Blank line separator
		mc.player.sendSystemMessage(Component.empty());

		// Header
		mc.player.sendSystemMessage(
				Component.translatable("xptracker.chat.summary.header")
						.withStyle(style -> style.withColor(0xFFD700).withBold(true)));

		// Duration
		mc.player.sendSystemMessage(
				Component.translatable("xptracker.chat.summary.duration", tracker.getFormattedSessionTime())
						.withStyle(style -> style.withColor(0xFFFFFF)));

		// Level change
		int startLevel = tracker.getStartLevel();
		int currentLevel = tracker.getCurrentLevel();
		if (startLevel != currentLevel) {
			mc.player.sendSystemMessage(
					Component.translatable("xptracker.chat.summary.level", startLevel, currentLevel)
							.withStyle(style -> style.withColor(0xFFFFFF)));
		}

		// Total XP / Levels
		int levelsGained = currentLevel - startLevel;
		String levelStr = levelsGained == 1 ? "Level" : "Levels";
		if (config.showTotalXP && config.showLevelGains) {
			mc.player.sendSystemMessage(
					Component.translatable("xptracker.chat.summary.total_xp_and_levels",
							tracker.getTotalXPGained(), levelsGained, levelStr)
							.withStyle(style -> style.withColor(0xFFFFFF)));
		} else if (config.showTotalXP) {
			mc.player.sendSystemMessage(
					Component.translatable("xptracker.chat.summary.total_xp", tracker.getTotalXPGained())
							.withStyle(style -> style.withColor(0xFFFFFF)));
		} else if (config.showLevelGains) {
			mc.player.sendSystemMessage(
					Component.translatable("xptracker.chat.summary.total_levels", levelsGained, levelStr)
							.withStyle(style -> style.withColor(0xFFFFFF)));
		}

		// XP per minute
		mc.player.sendSystemMessage(
				Component.translatable("xptracker.chat.summary.xp_per_minute",
						String.format("%.1f", tracker.getXPPerMinute()))
						.withStyle(style -> style.withColor(0xFFFFFF)));

		// XP sources breakdown
		Map<XPSource, Integer> sources = tracker.getXPBySource();
		if (!sources.isEmpty() && tracker.getTotalXPGained() > 0) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (XPSource source : XPSource.values()) {
				int xp = sources.getOrDefault(source, 0);
				if (xp <= 0) continue;
				int pct = tracker.getSourcePercentage(source);
				int sourceLvl = (int) Math.round((double) levelsGained * xp / tracker.getTotalXPGained());
				String srcLevelStr = sourceLvl == 1 ? "Level" : "Levels";
				if (!first) sb.append(" | ");

				if (config.showTotalXP && config.showLevelGains) {
					sb.append(source.getEnglishName()).append(": ").append(xp)
							.append(" (").append(sourceLvl).append(" ").append(srcLevelStr).append(")");
				} else if (config.showLevelGains) {
					sb.append(source.getEnglishName()).append(": ").append(sourceLvl)
							.append(" ").append(srcLevelStr);
				} else {
					sb.append(source.getEnglishName()).append(": ").append(xp)
							.append(" (").append(pct).append("%)");
				}
				first = false;
			}
			if (!first) {
				mc.player.sendSystemMessage(
						Component.literal("  " + sb)
								.withStyle(style -> style.withColor(0xAAAAAA)));
			}
		}
	}
}
