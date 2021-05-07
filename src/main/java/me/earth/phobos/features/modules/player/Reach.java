// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Reach extends Module
{
    public Setting<Boolean> override;
    public Setting<Float> add;
    public Setting<Float> reach;
    private static Reach INSTANCE;
    
    public Reach() {
        super("Reach", "Extends your block reach", Category.PLAYER, true, false, false);
        this.override = (Setting<Boolean>)this.register(new Setting("Override", false));
        this.add = (Setting<Float>)this.register(new Setting("Add", 3.0f, v -> !this.override.getValue()));
        this.reach = (Setting<Float>)this.register(new Setting("Reach", 6.0f, v -> this.override.getValue()));
        this.setInstance();
    }
    
    private void setInstance() {
        Reach.INSTANCE = this;
    }
    
    public static Reach getInstance() {
        if (Reach.INSTANCE == null) {
            Reach.INSTANCE = new Reach();
        }
        return Reach.INSTANCE;
    }
    
    @Override
    public String getDisplayInfo() {
        return this.override.getValue() ? this.reach.getValue().toString() : this.add.getValue().toString();
    }
    
    static {
        Reach.INSTANCE = new Reach();
    }
}
