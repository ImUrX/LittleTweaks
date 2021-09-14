package io.github.imurx.audioswitcher;

import net.fabricmc.api.ModInitializer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.EnumerateAllExt;

public class AudioSwitcher implements ModInitializer {
	@Override
	public void onInitialize() {
		if(!ALC11.alcIsExtensionPresent(0, "ALC_ENUMERATE_ALL_EXT")) {
			throw new IllegalStateException("Computer doesn't has the Enumerate All extension");
		}
		System.out.println(ALC11.alcGetString(0, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER).replaceAll("\0", ","));
	}
}
