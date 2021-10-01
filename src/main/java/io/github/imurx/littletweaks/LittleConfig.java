package io.github.imurx.littletweaks;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "littletweaks")
public class LittleConfig implements ConfigData {
    public String preferredDevice = "";
    public boolean useDefaultDevice = true;
    public int tickAudioEvery = 40;
}
