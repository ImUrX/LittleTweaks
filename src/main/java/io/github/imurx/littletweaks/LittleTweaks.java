package io.github.imurx.littletweaks;

import io.github.imurx.littletweaks.events.SoundSystemCallback;
import io.github.imurx.littletweaks.mixin.SoundEngineAccessor;
import io.github.imurx.littletweaks.mixin.SoundManagerAccessor;
import io.github.imurx.littletweaks.mixin.SoundSystemAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundSystem;
import org.lwjgl.openal.*;

import java.util.List;

public class LittleTweaks implements ClientModInitializer {
	private int tickCounter = -1;
	static private Thread audioThread;
	static public List<String> devices;
	static public String defaultDevice = "";
	static public String currentDevice = "";

	@Override
	public void onInitializeClient() {
		AutoConfig.register(LittleConfig.class, Toml4jConfigSerializer::new);
		this.audioSwitcher();

	}

	private void audioSwitcher() {
		updateDevices();
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(tickCounter < 0 || ++tickCounter < LittleTweaks.getConfig().tickAudioEvery) return;
			tickCounter = 0;

			if(audioThread != null) {
				try {
					audioThread.join();
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
				audioThread = null;
				return;
			}

			SoundEngine engine = ((SoundSystemAccessor) ((SoundManagerAccessor) client.getSoundManager()).getSoundSystem()).getSoundEngine();
			SoundEngineAccessor accessor = (SoundEngineAccessor) engine;
			int connect = ALC11.alcGetInteger(accessor.getDevicePointer(), EXTDisconnect.ALC_CONNECTED);
			updateDevices();

			if(connect == ALC11.ALC_FALSE) {
				audioThread = new Thread(() -> restartSoundSystem(defaultDevice));
			} else if(!currentDevice.equals(getConfig().preferredDevice) && devices.contains(getConfig().preferredDevice)) {
				audioThread = new Thread(() -> restartSoundSystem(getConfig().preferredDevice));
			} else {
				return;
			}

			audioThread.setName("LittleTweaks/AudioSwitcher");
			audioThread.start();
		});
		SoundSystemCallback.STARTED_SYSTEM.register((_x) -> tickCounter = 0);
		SoundSystemCallback.STOPPING_SYSTEM.register((_x) -> tickCounter = -1);
	}

	private void screenshotClipboard() {

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

	static public LittleConfig getConfig() {
		return AutoConfig.getConfigHolder(LittleConfig.class).getConfig();
	}
	static public void saveConfig() {
		AutoConfig.getConfigHolder(LittleConfig.class).save();
	}
}
