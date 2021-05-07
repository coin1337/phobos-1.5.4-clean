// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;

public class TrueDurability extends Module
{
    private static TrueDurability instance;
    
    public TrueDurability() {
        super("TrueDurability", "Shows True Durability of items", Category.PLAYER, false, false, false);
        TrueDurability.instance = this;
    }
    
    public static TrueDurability getInstance() {
        if (TrueDurability.instance == null) {
            TrueDurability.instance = new TrueDurability();
        }
        return TrueDurability.instance;
    }
}
