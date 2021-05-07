// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraft.entity.Entity;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.Phobos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.features.gui.PhobosGui;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class MCF extends Module
{
    private final Setting<Boolean> middleClick;
    private final Setting<Boolean> keyboard;
    private final Setting<Bind> key;
    private boolean clicked;
    
    public MCF() {
        super("MCF", "Middleclick Friends.", Category.MISC, true, false, false);
        this.middleClick = (Setting<Boolean>)this.register(new Setting("MiddleClick", true));
        this.keyboard = (Setting<Boolean>)this.register(new Setting("Keyboard", false));
        this.key = (Setting<Bind>)this.register(new Setting("KeyBind", new Bind(-1), v -> this.keyboard.getValue()));
        this.clicked = false;
    }
    
    @Override
    public void onUpdate() {
        if (Mouse.isButtonDown(2)) {
            if (!this.clicked && this.middleClick.getValue() && MCF.mc.currentScreen == null) {
                this.onClick();
            }
            this.clicked = true;
        }
        else {
            this.clicked = false;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (this.keyboard.getValue() && Keyboard.getEventKeyState() && !(MCF.mc.currentScreen instanceof PhobosGui) && this.key.getValue().getKey() == Keyboard.getEventKey()) {
            this.onClick();
        }
    }
    
    private void onClick() {
        final RayTraceResult result = MCF.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
            final Entity entity = result.entityHit;
            if (entity instanceof EntityPlayer) {
                if (Phobos.friendManager.isFriend(entity.getName())) {
                    Phobos.friendManager.removeFriend(entity.getName());
                    Command.sendMessage("§c" + entity.getName() + "§r" + " unfriended.");
                }
                else {
                    Phobos.friendManager.addFriend(entity.getName());
                    Command.sendMessage("§b" + entity.getName() + "§r" + " friended.");
                }
            }
        }
        this.clicked = true;
    }
}
