# Changelog

All notable changes to XPTracker will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-04-14

### Added
- Initial release
- Real-time XP tracking with session management (toggle, pause, reset)
- Automatic XP source detection: Combat, Mining, Smelting, Trading, Fishing, Breeding
- XP per minute efficiency calculation
- Level gain tracking with session time display
- Per-source percentage breakdown
- Configurable HUD overlay with four corner positions
- Adjustable HUD scale, text color, background color, and background opacity
- Toggle individual stat display (total XP, levels, XP/min, session time, current level, sources)
- Auto-pause when opening the pause menu, auto-resume on close
- Optional session persistence across world changes
- Session data saved to `xptracker_session.json`
- Detailed session summary printed to chat on reset
- Keybinds: `J` (toggle tracking), `K` (reset session)
- Full YACL config screen with three tabs: Session, HUD, Display
- ModMenu integration for in-game config access
- Mixins for accurate source detection (player attacks, block breaking, fishing rod use, animal interaction)
- Ore detection: Coal, Diamond, Emerald, Lapis, Redstone, Nether Quartz, Nether Gold, Sculk, Spawners
- Localization support (English & German)
- Client-side only - no server installation required
- Compatibility with Minecraft 26.1+
- Fabric Loader 0.18.4+ support
- Fabric API integration
