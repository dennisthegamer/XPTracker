package de.dennisthegamer.xptracker.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class XPTrackerConfigScreen {

	public static Screen create(Screen parent) {
		XPTrackerConfig config = XPTrackerConfig.getInstance();
		XPTrackerConfig defaults = new XPTrackerConfig();

		return YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("xptracker.config.title"))
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("xptracker.config.category.session"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.persist_sessions"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.persist_sessions.desc")))
								.binding(defaults.persistSessions, () -> config.persistSessions, v -> config.persistSessions = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_session_summary"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.show_session_summary.desc")))
								.binding(defaults.showSessionSummary, () -> config.showSessionSummary, v -> config.showSessionSummary = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("xptracker.config.category.hud"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.hud_enabled"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.hud_enabled.desc")))
								.binding(defaults.hudEnabled, () -> config.hudEnabled, v -> config.hudEnabled = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<XPTrackerConfig.HudPosition>createBuilder()
								.name(Component.translatable("xptracker.config.hud_position"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.hud_position.desc")))
								.binding(defaults.getHudPosition(), config::getHudPosition, v -> config.hudPosition = v.name())
								.controller(opt -> EnumControllerBuilder.create(opt).enumClass(XPTrackerConfig.HudPosition.class))
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.translatable("xptracker.config.hud_scale"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.hud_scale.desc")))
								.binding((int) (defaults.hudScale * 100), () -> (int) (config.hudScale * 100), v -> config.hudScale = v / 100f)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(50, 200).step(10))
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.translatable("xptracker.config.hud_color"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.hud_color.desc")))
								.binding(new Color(defaults.hudColor), () -> new Color(config.hudColor), v -> config.hudColor = v.getRGB())
								.controller(ColorControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_background"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.show_background.desc")))
								.binding(defaults.showBackground, () -> config.showBackground, v -> config.showBackground = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Component.translatable("xptracker.config.category.display"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_total_xp"))
								.binding(defaults.showTotalXP, () -> config.showTotalXP, v -> config.showTotalXP = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_level_gains"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.show_level_gains.desc")))
								.binding(defaults.showLevelGains, () -> config.showLevelGains, v -> config.showLevelGains = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_xp_per_minute"))
								.binding(defaults.showXPPerMinute, () -> config.showXPPerMinute, v -> config.showXPPerMinute = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_session_time"))
								.binding(defaults.showSessionTime, () -> config.showSessionTime, v -> config.showSessionTime = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_current_level"))
								.binding(defaults.showCurrentLevel, () -> config.showCurrentLevel, v -> config.showCurrentLevel = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("xptracker.config.show_xp_sources"))
								.description(OptionDescription.of(Component.translatable("xptracker.config.show_xp_sources.desc")))
								.binding(defaults.showXPSources, () -> config.showXPSources, v -> config.showXPSources = v)
								.controller(BooleanControllerBuilder::create)
								.build())
						.build())
				.save(config::save)
				.build()
				.generateScreen(parent);
	}
}
