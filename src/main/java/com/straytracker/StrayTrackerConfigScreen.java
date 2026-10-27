package com.straytracker;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** ModMenu config screen for StrayTracker. */
public final class StrayTrackerConfigScreen extends Screen {
    private final Screen parent;

    public StrayTrackerConfigScreen(Screen parent) {
        super(Text.literal("StrayTracker Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 4;

        addToggle(cx, y, "HUD overlay", () -> StrayTrackerConfig.hudEnabled, v -> StrayTrackerConfig.hudEnabled = v);
        addToggle(cx, y + 24, "Auto-track on load", () -> StrayTrackerConfig.autoTrack, v -> StrayTrackerConfig.autoTrack = v);

        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Auto-poll interval: " + StrayTrackerConfig.intervalSeconds + "s"),
                        b -> {
                            StrayTrackerConfig.intervalSeconds = StrayTrackerConfig.nextInterval();
                            b.setMessage(Text.literal("Auto-poll interval: " + StrayTrackerConfig.intervalSeconds + "s"));
                        })
                .dimensions(cx - 100, y + 48, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(cx - 100, y + 76, 200, 20).build());
    }

    private interface Getter { boolean get(); }
    private interface Setter { void set(boolean v); }

    private void addToggle(int cx, int y, String label, Getter getter, Setter setter) {
        boolean on = getter.get();
        ButtonWidget btn = ButtonWidget.builder(
                        Text.literal(label + ": " + (on ? "§aON" : "§cOFF")),
                        b -> {
                            boolean now = !getter.get();
                            setter.set(now);
                            b.setMessage(Text.literal(label + ": " + (now ? "§aON" : "§cOFF")));
                        })
                .dimensions(cx - 100, y, 200, 20).build();
        addDrawableChild(btn);
    }

    @Override
    public void close() {
        StrayTrackerConfig.save();
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}
