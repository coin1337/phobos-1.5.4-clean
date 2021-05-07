// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.command.Command;
import me.earth.phobos.util.PlayerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Iterator;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import me.earth.phobos.event.events.PacketEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.UUID;
import java.util.Queue;
import me.earth.phobos.features.modules.Module;

public class AntiVanish extends Module
{
    private final Queue<UUID> toLookUp;
    
    public AntiVanish() {
        super("AntiVanish", "Notifies you when players vanish", Category.MISC, true, false, false);
        this.toLookUp = new ConcurrentLinkedQueue<UUID>();
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem sPacketPlayerListItem = event.getPacket();
            if (sPacketPlayerListItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
                for (final SPacketPlayerListItem.AddPlayerData addPlayerData : sPacketPlayerListItem.getEntries()) {
                    try {
                        if (AntiVanish.mc.getConnection().getPlayerInfo(addPlayerData.getProfile().getId()) != null) {
                            continue;
                        }
                        this.toLookUp.add(addPlayerData.getProfile().getId());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (PlayerUtil.timer.passedS(5.0)) {
            final UUID lookUp = this.toLookUp.poll();
            if (lookUp != null) {
                try {
                    final String name = PlayerUtil.getNameFromUUID(lookUp);
                    if (name != null) {
                        Command.sendMessage("§c" + name + " has gone into vanish.");
                    }
                }
                catch (Exception ex) {}
                PlayerUtil.timer.reset();
            }
        }
    }
    
    @Override
    public void onLogout() {
        this.toLookUp.clear();
    }
}
