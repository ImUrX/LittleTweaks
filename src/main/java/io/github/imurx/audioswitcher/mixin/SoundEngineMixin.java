package io.github.imurx.audioswitcher.mixin;

import io.github.imurx.audioswitcher.AudioSwitcher;
import net.minecraft.client.sound.AlUtil;
import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.ByteBuffer;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/openal/ALC10;alcCreateContext(JLjava/nio/IntBuffer;)J"
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void getCapabilities(CallbackInfo ci, ALCCapabilities capabilities) {
        if(!capabilities.ALC_ENUMERATE_ALL_EXT) {
            throw new IllegalStateException("Computer doesn't have the Enumerate All extension");
        }
        if(!capabilities.ALC_EXT_disconnect) {
            throw new IllegalStateException("Computer doesn't have the Disconnect extension");
        }
    }

    @Inject(
            method = "openDevice",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void openDevice(CallbackInfoReturnable<Long> cir) {
        long l = ALC10.alcOpenDevice(AudioSwitcher.currentDevice);
        if (l != 0L && !AlUtilAccessor.callCheckAlcErrors(l, "Open device")) {
            cir.setReturnValue(l);
            return;
        }

        throw new IllegalStateException("Failed to open OpenAL device");
    }
}
