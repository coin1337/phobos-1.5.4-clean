// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class XRay extends Module
{
    public Setting<String> newBlock;
    public Setting<Boolean> showBlocks;
    private static XRay INSTANCE;
    
    public XRay() {
        super("XRay", "Lets you look through walls.", Category.RENDER, false, false, true);
        this.newBlock = (Setting<String>)this.register(new Setting("NewBlock", "Add Block..."));
        this.showBlocks = (Setting<Boolean>)this.register(new Setting("ShowBlocks", false));
        this.setInstance();
    }
    
    private void setInstance() {
        XRay.INSTANCE = this;
    }
    
    public static XRay getInstance() {
        if (XRay.INSTANCE == null) {
            XRay.INSTANCE = new XRay();
        }
        return XRay.INSTANCE;
    }
    
    @Override
    public void onEnable() {
        XRay.mc.renderGlobal.loadRenderers();
    }
    
    @Override
    public void onDisable() {
        XRay.mc.renderGlobal.loadRenderers();
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.newBlock) && !this.shouldRender(this.newBlock.getPlannedValue())) {
                this.register(new Setting(this.newBlock.getPlannedValue(), true, v -> this.showBlocks.getValue()));
                Command.sendMessage("<Xray> Added new Block: " + this.newBlock.getPlannedValue());
                if (this.isOn()) {
                    XRay.mc.renderGlobal.loadRenderers();
                }
                event.setCanceled(true);
            }
            else {
                final Setting setting = event.getSetting();
                if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newBlock) || setting.equals(this.showBlocks)) {
                    return;
                }
                if (setting.getValue() instanceof Boolean && !(Boolean)setting.getPlannedValue()) {
                    this.unregister(setting);
                    if (this.isOn()) {
                        XRay.mc.renderGlobal.loadRenderers();
                    }
                    event.setCanceled(true);
                }
            }
        }
    }
    
    public boolean shouldRender(final Block block) {
        return this.shouldRender(block.getLocalizedName());
    }
    
    public boolean shouldRender(final String name) {
        for (final Setting setting : this.getSettings()) {
            if (name.equalsIgnoreCase(setting.getName())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        XRay.INSTANCE = new XRay();
    }
}
