package com.example.xptracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * Detects the most likely source of XP gains using a heuristic
 * based on recent player actions and current game state.
 */
public class XPSourceDetector {

	private static final XPSourceDetector INSTANCE = new XPSourceDetector();

	// Time window in ms to attribute XP to a recent action
	private static final long ATTRIBUTION_WINDOW_MS = 6000;

	private long lastAttackTime;
	private long lastOreBreakTime;
	private long lastFishingTime;
	private long lastBreedingTime;

	// Blocks that drop XP when mined
	private static final Set<String> XP_ORE_BLOCKS = Set.of(
			"minecraft:coal_ore", "minecraft:deepslate_coal_ore",
			"minecraft:diamond_ore", "minecraft:deepslate_diamond_ore",
			"minecraft:emerald_ore", "minecraft:deepslate_emerald_ore",
			"minecraft:lapis_ore", "minecraft:deepslate_lapis_ore",
			"minecraft:redstone_ore", "minecraft:deepslate_redstone_ore",
			"minecraft:nether_quartz_ore",
			"minecraft:nether_gold_ore",
			"minecraft:sculk",
			"minecraft:sculk_catalyst",
			"minecraft:spawner"
	);

	public static XPSourceDetector getInstance() {
		return INSTANCE;
	}

	public void onAttackEntity() {
		lastAttackTime = System.currentTimeMillis();
	}

	public void onBlockBroken(BlockState state) {
		Block block = state.getBlock();
		Identifier id = BuiltInRegistries.BLOCK.getKey(block);
		if (XP_ORE_BLOCKS.contains(id.toString())) {
			lastOreBreakTime = System.currentTimeMillis();
		}
	}

	public void onFishingRodRetrieve() {
		lastFishingTime = System.currentTimeMillis();
	}

	public void onBreeding() {
		lastBreedingTime = System.currentTimeMillis();
	}

	/**
	 * Determines the most likely source of an XP gain based on recent events.
	 * Priority: Screen-based detection first (furnace/trading), then recent actions.
	 */
	public XPSource detectSource() {
		Minecraft mc = Minecraft.getInstance();
		long now = System.currentTimeMillis();

		// Screen-based detection (highest confidence)
		if (mc.screen instanceof AbstractFurnaceScreen) {
			return XPSource.SMELTING;
		}
		if (mc.screen instanceof MerchantScreen) {
			return XPSource.TRADING;
		}

		// Recent action-based detection (check most recent first)
		long mostRecent = 0;
		XPSource source = XPSource.OTHER;

		if (lastOreBreakTime > mostRecent && now - lastOreBreakTime < ATTRIBUTION_WINDOW_MS) {
			mostRecent = lastOreBreakTime;
			source = XPSource.MINING;
		}
		if (lastAttackTime > mostRecent && now - lastAttackTime < ATTRIBUTION_WINDOW_MS) {
			mostRecent = lastAttackTime;
			source = XPSource.COMBAT;
		}
		if (lastFishingTime > mostRecent && now - lastFishingTime < ATTRIBUTION_WINDOW_MS) {
			mostRecent = lastFishingTime;
			source = XPSource.FISHING;
		}
		if (lastBreedingTime > mostRecent && now - lastBreedingTime < ATTRIBUTION_WINDOW_MS) {
			mostRecent = lastBreedingTime;
			source = XPSource.BREEDING;
		}

		return source;
	}

	public void reset() {
		lastAttackTime = 0;
		lastOreBreakTime = 0;
		lastFishingTime = 0;
		lastBreedingTime = 0;
	}
}
