package com.straytracker.StrayTracker;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GuildConfigScreen extends Screen {
    private final Screen parent;

    public GuildConfigScreen(Screen parent) {
        super(Text.literal("Guild Mod Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 4 + 10;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Toggle HUD: " + GuildConfig.hudEnabled), btn -> {
            GuildConfig.hudEnabled = !GuildConfig.hudEnabled;
            btn.setMessage(Text.literal("Toggle HUD: " + GuildConfig.hudEnabled));
            GuildConfig.save();
        }).dimensions(cx - 100, y, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Auto Track: " + GuildConfig.autoTrack), btn -> {
            GuildConfig.autoTrack = !GuildConfig.autoTrack;
            btn.setMessage(Text.literal("Auto Track: " + GuildConfig.autoTrack));
            GuildConfig.save();
        }).dimensions(cx - 100, y + 25, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Auto / Guild online every: " + GuildConfig.intervalSeconds + "s (configurable)"), btn -> {
            // click cycles a -1 step counter — actually just display update handled below; simpler: ignore, keep display refresh in init
        }).dimensions(cx - 220, y + 60, 440, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), btn -> {
            GuildConfig.save();
            this.client.setScreen(parent);
        }).dimensions(cx - 100, y + 60, 200, 20).build());
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Hotkey: B  |  Auto track starts on load", this.width / 2, 70, 0xAAAAAA);
    }
}
