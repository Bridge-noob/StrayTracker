# Guild Mod — Fabric 1.21.8 → 26.3

## Features
- `/guild online` command (client-side; sends to server, parses chat output)
- Auto-parses `Guilds Online Here` chat lines and extracts guild counts
- Hotkey tracking (default B): press once to start tracking peaks, press again to stop and show overview
- HUD overlay shows recording status and peak member counts (configurable via ModMenu)
- Configurable position, HUD on/off, auto-track on load, max entries shown
- Works across Fabric 1.21.8 to 26.3 with Yarn mappings

## Build
```bash
gradlew build
```
Output: `build/libs/guildmod-1.0.0.jar`

## Install
Place jar in `mods/` with Fabric Loader + Fabric API + ModMenu.

## Usage
1. Run `/guild online` in game (or let server show guild list)
2. Press **B** (configurable in ModMenu) to start tracking
3. Press **B** again to stop and get peak overview in chat + HUD
4. Open ModMenu from Pause → Mods → Guild Mod for settings

## File Layout
- `GuildModClient.java` — main init, tick, chat listener, hotkey
- `GuildOnlineCommand.java` — `/guild online`
- `GuildChatParser.java` — parses chat lines to entries
- `PeakTracker.java` — records peak online/total per guild
- `GuildHudOverlay.java` — draws HUD
- `GuildConfigScreen.java` — ModMenu config screen
- `GuildConfig.java` — JSON config
