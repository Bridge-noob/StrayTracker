# StrayTracker (Fabric mod)

Tracks guild online counts from the server's `/guild online` output and records the **peak online count for each guild** between hotkey presses.

- **Minecraft:** separate jars for 1.21.8, 1.21.11 and 26.1.2 · **Loader:** Fabric · **License:** MIT

## Features
- **Auto-polling** — while tracking, automatically sends `/guild online` every N seconds (default 30s, configurable 10/15/30/60/120/300s)
- Parses `Guilds Online Here` chat output (e.g. `Forsaken [Forsaken] • 10/219 online`) into entries
- **Hotkey `-`** (fully rebindable): 1st press starts tracking, 2nd press stops and prints a chat overview of the recorded peaks
- **HUD overlay** — shows ● Tracking status while active; after stopping, shows the sorted peak-online list
- **ModMenu config** — HUD on/off, auto-track on load, poll interval; HUD position (`hudX`/`hudY`) in `config/straytracker.json`
- Auto-track on load option (starts tracking when the game launches)

## Build
```bash
gradle build -PmcVersion=1.21.8    # or 1.21.11, 26.1.2
```
Output: `build/libs/StrayTracker-1.0.1+<mcVersion>.jar` — each Minecraft version gets its own jar with its own version (`1.0.1+1.21.8`, `1.0.1+1.21.11`, `1.0.1+26.1.2`).

Releases with prebuilt jars are produced automatically by GitHub Actions (`.github/workflows/build.yml`) on `v*` tags.

## Install
Drop the jar in `mods/` alongside Fabric Loader, Fabric API and ModMenu.

## Usage
1. Join the server, press **`-`** → tracking starts, HUD shows ● Tracking
2. Press **`-`** again → tracking stops, peak overview is printed in chat and stays on the HUD
3. To disable auto-polling without stopping tracking, set `intervalSeconds` high; adjust everything in Mods → StrayTracker

## Config
`config/straytracker.json`
```json
{
  "hudEnabled": true,
  "hudX": 10,
  "hudY": 10,
  "autoTrack": true,
  "maxEntries": 40,
  "intervalSeconds": 30
}
```

## File layout
- `src/main/java/com/straytracker/` — shared code (config, parser, peak tracker, ModMenu entrypoint)
- `src/yarn8/java/com/straytracker/` — Yarn sources for 1.21.8 (string-category `KeyBinding`)
- `src/yarn11/java/com/straytracker/` — Yarn sources for 1.21.11 (`KeyBinding.Category`)
- `src/mojang/java/com/straytracker/` — Mojang-names sources for unobfuscated 26.1.2
- `StrayTrackerClient.java` — init, hotkey, auto-poll timer, chat listener
- `StrayTrackerConfig.java` — JSON config
- `StrayTrackerConfigScreen.java` — ModMenu screen
- `StrayTrackerMenuIntegration.java` — ModMenu entrypoint
- `StrayTrackerHudOverlay.java` — HUD rendering
- `GuildChatParser.java` — chat → `GuildEntry` parsing
- `GuildEntry.java` — parsed line record
- `PeakTracker.java` — peak recording + sorted overview
