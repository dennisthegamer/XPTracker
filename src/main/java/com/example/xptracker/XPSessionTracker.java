package com.example.xptracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class XPSessionTracker {

	private static final XPSessionTracker INSTANCE = new XPSessionTracker();

	private boolean tracking;
	private boolean paused;
	private long accumulatedTimeMs;
	private long lastResumeTime;
	private int lastKnownTotalXP;
	private int totalXPGained;
	private boolean inWorld;
	private int startLevel;

	// Per-source XP tracking
	private final Map<XPSource, Integer> xpBySource = new HashMap<>();

	public static XPSessionTracker getInstance() {
		return INSTANCE;
	}

	public void tick(boolean gamePaused) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;

		if (player == null) {
			if (inWorld) {
				reset();
			}
			return;
		}

		inWorld = true;

		if (!tracking || paused || gamePaused) {
			// Keep lastKnownTotalXP in sync so we don't count XP gained while paused
			if (tracking) {
				lastKnownTotalXP = player.totalExperience;
			}
			return;
		}

		int currentTotalXP = player.totalExperience;

		// Detect XP changes - only count gains, not losses (e.g. from enchanting/dying)
		int delta = currentTotalXP - lastKnownTotalXP;
		if (delta > 0) {
			totalXPGained += delta;
			// Attribute XP to detected source
			XPSource source = XPSourceDetector.getInstance().detectSource();
			xpBySource.merge(source, delta, Integer::sum);
		}
		lastKnownTotalXP = currentTotalXP;
	}

	/**
	 * Toggle tracking on/off. Returns true if tracking is now active (unpaused).
	 */
	public boolean toggleTracking() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) return false;

		if (!tracking) {
			// First start
			tracking = true;
			paused = false;
			accumulatedTimeMs = 0;
			lastResumeTime = System.currentTimeMillis();
			lastKnownTotalXP = player.totalExperience;
			totalXPGained = 0;
			startLevel = player.experienceLevel;
			xpBySource.clear();
			XPSourceDetector.getInstance().reset();
			return true;
		}

		// Toggle pause
		if (paused) {
			resume();
			return true;
		} else {
			pause();
			return false;
		}
	}

	public void pause() {
		if (tracking && !paused) {
			accumulatedTimeMs += System.currentTimeMillis() - lastResumeTime;
			paused = true;
		}
	}

	public void resume() {
		if (tracking && paused) {
			lastResumeTime = System.currentTimeMillis();
			paused = false;
			// Sync XP so we don't count changes during pause
			Minecraft mc = Minecraft.getInstance();
			if (mc.player != null) {
				lastKnownTotalXP = mc.player.totalExperience;
			}
		}
	}

	public void reset() {
		tracking = false;
		paused = false;
		accumulatedTimeMs = 0;
		lastResumeTime = 0;
		lastKnownTotalXP = 0;
		totalXPGained = 0;
		startLevel = 0;
		inWorld = false;
		xpBySource.clear();
		XPSourceDetector.getInstance().reset();
	}

	public boolean isTracking() {
		return tracking;
	}

	public boolean isPaused() {
		return paused;
	}

	public boolean isInWorld() {
		return inWorld;
	}

	public int getTotalXPGained() {
		return totalXPGained;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public Map<XPSource, Integer> getXPBySource() {
		return xpBySource;
	}

	/**
	 * Returns the percentage of total XP attributed to the given source.
	 */
	public int getSourcePercentage(XPSource source) {
		if (totalXPGained <= 0) return 0;
		return (int) Math.round(xpBySource.getOrDefault(source, 0) * 100.0 / totalXPGained);
	}

	public long getSessionDurationMs() {
		if (!tracking) return 0;
		if (paused) return accumulatedTimeMs;
		return accumulatedTimeMs + (System.currentTimeMillis() - lastResumeTime);
	}

	public double getXPPerMinute() {
		long durationMs = getSessionDurationMs();
		if (durationMs < 1000) return 0;
		return (totalXPGained / (durationMs / 60000.0));
	}

	public int getCurrentLevel() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return 0;
		return mc.player.experienceLevel;
	}

	public int getCurrentTotalXP() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return 0;
		return mc.player.totalExperience;
	}

	public String getFormattedSessionTime() {
		long totalSeconds = getSessionDurationMs() / 1000;
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;

		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		}
		return String.format("%d:%02d", minutes, seconds);
	}

	// === Persistence ===

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File SESSION_FILE = new File(
			FabricLoader.getInstance().getConfigDir().toFile(),
			"xptracker_session.json"
	);

	public void saveToDisk() {
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("totalXPGained", totalXPGained);
			data.put("durationMs", getSessionDurationMs());
			data.put("startLevel", startLevel);

			Map<String, Integer> sourceData = new HashMap<>();
			for (Map.Entry<XPSource, Integer> e : xpBySource.entrySet()) {
				if (e.getValue() > 0) sourceData.put(e.getKey().name(), e.getValue());
			}
			data.put("xpBySource", sourceData);

			SESSION_FILE.getParentFile().mkdirs();
			try (FileWriter writer = new FileWriter(SESSION_FILE)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			System.err.println("Failed to save XPTracker session: " + e.getMessage());
		}
	}

	public boolean loadFromDisk() {
		if (!SESSION_FILE.exists()) return false;
		try (FileReader reader = new FileReader(SESSION_FILE)) {
			Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
			Map<String, Object> data = GSON.fromJson(reader, mapType);
			if (data == null) return false;

			reset();

			if (data.containsKey("totalXPGained")) {
				totalXPGained = ((Double) data.get("totalXPGained")).intValue();
			}
			if (data.containsKey("durationMs")) {
				long savedDuration = ((Double) data.get("durationMs")).longValue();
				accumulatedTimeMs = savedDuration;
			}
			if (data.containsKey("startLevel")) {
				startLevel = ((Double) data.get("startLevel")).intValue();
			}

			@SuppressWarnings("unchecked")
			Map<String, Double> sourceData = (Map<String, Double>) data.get("xpBySource");
			if (sourceData != null) {
				for (Map.Entry<String, Double> e : sourceData.entrySet()) {
					try {
						XPSource source = XPSource.valueOf(e.getKey());
						xpBySource.put(source, e.getValue().intValue());
					} catch (IllegalArgumentException ignored) {}
				}
			}

			// Restored session starts paused
			tracking = true;
			paused = true;
			inWorld = true;

			return totalXPGained > 0 || accumulatedTimeMs > 0;
		} catch (Exception e) {
			System.err.println("Failed to load XPTracker session: " + e.getMessage());
			return false;
		}
	}

	public void deleteSavedSession() {
		if (SESSION_FILE.exists()) {
			SESSION_FILE.delete();
		}
	}
}
