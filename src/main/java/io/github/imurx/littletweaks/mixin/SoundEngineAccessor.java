package io.github.imurx.littletweaks.mixin;

import net.minecraft.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundEngine.class)
public interface SoundEngineAccessor {
    @Accessor
    long getDevicePointer();
    @Accessor
    long getContextPointer();
}
