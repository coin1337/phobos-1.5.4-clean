// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraft.inventory.Slot;
import me.earth.phobos.util.TextUtil;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.inventory.GuiContainer;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class KitDelete extends Module
{
    private Setting<Bind> deleteKey;
    private boolean keyDown;
    
    public KitDelete() {
        super("KitDelete", "Automates /deleteukit", Category.MISC, false, false, false);
        this.deleteKey = (Setting<Bind>)this.register(new Setting("Key", new Bind(-1)));
    }
    
    @Override
    public void onTick() {
        if (this.deleteKey.getValue().getKey() != -1) {
            if (KitDelete.mc.currentScreen instanceof GuiContainer && Keyboard.isKeyDown(this.deleteKey.getValue().getKey())) {
                final Slot slot = ((GuiContainer)KitDelete.mc.currentScreen).getSlotUnderMouse();
                if (slot != null && !this.keyDown) {
                    KitDelete.mc.player.sendChatMessage("/deleteukit " + TextUtil.stripColor(slot.getStack().getDisplayName()));
                    this.keyDown = true;
                }
            }
            else if (this.keyDown) {
                this.keyDown = false;
            }
        }
    }
}
