package com.example.xptracker;

import com.example.xptracker.config.XPTrackerConfig;
import org.joml.Matrix3x2fStack;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPTrackerHudElement implements HudElement {

	private static final int MARGIN = 10;
	private static final int PADDING = 4;

	@Override
	public void extractRenderState(@Nullable GuiGraphicsExtractor graphics, @Nullable DeltaTracker deltaTracker) {
		XPTrackerConfig config = XPTrackerConfig.getInstance();
		if (!config.hudEnabled) return;

		XPSessionTracker tracker = XPSessionTracker.getInstance();
		boolean showResetMessage = HudEffects.isResetMessageActive();
		if (!tracker.isTracking() && !showResetMessage) return;

		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;

		List<HudLine> lines;
		if (showResetMessage) {
			lines = List.of(new HudLine("Session reset!", 0xFFFFFF00));
		} else {
			lines = buildLines(config, tracker);
		}
		if (lines.isEmpty()) return;

		float scale = config.hudScale;
		int lineHeight = font.lineHeight + 2;

		// Calculate content dimensions (unscaled)
		int maxWidth = 0;
		for (HudLine line : lines) {
			int w = font.width(line.text);
			if (w > maxWidth) maxWidth = w;
		}
		int contentWidth = maxWidth + PADDING * 2;
		int contentHeight = lines.size() * lineHeight + PADDING * 2;

		// Calculate scaled dimensions for positioning
		int scaledWidth = (int) (contentWidth * scale);
		int scaledHeight = (int) (contentHeight * scale);

		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		// Calculate position based on selected corner
		XPTrackerConfig.HudPosition pos = config.getHudPosition();
		int originX = switch (pos) {
			case TOP_LEFT, BOTTOM_LEFT -> MARGIN;
			case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - scaledWidth - MARGIN;
		};
		int originY = switch (pos) {
			case TOP_LEFT, TOP_RIGHT -> MARGIN;
			case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - scaledHeight - MARGIN;
		};

		Matrix3x2fStack matrix = graphics.pose();
		matrix.pushMatrix();
		matrix.translate(originX, originY);
		matrix.scale(scale, scale);

		int x = 0;
		int y = 0;

		if (config.showBackground) {
			graphics.fill(x, y, x + contentWidth, y + contentHeight, config.hudBackgroundColor);
		}

		for (int i = 0; i < lines.size(); i++) {
			HudLine line = lines.get(i);
			graphics.text(font, line.text, x + PADDING, y + PADDING + (i * lineHeight), line.color, true);
		}

		matrix.popMatrix();
	}

	private List<HudLine> buildLines(XPTrackerConfig config, XPSessionTracker tracker) {
		List<HudLine> lines = new ArrayList<>();
		int textColor = config.hudColor;

		if (tracker.isPaused()) {
			lines.add(new HudLine("XP Tracker [PAUSED]", 0xFFAA00));
		}
		if (config.showCurrentLevel) {
			int startLevel = tracker.getStartLevel();
			int currentLevel = tracker.getCurrentLevel();
			if (startLevel > 0 && currentLevel != startLevel) {
				lines.add(new HudLine("Level: " + startLevel + " → " + currentLevel, textColor));
			} else {
				lines.add(new HudLine("Level: " + currentLevel, textColor));
			}
		}
		if (config.showTotalXP || config.showLevelGains) {
			int levelsGained = tracker.getCurrentLevel() - tracker.getStartLevel();
			if (config.showTotalXP && config.showLevelGains) {
				String levelStr = levelsGained == 1 ? "Level" : "Levels";
				lines.add(new HudLine("XP Gained: +" + tracker.getTotalXPGained()
						+ " (" + levelsGained + " " + levelStr + ")", textColor));
			} else if (config.showTotalXP) {
				lines.add(new HudLine("XP Gained: +" + tracker.getTotalXPGained(), textColor));
			} else {
				String levelStr = levelsGained == 1 ? "Level" : "Levels";
				lines.add(new HudLine("Levels Gained: +" + levelsGained + " " + levelStr, textColor));
			}
		}
		if (config.showXPPerMinute) {
			lines.add(new HudLine(String.format("XP/min: %.1f", tracker.getXPPerMinute()), textColor));
		}
		if (config.showSessionTime) {
			lines.add(new HudLine("Session: " + tracker.getFormattedSessionTime(), textColor));
		}
		if (config.showXPSources && tracker.getTotalXPGained() > 0) {
			Map<XPSource, Integer> sources = tracker.getXPBySource();
			int totalLevelsGained = tracker.getCurrentLevel() - tracker.getStartLevel();
			for (XPSource source : XPSource.values()) {
				int xp = sources.getOrDefault(source, 0);
				if (xp <= 0) continue;
				int pct = tracker.getSourcePercentage(source);
				int sourceLevels = tracker.getTotalXPGained() > 0
						? (int) Math.round((double) totalLevelsGained * xp / tracker.getTotalXPGained())
						: 0;
				String levelStr = sourceLevels == 1 ? "Level" : "Levels";

				String text;
				if (config.showTotalXP && config.showLevelGains) {
					text = source.getEnglishName() + ": +" + xp + " (" + sourceLevels + " " + levelStr + ")";
				} else if (config.showLevelGains) {
					text = source.getEnglishName() + ": +" + sourceLevels + " " + levelStr;
				} else {
					text = source.getEnglishName() + ": +" + xp + " (" + pct + "%)";
				}
				lines.add(new HudLine(text, source.getColor()));
			}
		}

		return lines;
	}

	private record HudLine(String text, int color) {}
}
