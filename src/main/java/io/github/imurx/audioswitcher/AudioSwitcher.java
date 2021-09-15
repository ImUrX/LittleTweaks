package io.github.imurx.audioswitcher;

import io.github.imurx.audioswitcher.mixin.SoundEngineAccessor;
import io.github.imurx.audioswitcher.mixin.SoundManagerAccessor;
import io.github.imurx.audioswitcher.mixin.SoundSystemAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundEngine;
import org.lwjgl.openal.*;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AudioSwitcher implements ClientModInitializer {
	private Timer timer;
	static public List<String> devices;
	static public String defaultDevice = "";
	static public String currentDevice = "";
	static public AudioSwitcher switcher;

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

	public void updateDevices() {
		this.devices = ALUtil.getStringList(0, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER);
		this.defaultDevice = ALC11.alcGetString(0, EnumerateAllExt.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
		this.currentDevice = this.defaultDevice;
	}

	public class DisconnectCheckTask extends TimerTask {
		@Override
		public void run() {
			SoundEngine engine = ((SoundSystemAccessor) ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem()).getSoundEngine();
			SoundEngineAccessor accessor = (SoundEngineAccessor) engine;
			int connect = ALC11.alcGetInteger(accessor.getDevicePointer(), EXTDisconnect.ALC_CONNECTED);
			if(connect == ALC11.ALC_FALSE) {
				System.out.println("Current device got disconnected");
			}
		}
	}
}
