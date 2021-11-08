package io.github.imurx.littletweaks;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "littletweaks")
public class LittleConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public String preferredDevice = "";

    @ConfigEntry.Gui.Excluded
    public boolean useDefaultDevice = true;

    @ConfigEntry.BoundedDiscrete(max = 240, min = 1)
    public int tickAudioEvery = 40;
}
