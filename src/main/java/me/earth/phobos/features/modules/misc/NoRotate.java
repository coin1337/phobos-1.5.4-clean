// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class NoRotate extends Module
{
    private Setting<Integer> waitDelay;
    private Timer timer;
    private boolean cancelPackets;
    private boolean timerReset;
    
    public NoRotate() {
        super("NoRotate", "Dangerous to use might desync you.", Category.MISC, true, false, false);
        this.waitDelay = (Setting<Integer>)this.register(new Setting("Delay", 2500, 0, 10000));
        this.timer = new Timer();
        this.cancelPackets = true;
        this.timerReset = false;
    }
    
    @Override
    public void onLogout() {
        this.cancelPackets = false;
    }
    
    @Override
    public void onLogin() {
        this.timer.reset();
        this.timerReset = true;
    }
    
    @Override
    public void onUpdate() {
        if (this.timerReset && !this.cancelPackets && this.timer.passedMs(this.waitDelay.getValue())) {
            Command.sendMessage("<NoRotate> §cThis module might desync you!");
            this.cancelPackets = true;
            this.timerReset = false;
        }
    }
    
    @Override
    public void onEnable() {
        Command.sendMessage("<NoRotate> §cThis module might desync you!");
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.cancelPackets && event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet = event.getPacket();
            //packet.yaw = NoRotate.mc.player.rotationYaw;
            //packet.pitch = NoRotate.mc.player.rotationPitch;
        }
    }
}
