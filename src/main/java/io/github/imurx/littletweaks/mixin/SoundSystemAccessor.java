package io.github.imurx.littletweaks.mixin;

import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundSystem.class)
public interface SoundSystemAccessor {
    @Accessor
    SoundEngine getSoundEngine();
    @Invoker
    void callStart();
}
