package io.github.imurx.littletweaks.mixin;

import io.github.imurx.littletweaks.events.SoundSystemCallback;
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
    private void onStarted(CallbackInfo ci) {
        SoundSystemCallback.STARTED_SYSTEM.invoker().onStateChange((SoundSystem) (Object) this);
    }

    @Inject(
            method = "stop()V",
            at = @At("HEAD")
    )
    private void onStopped(CallbackInfo ci) {
        SoundSystemCallback.STOPPING_SYSTEM.invoker().onStateChange((SoundSystem) (Object) this);
    }
}
