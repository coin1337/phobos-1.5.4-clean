// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.manager;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.Phobos;
import net.minecraft.network.play.client.CPacketChatMessage;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import net.minecraftforge.common.MinecraftForge;
import me.earth.phobos.features.Feature;

public class ReloadManager extends Feature
{
    public String prefix;
    
    public void init(final String prefix) {
        this.prefix = prefix;
        MinecraftForge.EVENT_BUS.register((Object)this);
        if (!Feature.fullNullCheck()) {
            Command.sendMessage("§cPhobos has been unloaded. Type " + prefix + "reload to reload.");
        }
    }
    
    public void unload() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            final CPacketChatMessage packet = event.getPacket();
            if (packet.getMessage().startsWith(this.prefix) && packet.getMessage().contains("reload")) {
                Phobos.load();
                event.setCanceled(true);
            }
        }
    }
}
