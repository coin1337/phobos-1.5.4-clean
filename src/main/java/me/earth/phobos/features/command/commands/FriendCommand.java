// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import java.util.UUID;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class FriendCommand extends Command
{
    public FriendCommand() {
        super("friend", new String[] { "<add/del/name/clear>", "<name>" });
    }
    
    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            if (Phobos.friendManager.getFriends().isEmpty()) {
                Command.sendMessage("You currently dont have any friends added.");
            }
            else {
                Command.sendMessage("Friends: ");
                for (final Map.Entry<String, UUID> entry : Phobos.friendManager.getFriends().entrySet()) {
                    Command.sendMessage(entry.getKey());
                }
            }
            return;
        }
        if (commands.length == 2) {
            final String s = commands[0];
            switch (s) {
                case "reset": {
                    Phobos.friendManager.onLoad();
                    Command.sendMessage("Friends got reset.");
                    break;
                }
                default: {
                    Command.sendMessage(commands[0] + (Phobos.friendManager.isFriend(commands[0]) ? " is friended." : " isnt friended."));
                    break;
                }
            }
            return;
        }
        if (commands.length >= 2) {
            final String s2 = commands[0];
            switch (s2) {
                case "add": {
                    Phobos.friendManager.addFriend(commands[1]);
                    Command.sendMessage("§b" + commands[1] + " has been friended");
                    break;
                }
                case "del": {
                    Phobos.friendManager.removeFriend(commands[1]);
                    Command.sendMessage("§c" + commands[1] + " has been unfriended");
                    break;
                }
                default: {
                    Command.sendMessage("§cBad Command, try: friend <add/del/name> <name>.");
                    break;
                }
            }
        }
    }
}
