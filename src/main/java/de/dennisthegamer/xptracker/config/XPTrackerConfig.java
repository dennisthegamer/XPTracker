package de.dennisthegamer.xptracker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XPTrackerConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger("xptracker");

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("xptracker.json");

	private static XPTrackerConfig instance;

	// Session Settings
	public boolean persistSessions = false;
	public boolean showSessionSummary = true;

	// HUD Settings
	public boolean hudEnabled = true;
	public String hudPosition = "TOP_LEFT";
	public float hudScale = 1.0f;
	public int hudColor = new Color(85, 255, 85).getRGB(); // green
	public int hudBackgroundColor = new Color(0, 0, 0, 128).getRGB();
	public boolean showBackground = true;
	public boolean showTotalXP = true;
	public boolean showLevelGains = true;
	public boolean showXPPerMinute = true;
	public boolean showSessionTime = true;
	public boolean showCurrentLevel = true;
	public boolean showXPSources = true;

	public HudPosition getHudPosition() {
		try {
			return HudPosition.valueOf(hudPosition);
		} catch (IllegalArgumentException e) {
			return HudPosition.TOP_LEFT;
		}
	}

	public enum HudPosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	public static XPTrackerConfig getInstance() {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	public static XPTrackerConfig load() {
		if (Files.exists(CONFIG_PATH)) {
			try {
				String json = Files.readString(CONFIG_PATH);
				instance = GSON.fromJson(json, XPTrackerConfig.class);
				if (instance == null) {
					instance = new XPTrackerConfig();
				}
				return instance;
			} catch (IOException e) {
				LOGGER.error("Failed to handle XPTracker config", e);
			}
		}
		instance = new XPTrackerConfig();
		instance.save();
		return instance;
	}

	public void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			Files.writeString(CONFIG_PATH, GSON.toJson(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
