# Guild Mod — Multi-Version Fabric

Compatible: 1.21.11 → 26.1.2 (loader 0.16.10+)

## Build one version
```bash
gradlew build -PmcVersion=26.1.2
```

## Build all listed
```bash
for v in 1.21.11 1.21.8 26.1.2; do
  ./gradlew build -PmcVersion=$v
done
```

Output: `build/libs/StrayTracker-<mcVersion>.jar`

## Features
- `/guild online`
- Hotkey `-` (minus): press to start/stop peak tracking
- HUD overlay (config via ModMenu)
- Auto-track on load (toggle in config)
- Chat parsing for `Guilds Online Here` output

## Upload to GitHub repo
```bash
git init
git add .
git commit -m "Multi-version guild mod"
git remote add origin https://github.com/YOURUSER/straytracker-StrayTracker.git
git push -u origin main
```
