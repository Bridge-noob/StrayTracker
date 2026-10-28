package com.straytracker;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** ModMenu config screen for StrayTracker. */
public final class StrayTrackerConfigScreen extends Screen {
    private final Screen parent;
    private ButtonWidget hotkeyButton;
    private boolean capturingKey = false;

    public StrayTrackerConfigScreen(Screen parent) {
        super(Text.literal("StrayTracker Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 4;

        addToggle(cx, y, "HUD overlay", () -> StrayTrackerConfig.hudEnabled, v -> StrayTrackerConfig.hudEnabled = v);
        addToggle(cx, y + 24, "Auto-track on join", () -> StrayTrackerConfig.autoTrack, v -> StrayTrackerConfig.autoTrack = v);

        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Auto-poll interval: " + StrayTrackerConfig.intervalSeconds + "s"),
                        b -> {
                            StrayTrackerConfig.intervalSeconds = StrayTrackerConfig.nextInterval();
                            b.setMessage(Text.literal("Auto-poll interval: " + StrayTrackerConfig.intervalSeconds + "s"));
                        })
                .dimensions(cx - 100, y + 48, 200, 20).build());

        hotkeyButton = addDrawableChild(ButtonWidget.builder(Text.literal(hotkeyLabel()), b -> {
                    capturingKey = true;
                    updateHotkeyLabel();
                })
                .dimensions(cx - 100, y + 72, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(cx - 100, y + 96, 200, 20).build());
    }

    private String hotkeyLabel() {
        if (capturingKey) return "> press any key... (Esc to cancel)";
        String key = StrayTrackerClient.hotkey.getBoundKeyLocalizedText().getString();
        return "Hotkey: " + (key.isEmpty() ? "not set" : key);
    }

    private void updateHotkeyLabel() {
        if (hotkeyButton != null) hotkeyButton.setMessage(Text.literal(hotkeyLabel()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (capturingKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                capturingKey = false; // cancel — keep the current binding
            } else {
                StrayTrackerConfig.hotkeyCode = keyCode;
                StrayTrackerClient.hotkey.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
                KeyBinding.updateKeysByCode();
                StrayTrackerConfig.save();
                capturingKey = false;
            }
            updateHotkeyLabel();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        // super.render() already renders the background (incl. the 1.21.6+ menu blur,
        // which may only be applied once per frame — calling renderBackground() here too
        // crashed with "Can only blur once per frame" on 1.21.11).
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}
