// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class SmallShield extends Module
{
    public Setting<Boolean> normalOffset;
    public Setting<Float> offset;
    public Setting<Float> offX;
    public Setting<Float> offY;
    public Setting<Float> mainX;
    public Setting<Float> mainY;
    private static SmallShield INSTANCE;
    
    public SmallShield() {
        super("SmallShield", "Makes you offhand lower.", Category.RENDER, false, false, false);
        this.normalOffset = (Setting<Boolean>)this.register(new Setting("OffNormal", false));
        this.offset = (Setting<Float>)this.register(new Setting("Offset", 0.7f, 0.0f, 1.0f, v -> this.normalOffset.getValue()));
        this.offX = (Setting<Float>)this.register(new Setting("OffX", 0.0f, (-1.0f), 1.0f, v -> !this.normalOffset.getValue()));
        this.offY = (Setting<Float>)this.register(new Setting("OffY", 0.0f, (-1.0f), 1.0f, v -> !this.normalOffset.getValue()));
        this.mainX = (Setting<Float>)this.register(new Setting("MainX", 0.0f, (-1.0f), 1.0f));
        this.mainY = (Setting<Float>)this.register(new Setting("MainY", 0.0f, (-1.0f), 1.0f));
        this.setInstance();
    }
    
    private void setInstance() {
        SmallShield.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (this.normalOffset.getValue()) {
            //SmallShield.mc.entityRenderer.itemRenderer.equippedProgressOffHand = this.offset.getValue();
        }
    }
    
    public static SmallShield getINSTANCE() {
        if (SmallShield.INSTANCE == null) {
            SmallShield.INSTANCE = new SmallShield();
        }
        return SmallShield.INSTANCE;
    }
    
    static {
        SmallShield.INSTANCE = new SmallShield();
    }
}
