// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class HelpCommand extends Command
{
    public HelpCommand() {
        super("commands");
    }
    
    @Override
    public void execute(final String[] commands) {
        Command.sendMessage("You can use following commands: ");
        for (final Command command : Phobos.commandManager.getCommands()) {
            Command.sendMessage(Phobos.commandManager.getPrefix() + command.getName());
        }
    }
}
