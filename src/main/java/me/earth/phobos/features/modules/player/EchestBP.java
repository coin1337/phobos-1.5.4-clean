// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.Feature;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.GuiScreen;
import me.earth.phobos.features.modules.Module;

public class EchestBP extends Module
{
    private GuiScreen echestScreen;
    
    public EchestBP() {
        super("EchestBP", "Allows to open your echest later.", Category.PLAYER, false, false, false);
        this.echestScreen = null;
    }
    
    @Override
    public void onUpdate() {
        if (EchestBP.mc.currentScreen instanceof GuiContainer) {
            final Container container = ((GuiContainer)EchestBP.mc.currentScreen).inventorySlots;
            if (container instanceof ContainerChest && ((ContainerChest)container).getLowerChestInventory() instanceof InventoryBasic) {
                final InventoryBasic basic = (InventoryBasic)((ContainerChest)container).getLowerChestInventory();
                if (basic.getName().equalsIgnoreCase("Ender Chest")) {
                    this.echestScreen = EchestBP.mc.currentScreen;
                    EchestBP.mc.currentScreen = null;
                }
            }
        }
    }
    
    @Override
    public void onDisable() {
        if (!Feature.fullNullCheck() && this.echestScreen != null) {
            EchestBP.mc.displayGuiScreen(this.echestScreen);
        }
        this.echestScreen = null;
    }
}
