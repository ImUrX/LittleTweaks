package io.github.imurx.audioswitcher.mixin;

import io.github.imurx.audioswitcher.AudioSwitcher;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(
            method = "start",
            at = @At("TAIL")
    )
    void onStarted(CallbackInfo ci) {
        AudioSwitcher.switcher.onInitialized();
    }

    @Inject(
            method = "stop()V",
            at = @At("HEAD")
    )
    void onStopped(CallbackInfo ci) {
        AudioSwitcher.switcher.onStopSystem();
    }
}
