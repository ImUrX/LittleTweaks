package io.github.imurx.audioswitcher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class RightClickableWidget extends ButtonWidget {
    PressAction rightAction;

    public RightClickableWidget(int x, int y, int width, int height, Text message, PressAction onPress, PressAction onRightClick) {
        super(x, y, width, height, message, onPress);
        this.rightAction = onRightClick;
    }

    public RightClickableWidget(int x, int y, int width, int height, Text message, PressAction onPress, PressAction onRightClick, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
        this.rightAction = onRightClick;
    }

    public void onRightClick(double mouseX, double mouseY) {
        rightAction.onPress(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidRightClick(button)) {
                boolean bl = this.clicked(mouseX, mouseY);
                if (bl) {
                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                    this.onRightClick(mouseX, mouseY);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean isValidRightClick(int button) {
        return button == 1;
    }
}
