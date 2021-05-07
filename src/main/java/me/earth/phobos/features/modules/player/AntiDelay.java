// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import net.minecraft.client.settings.KeyBinding;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class AntiDelay extends Module
{
    public Setting<Mode> mode;
    public Setting<Integer> swordSlotSet;
    public Setting<Integer> crystaSlotSet;
    private static AntiDelay instance;
    private boolean didSwitch;
    
    public AntiDelay() {
        super("AntiDelay", "Removes Hotbar Delay", Category.PLAYER, false, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.SWORDCRYSTAL));
        this.swordSlotSet = (Setting<Integer>)this.register(new Setting("SwordSlot", 5, 1, 9));
        this.crystaSlotSet = (Setting<Integer>)this.register(new Setting("CrystalSlot", 1, 1, 9));
        this.didSwitch = false;
        AntiDelay.instance = this;
    }
    
    public static AntiDelay getInstance() {
        if (AntiDelay.instance == null) {
            AntiDelay.instance = new AntiDelay();
        }
        return AntiDelay.instance;
    }
    
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
    }
    
    public boolean processPressed(final KeyBinding binding) {
        int number = 0;
        try {
            number = Integer.parseInt(binding.getDisplayName());
        }
        catch (Exception ex) {}
        return binding.isPressed();
    }
    
    private void doSwitch(final int slot1, final int slot2) {
    }
    
    private int getSwordSlot() {
        return this.swordSlotSet.getValue() - 1;
    }
    
    private int getCrystalSlot() {
        return this.crystaSlotSet.getValue() - 1;
    }
    
    public enum Mode
    {
        SWORDCRYSTAL;
    }
}
