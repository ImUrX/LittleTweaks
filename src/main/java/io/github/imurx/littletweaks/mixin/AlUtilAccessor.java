package io.github.imurx.littletweaks.mixin;

import net.minecraft.client.sound.AlUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AlUtil.class)
public interface AlUtilAccessor {
    @Invoker("checkAlcErrors")
    public static boolean callCheckAlcErrors(long deviceHandle, String sectionName) {
        throw new AssertionError();
    }
}
