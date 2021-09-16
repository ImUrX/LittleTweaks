package io.github.imurx.littletweaks.mixin;

import io.github.imurx.littletweaks.LittleConfig;
import io.github.imurx.littletweaks.LittleTweaks;
import io.github.imurx.littletweaks.RightClickableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(SoundOptionsScreen.class)
public class SoundOptionsScreenMixin extends GameOptionsScreen {
	@Unique
	public int selectedIndex = 0;

	public SoundOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	@Redirect(
		method = "init()V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screen/option/SoundOptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
			ordinal = 2
		)
	)
	private Element addSubtitleWidget(SoundOptionsScreen soundOptionsScreen, Element widget) {
		if(!(widget instanceof ClickableWidget subtitleWidget)) {
			throw new IllegalStateException("The subtitle button isn't a ClickableWidget!");
		}
		LittleTweaks.updateDevices();
		if(!LittleTweaks.getConfig().useDefaultDevice) {
			selectedIndex = LittleTweaks.devices.indexOf(LittleTweaks.currentDevice) + 1;
		} else {
			selectedIndex = 0;
		}
		subtitleWidget.x = this.width / 2 + 5;
		subtitleWidget.y -= 24;
		this.addDrawableChild(subtitleWidget);
		String option = LittleTweaks.getConfig().useDefaultDevice ? "Device: System Default" : "Device: " + LittleTweaks.currentDevice.replaceAll("OpenAL Soft on ", "");
		ButtonWidget sourcesWidget = new RightClickableWidget(subtitleWidget.x - 160, subtitleWidget.y + 24, subtitleWidget.getWidth() * 2 + 10, subtitleWidget.getHeight(), Text.of(option), (button) -> {
			if(++selectedIndex > LittleTweaks.devices.size()) {
				selectedIndex = 0;
			}
			updateDevice(button);
		}, (button -> {
			if(--selectedIndex < 0) {
				selectedIndex = LittleTweaks.devices.size();
			}
			updateDevice(button);
		}));
		this.addDrawableChild(sourcesWidget);
		return widget;
	}

	@Unique
	private void updateDevice(ClickableWidget button) {
		SoundSystem soundSystem = ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem();
		if(selectedIndex == 0) {
			LittleTweaks.getConfig().useDefaultDevice = true;
			button.setMessage(Text.of("Device: System Default"));
			soundSystem.reloadSounds();
			LittleTweaks.saveConfig();
			return;
		} else if(LittleTweaks.getConfig().useDefaultDevice) {
			LittleTweaks.getConfig().useDefaultDevice = false;
		}
		LittleTweaks.currentDevice = LittleTweaks.devices.get(selectedIndex - 1);
		LittleTweaks.getConfig().preferredDevice = LittleTweaks.currentDevice;
		button.setMessage(Text.of("Device: " + LittleTweaks.currentDevice.replaceAll("OpenAL Soft on ", "")));
		LittleTweaks.restartSoundSystem(LittleTweaks.currentDevice);
		LittleTweaks.saveConfig();
	}
}
