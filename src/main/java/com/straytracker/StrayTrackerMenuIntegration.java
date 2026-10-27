package com.straytracker;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** ModMenu integration — opens StrayTrackerConfigScreen. */
public final class StrayTrackerMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return StrayTrackerConfigScreen::new;
    }
}
