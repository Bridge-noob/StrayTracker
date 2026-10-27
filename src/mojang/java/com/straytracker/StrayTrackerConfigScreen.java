package com.straytracker;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** ModMenu config screen for StrayTracker. */
public final class StrayTrackerConfigScreen extends Screen {
    private final Screen parent;

    public StrayTrackerConfigScreen(Screen parent) {
        super(Component.literal("StrayTracker Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 4;

        addToggle(cx, y, "HUD overlay", () -> StrayTrackerConfig.hudEnabled, v -> StrayTrackerConfig.hudEnabled = v);
        addToggle(cx, y + 24, "Auto-track on load", () -> StrayTrackerConfig.autoTrack, v -> StrayTrackerConfig.autoTrack = v);

        addRenderableWidget(Button.builder(
                        Component.literal("Auto-poll interval: " + StrayTrackerConfig.intervalSeconds + "s"),
                        b -> {
                            StrayTrackerConfig.intervalSeconds = StrayTrackerConfig.nextInterval();
                            b.setMessage(Component.literal("Auto-poll interval: " + StrayTrackerConfig.intervalSeconds + "s"));
                        })
                .bounds(cx - 100, y + 48, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(cx - 100, y + 76, 200, 20).build());
    }

    private interface Getter { boolean get(); }
    private interface Setter { void set(boolean v); }

    private void addToggle(int cx, int y, String label, Getter getter, Setter setter) {
        boolean on = getter.get();
        Button btn = Button.builder(
                        Component.literal(label + ": " + (on ? "§aON" : "§cOFF")),
                        b -> {
                            boolean now = !getter.get();
                            setter.set(now);
                            b.setMessage(Component.literal(label + ": " + (now ? "§aON" : "§cOFF")));
                        })
                .bounds(cx - 100, y, 200, 20).build();
        addRenderableWidget(btn);
    }

    @Override
    public void onClose() {
        StrayTrackerConfig.save();
        this.minecraft.setScreen(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int titleX = (this.width - this.minecraft.font.width(this.title.getString())) / 2;
        graphics.text(this.minecraft.font, this.title.getString(), titleX, 20, 0xFFFFFFFF, true);
    }
}
