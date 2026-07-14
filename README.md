# XPTracker

A Minecraft Fabric mod that tracks XP collection per session with detailed source breakdown, efficiency stats, and a configurable HUD overlay.

## Features

- Real-time XP tracking with session management
- Automatic XP source detection (Combat, Mining, Smelting, Trading, Fishing, Breeding)
- XP per minute efficiency calculation
- Level gain tracking
- Configurable HUD overlay with multiple positions and colors
- Auto-pause when opening the pause menu
- Session persistence across world changes (optional)
- Detailed session summary in chat on reset
- Dedicated keybinds: `J` (toggle tracking), `K` (reset session)
- Full in-game config screen (YACL + ModMenu)
- Client-side only - no server required

## Compatibility

- **Minecraft**: 26.1+
- **Fabric Loader**: 0.18.4+
- **Fabric API**: Required
- **Java**: 25+
- **ModMenu**: Optional (for config screen)
- **YACL**: Optional (for config screen)

## Download

Download the latest release from [Modrinth](https://modrinth.com/mod/xptracker) or [GitHub Releases](https://github.com/DennisTheGamer/XPTracker/releases).

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download XPTracker (this mod)
4. Place both JAR files in your `mods` folder
5. Launch Minecraft

## Configuration

Open the config screen via ModMenu. Settings are organized in three tabs:

- **Session** - Persist sessions across worlds, show session summary on reset
- **HUD** - Enable/disable overlay, position, scale, colors, background opacity
- **Display** - Toggle individual stats (total XP, levels, XP/min, session time, current level, XP sources)

Keybinds are listed under **Controls > XP Tracker**:
- `J` - Toggle tracking on/off
- `K` - Reset session

Config file is saved at `config/xptracker.json`.

## Building from Source

```bash
git clone https://github.com/DennisTheGamer/XPTracker.git
cd XPTracker
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- **Author**: Dennis_thegamer
- **Built with**: Fabric, Fabric API, YACL

## Support

- Report bugs on [GitHub Issues](https://github.com/DennisTheGamer/XPTracker/issues)
- Visit the [Modrinth page](https://modrinth.com/mod/xptracker) for more information
