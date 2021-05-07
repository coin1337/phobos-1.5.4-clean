// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;

public class MultiTask extends Module
{
    private static MultiTask INSTANCE;
    
    public MultiTask() {
        super("MultiTask", "Allows you to eat while mining.", Category.PLAYER, false, false, false);
        this.setInstance();
    }
    
    private void setInstance() {
        MultiTask.INSTANCE = this;
    }
    
    public static MultiTask getInstance() {
        if (MultiTask.INSTANCE == null) {
            MultiTask.INSTANCE = new MultiTask();
        }
        return MultiTask.INSTANCE;
    }
    
    static {
        MultiTask.INSTANCE = new MultiTask();
    }
}
