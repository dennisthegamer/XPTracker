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



It's finally time — new mods just dropped! 🎉

After a lot of work behind the scenes, things are finally going live. Here's what's out and what's coming!
                                                                                                                                                                       
---                                                                                                                                                                  
✅ Already Live

MiningStats — tracks all 10 vanilla ore types (including deepslate variants), calculates Fortune bonus drops per ore, and shows everything in a live HUD overlay.
Comes with session timer, milestone announcements (e.g. "100 Diamonds mined!"), and a golden flash effect when hitting Diamonds or Ancient Debris. Toggle compact
mode with H, reset with K.
📥 https://www.curseforge.com/members/Dennis_thegamer/projects · https://modrinth.com/user/dennis.thegamer96

  ---
🕐 Just submitted

These are already live on CurseForge — Modrinth approval is still pending.

KillStats — tracks kills per entity type with a live HUD, including estimated emerald drop values with Looting support. Features a session timer with
start/pause/resume (auto-pauses when menus open), milestone notifications, boss kill sound alerts, and an optional session summary on exit. Keys: K (timer), L
(reset), J (compact mode).

XPTracker — tracks XP gain per session with automatic source detection: Combat, Mining, Smelting, Trading, Fishing, and Breeding. Shows XP per minute, level gains,
and current level in a configurable HUD. Auto-pauses on the pause menu, optional session persistence. Keys: J (toggle tracking), K (reset).

TradeTracker — automatically records every trade with Villagers and Wandering Traders and calculates emerald profit/loss in real time using 100+ item value mappings.
Includes a dedicated trade history screen with filtering and sorting (G to open), HUD overlay in full or compact mode, and flash effects for profitable vs. losing
trades. Keys: T (compact HUD), G (trade history), J (start/pause session).

BreedTimer — displays floating timers above breedable animals showing breed cooldown and baby growth time. Includes love mode and ready-to-breed indicators,
distance-based fading, FOV culling, compact mode, and a sound alert when animals are ready to breed. Keys: N (toggle on/off), B (compact mode).

  ---
🔨 Still cooking

ShulkerSort — press J to automatically sort all shulker boxes in your inventory by item category (Blocks, Tools, Food, Ores, Brewing, Misc). Merges partial stacks,
auto-labels boxes by category (e.g. "Ores #1"), and skips boxes marked with [LOCKED]. Tooltip preview on hover included.

CircleCraft — a shape builder for in-world construction. Supports Circles, Ovals, Cylinders, Spheres, Cones, Pyramids, and Toruses. Features a ghost preview overlay,
multiple placement modes, symmetry, pattern fill (solid/stripe/random), undo/redo, and export to Litematica or NBT schematic. Open the shape builder UI with C.

  ---
🌐 Full portfolio: https://dennisthegamer.github.io
📦 CurseForge: https://www.curseforge.com/members/Dennis_thegamer/projects
🟢 Modrinth: https://modrinth.com/user/dennis.thegamer96

Feedback and bug reports are always welcome — more on the way! 🚀