// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.util.MathUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class AutoLog extends Module
{
    private Setting<Float> health;
    private Setting<Boolean> bed;
    private Setting<Float> range;
    private Setting<Boolean> logout;
    private static AutoLog INSTANCE;
    
    public AutoLog() {
        super("AutoLog", "Logs when in danger.", Category.MISC, false, false, false);
        this.health = (Setting<Float>)this.register(new Setting("Health", 16.0f, 0.1f, 36.0f));
        this.bed = (Setting<Boolean>)this.register(new Setting("Beds", true));
        this.range = (Setting<Float>)this.register(new Setting("BedRange", 6.0f, 0.1f, 36.0f, v -> this.bed.getValue()));
        this.logout = (Setting<Boolean>)this.register(new Setting("LogoutOff", true));
        this.setInstance();
    }
    
    private void setInstance() {
        AutoLog.INSTANCE = this;
    }
    
    public static AutoLog getInstance() {
        if (AutoLog.INSTANCE == null) {
            AutoLog.INSTANCE = new AutoLog();
        }
        return AutoLog.INSTANCE;
    }
    
    @Override
    public void onTick() {
        if (!Feature.nullCheck() && AutoLog.mc.player.getHealth() <= this.health.getValue()) {
            Phobos.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
            if (this.logout.getValue()) {
                this.disable();
            }
        }
    }
    
    @SubscribeEvent
    public void onReceivePacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockChange && this.bed.getValue()) {
            final SPacketBlockChange packet = event.getPacket();
            if (packet.getBlockState().getBlock() == Blocks.BED && AutoLog.mc.player.getDistanceSqToCenter(packet.getBlockPosition()) <= MathUtil.square(this.range.getValue())) {
                Phobos.moduleManager.disableModule("AutoReconnect");
                AutoLog.mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
                if (this.logout.getValue()) {
                    this.disable();
                }
            }
        }
    }
    
    static {
        AutoLog.INSTANCE = new AutoLog();
    }
}
