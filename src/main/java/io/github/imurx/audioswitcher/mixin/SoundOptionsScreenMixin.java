package io.github.imurx.audioswitcher.mixin;

import io.github.imurx.audioswitcher.AudioSwitcher;
import io.github.imurx.audioswitcher.RightClickableWidget;
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
		AudioSwitcher.updateDevices();
		if(!AudioSwitcher.useDefault) {
			selectedIndex = AudioSwitcher.devices.indexOf(AudioSwitcher.currentDevice) + 1;
		} else {
			selectedIndex = 0;
		}
		subtitleWidget.x = this.width / 2 + 5;
		subtitleWidget.y -= 24;
		this.addDrawableChild(subtitleWidget);
		String option = AudioSwitcher.useDefault ? "Device: System Default" : "Device: " + AudioSwitcher.currentDevice.replaceAll("OpenAL Soft on ", "");
		ButtonWidget sourcesWidget = new RightClickableWidget(subtitleWidget.x - 160, subtitleWidget.y + 24, subtitleWidget.getWidth() * 2 + 10, subtitleWidget.getHeight(), Text.of(option), (button) -> {
			if(++selectedIndex > AudioSwitcher.devices.size()) {
				selectedIndex = 0;
			}
			updateDevice(button);
		}, (button -> {
			if(--selectedIndex < 0) {
				selectedIndex = AudioSwitcher.devices.size();
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
			AudioSwitcher.useDefault = true;
			button.setMessage(Text.of("Device: System Default"));
			soundSystem.reloadSounds();
			return;
		} else if(AudioSwitcher.useDefault) {
			AudioSwitcher.useDefault = false;
		}
		AudioSwitcher.currentDevice = AudioSwitcher.devices.get(selectedIndex - 1);
		AudioSwitcher.preferredDevice = AudioSwitcher.currentDevice;
		button.setMessage(Text.of("Device: " + AudioSwitcher.currentDevice.replaceAll("OpenAL Soft on ", "")));
		AudioSwitcher.restartSoundSystem(AudioSwitcher.currentDevice);
	}
}
