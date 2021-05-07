// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class CameraClip extends Module
{
    public Setting<Boolean> extend;
    public Setting<Double> distance;
    private static CameraClip INSTANCE;
    
    public CameraClip() {
        super("CameraClip", "Makes your Camera clip.", Category.RENDER, false, false, false);
        this.extend = (Setting<Boolean>)this.register(new Setting("Extend", false));
        this.distance = (Setting<Double>)this.register(new Setting("Distance", 10.0, 0.0, 50.0, v -> this.extend.getValue(), "By how much you want to extend the distance."));
        this.setInstance();
    }
    
    private void setInstance() {
        CameraClip.INSTANCE = this;
    }
    
    public static CameraClip getInstance() {
        if (CameraClip.INSTANCE == null) {
            CameraClip.INSTANCE = new CameraClip();
        }
        return CameraClip.INSTANCE;
    }
    
    static {
        CameraClip.INSTANCE = new CameraClip();
    }
}
