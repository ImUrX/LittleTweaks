package io.github.imurx.audioswitcher.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;

import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(SoundOptionsScreen.class)
public class SoundOptionsScreenMixin extends GameOptionsScreen {
	boolean hey = true;
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
		if(!(widget instanceof ClickableWidget)) {
			throw new IllegalStateException("The subtitle button isn't a ClickableWidget!");
		}
		ClickableWidget subtitleWidget = (ClickableWidget) widget;
		subtitleWidget.x = this.width / 2 - 155;
		this.addDrawableChild(subtitleWidget);
		ButtonWidget sourcesWidget = new ButtonWidget(subtitleWidget.x + 160, subtitleWidget.y, subtitleWidget.getWidth(), subtitleWidget.getHeight(), Text.of("true"), (button)  -> {
			System.out.println("pressed the button");
			this.hey = !this.hey;
			button.setMessage(this.hey ? Text.of("true") : Text.of("false"));

		});
		this.addDrawableChild(sourcesWidget);
		return widget;
	}
}
