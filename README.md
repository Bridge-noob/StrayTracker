<div align="center">

# StrayTracker

*A Fabric client mod that tracks each guild's peak online count from `/guild online`.*

[![Build](https://github.com/Bridge-noob/StrayTracker/actions/workflows/build.yml/badge.svg)](https://github.com/Bridge-noob/StrayTracker/actions/workflows/build.yml)
[![Latest release](https://img.shields.io/github/v/release/Bridge-noob/StrayTracker?sort=semver)](https://github.com/Bridge-noob/StrayTracker/releases/latest)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8%20|%201.21.11%20|%2026.1.2-brightgreen)](#download)
[![License](https://img.shields.io/github/license/Bridge-noob/StrayTracker)](LICENSE)

</div>

Press **`-`** to start tracking, press it again to stop and get a **peak overview** — the highest online/total count every guild reached while you watched.

## Download

Grab the jar for **your** game version from the [latest release](https://github.com/Bridge-noob/StrayTracker/releases/latest):

| File | Minecraft | Fabric API | Java |
|---|---|---|---|
| `StrayTracker-1.0.1+1.21.8.jar` | 1.21.8 | 0.136.1+1.21.8 | 21+ |
| `StrayTracker-1.0.1+1.21.11.jar` | 1.21.11 | 0.141.6+1.21.11 | 21+ |
| `StrayTracker-1.0.1+26.1.2.jar` | 26.1.2 | 0.155.3+26.1.2 | 21+ |

## Install
1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version
2. Drop **Fabric API** and the matching **StrayTracker** jar into `mods/`
3. *(optional)* [ModMenu](https://modrinth.com/mod/modmenu) — enables the in-game config screen

## Features
- **Auto-polling** — while tracking, automatically sends `/guild online` every N seconds (default 30s; 10/15/30/60/120/300s in config)
- Parses the `Guilds Online Here` chat output (e.g. `Forsaken [Forsaken] • 10/219 online`) into entries
- **Hotkey `-`** (rebindable — in *Options → Controls* or directly in the mod's config screen): 1st press starts, 2nd press stops and prints the recorded peaks in chat
- **HUD overlay** — `● Started` status + **live** peak list while tracking; after stopping, the final sorted list stays on screen
- **ModMenu config** — HUD on/off, auto-track on join (starts once when you join a world — the hotkey can stop it anytime), poll interval, hotkey rebinding; HUD position (`hudX`/`hudY`) in `config/straytracker.json`

## Usage
1. Join the server and press **`-`** → tracking starts, HUD shows ● Tracking
2. Press **`-`** again → tracking stops, the peak overview is printed in chat and stays on the HUD
3. Everything else: *Mods → StrayTracker → Config* (or edit `config/straytracker.json`)

## Config
`config/straytracker.json`
```json
{
  "hudEnabled": true,
  "hudX": 10,
  "hudY": 10,
  "autoTrack": true,
  "maxEntries": 40,
  "intervalSeconds": 30,
  "hotKeyCode": 45
}
```

## Building
```bash
gradle build -PmcVersion=1.21.8    # or 1.21.11 / 26.1.2
```
Output: `build/libs/StrayTracker-1.0.1+<mcVersion>.jar` — one jar per Minecraft version, each versioned `1.0.1+<mcVersion>`. Java: 21 for 1.21.x, 25 for 26.x. Jars for release tags are built automatically by [GitHub Actions](.github/workflows/build.yml).

## Project layout
| Path | Contents |
|---|---|
| `src/main/java/com/straytracker/` | shared code — `StrayTrackerConfig`, `GuildChatParser`, `GuildEntry`, `PeakTracker`, `StrayTrackerMenuIntegration` |
| `src/yarn8/java/com/straytracker/` | client sources for 1.21.8 (Yarn names, string-category `KeyBinding`) |
| `src/yarn11/java/com/straytracker/` | client sources for 1.21.11 (Yarn names, `KeyBinding.Category`) |
| `src/mojang/java/com/straytracker/` | client sources for 26.1.2 (unobfuscated, Mojang names) |

Each client tree contains `StrayTrackerClient` (entrypoint, hotkey, auto-poll timer, chat listener), `StrayTrackerHudOverlay` (HUD) and `StrayTrackerConfigScreen` (ModMenu screen).

## License
[MIT](LICENSE)
