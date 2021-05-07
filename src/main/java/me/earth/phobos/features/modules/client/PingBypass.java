// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.features.command.Command;
import java.util.Objects;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;

public class PingBypass extends Module
{
    public PingBypass() {
        super("PingBypass", "Big Hack", Category.CLIENT, true, false, false);
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = event.getPacket();
            Command.sendMessage(Objects.requireNonNull(packet.getEntityFromWorld((World)PingBypass.mc.world)).getEntityId() + "");
        }
    }
}
