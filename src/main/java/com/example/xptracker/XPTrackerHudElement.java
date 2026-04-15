package com.example.xptracker;

import com.example.xptracker.config.XPTrackerConfig;
import org.joml.Matrix3x2fStack;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPTrackerHudElement implements HudElement {

	private static final int MARGIN = 10;
	private static final int PADDING = 4;
	private static final int ICON_SIZE = 16;
	private static final int ICON_GAP = 1;

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
		int textLineHeight = font.lineHeight + 2;
		int iconLineHeight = ICON_SIZE + 2;

		// Calculate content dimensions (unscaled)
		int maxWidth = 0;
		for (HudLine line : lines) {
			int w = font.width(line.text);
			if (line.icon != null) {
				w += ICON_SIZE + ICON_GAP;
			}
			if (w > maxWidth) maxWidth = w;
		}
		int contentWidth = maxWidth + PADDING * 2;
		int contentHeight = PADDING * 2;
		for (HudLine line : lines) {
			contentHeight += line.icon != null ? iconLineHeight : textLineHeight;
		}

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

		int currentY = y + PADDING;
		for (int i = 0; i < lines.size(); i++) {
			HudLine line = lines.get(i);
			if (line.icon != null) {
				int iconY = currentY;
				graphics.item(line.icon, x + PADDING, iconY);
				int textY = iconY + (ICON_SIZE - font.lineHeight) / 2;
				graphics.text(font, line.text, x + PADDING + ICON_SIZE + ICON_GAP, textY, line.color, true);
				currentY += iconLineHeight;
			} else {
				graphics.text(font, line.text, x + PADDING, currentY, line.color, true);
				currentY += textLineHeight;
			}
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
					text = "+" + xp + " (" + sourceLevels + " " + levelStr + ")";
				} else if (config.showLevelGains) {
					text = "+" + sourceLevels + " " + levelStr;
				} else {
					text = "+" + xp + " (" + pct + "%)";
				}
				lines.add(new HudLine(text, source.getColor(), source.getIcon()));
			}
		}

		return lines;
	}

	private record HudLine(String text, int color, @Nullable ItemStack icon) {
		HudLine(String text, int color) {
			this(text, color, null);
		}
	}
}
