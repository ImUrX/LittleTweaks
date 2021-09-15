package io.github.imurx.audioswitcher;

import io.github.imurx.audioswitcher.events.SoundSystemCallback;
import io.github.imurx.audioswitcher.mixin.SoundEngineAccessor;
import io.github.imurx.audioswitcher.mixin.SoundManagerAccessor;
import io.github.imurx.audioswitcher.mixin.SoundSystemAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundSystem;
import org.lwjgl.openal.*;

import java.util.List;

public class AudioSwitcher implements ClientModInitializer {
	private int tickCounter = -1;
	static private Thread thread;
	static public List<String> devices;
	static public String defaultDevice = "";
	static public String currentDevice = "";
	static public String preferredDevice = "";
	static public boolean useDefault = true;

	@Override
	public void onInitializeClient() {
		updateDevices();
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(tickCounter < 0 || ++tickCounter < 40) return;
			SoundEngine engine = ((SoundSystemAccessor) ((SoundManagerAccessor) client.getSoundManager()).getSoundSystem()).getSoundEngine();
			SoundEngineAccessor accessor = (SoundEngineAccessor) engine;
			int connect = ALC11.alcGetInteger(accessor.getDevicePointer(), EXTDisconnect.ALC_CONNECTED);
			updateDevices();

			if(thread != null) {
				try {
					thread.join();
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
				thread = null;
				return;
			}

			if(connect == ALC11.ALC_FALSE) {
				thread = new Thread(() -> restartSoundSystem(defaultDevice));
			} else if(!currentDevice.equals(preferredDevice) && devices.contains(preferredDevice)) {
				thread = new Thread(() -> restartSoundSystem(preferredDevice));
			} else {
				return;
			}

			thread.setName("AudioSwitcher");
			thread.start();
		});
		SoundSystemCallback.STARTED_SYSTEM.register((_x) -> tickCounter = 0);
		SoundSystemCallback.STOPPING_SYSTEM.register((_x) -> tickCounter = -1);
	}

	static public void updateDevices() {
		devices = ALUtil.getStringList(0, ALC11.ALC_ALL_DEVICES_SPECIFIER);
		defaultDevice = ALC11.alcGetString(0, ALC11.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
	}
	static public void restartSoundSystem(String device) {
		SoundSystem soundSystem = ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem();
		soundSystem.stop();
		currentDevice = device;
		((SoundSystemAccessor) soundSystem).callStart();
	}
}
