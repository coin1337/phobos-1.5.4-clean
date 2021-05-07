// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import java.lang.reflect.Field;
import net.minecraft.util.StringUtils;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Logger extends Module
{
    public Setting<Packets> packets;
    public Setting<Boolean> chat;
    public Setting<Boolean> fullInfo;
    
    public Logger() {
        super("Logger", "Logs stuff", Category.MISC, true, false, false);
        this.packets = (Setting<Packets>)this.register(new Setting("Packets", Packets.OUTGOING));
        this.chat = (Setting<Boolean>)this.register(new Setting("Chat", false));
        this.fullInfo = (Setting<Boolean>)this.register(new Setting("FullInfo", false));
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onPacketSend(final PacketEvent.Send event) {
        if (this.packets.getValue() == Packets.OUTGOING || this.packets.getValue() == Packets.ALL) {
            if (this.chat.getValue()) {
                Command.sendMessage(event.getPacket().toString());
            }
            else {
                this.writePacketOnConsole(event.getPacket(), false);
            }
        }
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (this.packets.getValue() == Packets.INCOMING || this.packets.getValue() == Packets.ALL) {
            if (this.chat.getValue()) {
                Command.sendMessage(event.getPacket().toString());
            }
            else {
                this.writePacketOnConsole(event.getPacket(), true);
            }
        }
    }
    
    private void writePacketOnConsole(final Packet<?> packet, final boolean in) {
        if (this.fullInfo.getValue()) {
            System.out.println((in ? "In: " : "Send: ") + packet.getClass().getSimpleName() + " {");
            try {
                for (Class clazz = packet.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                    for (final Field field : clazz.getDeclaredFields()) {
                        if (field != null) {
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                            }
                            System.out.println(StringUtils.stripControlCodes("      " + field.getType().getSimpleName() + " " + field.getName() + " : " + field.get(packet)));
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("}");
        }
        else {
            System.out.println(packet.toString());
        }
    }
    
    public enum Packets
    {
        NONE, 
        INCOMING, 
        OUTGOING, 
        ALL;
    }
}
