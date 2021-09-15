package io.github.imurx.audioswitcher;

import io.github.imurx.audioswitcher.mixin.SoundEngineAccessor;
import io.github.imurx.audioswitcher.mixin.SoundManagerAccessor;
import io.github.imurx.audioswitcher.mixin.SoundSystemAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundSystem;
import org.lwjgl.openal.*;

import java.nio.LongBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AudioSwitcher implements ClientModInitializer {
	private Timer timer;
	static public List<String> devices;
	static public String defaultDevice = "";
	static public String currentDevice = "";
	static public AudioSwitcher switcher;
	static boolean useDefault = true;

	@Override
	public void onInitializeClient() {
		updateDevices();
		switcher = this; //not good
	}

	public void onInitialized() {
		timer = new Timer("AudioSwitcherChecker");
		timer.scheduleAtFixedRate(new DisconnectCheckTask(), 0, 2000);
	}

	public void onStopSystem() {
		if(timer != null) timer.cancel();
	}

	static public void updateDevices() {
		devices = ALUtil.getStringList(0, ALC11.ALC_ALL_DEVICES_SPECIFIER);
		defaultDevice = ALC11.alcGetString(0, ALC11.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
		currentDevice = defaultDevice;
	}

	static public class DisconnectCheckTask extends TimerTask {
		@Override
		public void run() {
			SoundSystem soundSystem = ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem();
			SoundEngine engine = ((SoundSystemAccessor) soundSystem).getSoundEngine();
			SoundEngineAccessor accessor = (SoundEngineAccessor) engine;
			if(useDefault) {

				String latestDefaultDevice = ALC11.alcGetString(0, ALC11.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
				System.out.println(latestDefaultDevice + " vs " + defaultDevice);
				if(!latestDefaultDevice.equals(defaultDevice)) {
					defaultDevice = latestDefaultDevice;
					currentDevice = defaultDevice;
					soundSystem.reloadSounds();
					return;
				}
			}
			int connect = ALC11.alcGetInteger(accessor.getDevicePointer(), EXTDisconnect.ALC_CONNECTED);
			if(connect == ALC11.ALC_FALSE) {
				updateDevices();
				soundSystem.reloadSounds();
			}
		}
	}
}
