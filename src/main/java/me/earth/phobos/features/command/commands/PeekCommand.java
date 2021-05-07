// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Map;
import me.earth.phobos.features.modules.misc.ToolTips;
import net.minecraft.item.ItemShulkerBox;
import me.earth.phobos.features.command.Command;

public class PeekCommand extends Command
{
    public PeekCommand() {
        super("peek", new String[] { "<player>" });
    }
    
    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            final ItemStack stack = PeekCommand.mc.player.getHeldItemMainhand();
            if (stack == null || !(stack.getItem() instanceof ItemShulkerBox)) {
                Command.sendMessage("§cYou need to hold a Shulker in your mainhand.");
                return;
            }
            ToolTips.displayInv(stack, null);
        }
        if (commands.length > 1) {
            if (ToolTips.getInstance().isOn() && ToolTips.getInstance().shulkerSpy.getValue()) {
                for (final Map.Entry<EntityPlayer, ItemStack> entry : ToolTips.getInstance().spiedPlayers.entrySet()) {
                    if (entry.getKey().getName().equalsIgnoreCase(commands[0])) {
                        final ItemStack stack2 = entry.getValue();
                        ToolTips.displayInv(stack2, entry.getKey().getName());
                        break;
                    }
                }
            }
            else {
                Command.sendMessage("§cYou need to turn on Tooltips - ShulkerSpy");
            }
        }
    }
}
