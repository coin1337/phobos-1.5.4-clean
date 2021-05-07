// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.render.XRay;
import me.earth.phobos.features.command.Command;

public class XrayCommand extends Command
{
    public XrayCommand() {
        super("xray", new String[] { "<add/del>", "<block>" });
    }
    
    @Override
    public void execute(final String[] commands) {
        final XRay module = Phobos.moduleManager.getModuleByClass(XRay.class);
        if (module != null) {
            if (commands.length == 1) {
                final StringBuilder blocks = new StringBuilder();
                for (final Setting setting : module.getSettings()) {
                    if (!setting.equals(module.enabled) && !setting.equals(module.drawn) && !setting.equals(module.bind) && !setting.equals(module.newBlock)) {
                        if (setting.equals(module.showBlocks)) {
                            continue;
                        }
                        blocks.append(setting.getName()).append(", ");
                    }
                }
                Command.sendMessage(blocks.toString());
                return;
            }
            if (commands.length == 2) {
                Command.sendMessage("Please specify a block.");
                return;
            }
            final String addRemove = commands[0];
            final String blockName = commands[1];
            if (addRemove.equalsIgnoreCase("del") || addRemove.equalsIgnoreCase("remove")) {
                final Setting setting = module.getSettingByName(blockName);
                if (setting != null) {
                    if (setting.equals(module.enabled) || setting.equals(module.drawn) || setting.equals(module.bind) || setting.equals(module.newBlock) || setting.equals(module.showBlocks)) {
                        return;
                    }
                    module.unregister(setting);
                }
                Command.sendMessage("<XRay>§c Removed: " + blockName);
            }
            else if (addRemove.equalsIgnoreCase("add")) {
                if (!module.shouldRender(blockName)) {
                    module.register(new Setting(blockName, true, v -> module.showBlocks.getValue()));
                    Command.sendMessage("<Xray> Added new Block: " + blockName);
                }
            }
            else {
                Command.sendMessage("§cAn error occured, block either exists or wrong use of command: .xray <add/del(remove)> <block>");
            }
        }
    }
}
