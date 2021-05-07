// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class BetterPortals extends Module
{
    public Setting<Boolean> portalChat;
    public Setting<Boolean> godmode;
    public Setting<Boolean> fastPortal;
    public Setting<Integer> cooldown;
    public Setting<Integer> time;
    private static BetterPortals INSTANCE;
    
    public BetterPortals() {
        super("BetterPortals", "Tweaks for Portals", Category.MISC, true, false, false);
        this.portalChat = (Setting<Boolean>)this.register(new Setting("Chat", true, "Allows you to chat in portals."));
        this.godmode = (Setting<Boolean>)this.register(new Setting("Godmode", false, "Portal Godmode."));
        this.fastPortal = (Setting<Boolean>)this.register(new Setting("FastPortal", false));
        this.cooldown = (Setting<Integer>)this.register(new Setting("Cooldown", 5, 1, 10, v -> this.fastPortal.getValue(), "Portal cooldown."));
        this.time = (Setting<Integer>)this.register(new Setting("Time", 5, 0, 80, v -> this.fastPortal.getValue(), "Time in Portal"));
        this.setInstance();
    }
    
    private void setInstance() {
        BetterPortals.INSTANCE = this;
    }
    
    public static BetterPortals getInstance() {
        if (BetterPortals.INSTANCE == null) {
            BetterPortals.INSTANCE = new BetterPortals();
        }
        return BetterPortals.INSTANCE;
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.godmode.getValue()) {
            return "Godmode";
        }
        return null;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.godmode.getValue() && event.getPacket() instanceof CPacketConfirmTeleport) {
            event.setCanceled(true);
        }
    }
    
    static {
        BetterPortals.INSTANCE = new BetterPortals();
    }
}
