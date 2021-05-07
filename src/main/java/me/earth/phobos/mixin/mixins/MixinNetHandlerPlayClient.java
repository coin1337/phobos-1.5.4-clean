// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.entity.Entity;
import me.earth.phobos.Phobos;
import net.minecraftforge.fml.common.eventhandler.Event;
import me.earth.phobos.event.events.DeathEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ NetHandlerPlayClient.class })
public class MixinNetHandlerPlayClient
{
    @Inject(method = { "handleEntityMetadata" }, at = { @At("RETURN") }, cancellable = true)
    private void handleEntityMetadataHook(final SPacketEntityMetadata packetIn, final CallbackInfo info) {
        if (Util.mc.world != null) {
            final Entity entity = Util.mc.world.getEntityByID(packetIn.getEntityId());
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)entity;
                if (player.getHealth() <= 0.0f) {
                    MinecraftForge.EVENT_BUS.post((Event)new DeathEvent(player));
                    if (Phobos.totemPopManager != null) {
                        Phobos.totemPopManager.onDeath(player);
                    }
                }
            }
        }
    }
}
