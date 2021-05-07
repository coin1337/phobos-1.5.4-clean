// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import java.util.Random;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class NoAFK extends Module
{
    private final Setting<Boolean> swing;
    private final Setting<Boolean> turn;
    private final Random random;
    
    public NoAFK() {
        super("NoAFK", "Prevents you from getting kicked for afk.", Category.MISC, false, false, false);
        this.swing = (Setting<Boolean>)this.register(new Setting("Swing", true));
        this.turn = (Setting<Boolean>)this.register(new Setting("Turn", true));
        this.random = new Random();
    }
    
    @Override
    public void onUpdate() {
        if (NoAFK.mc.playerController.getIsHittingBlock()) {
            return;
        }
        if (NoAFK.mc.player.ticksExisted % 40 == 0 && this.swing.getValue()) {
            NoAFK.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        if (NoAFK.mc.player.ticksExisted % 15 == 0 && this.turn.getValue()) {
            NoAFK.mc.player.rotationYaw = (float)(this.random.nextInt(360) - 180);
        }
        if (!this.swing.getValue() && !this.turn.getValue() && NoAFK.mc.player.ticksExisted % 80 == 0) {
            NoAFK.mc.player.jump();
        }
    }
}
