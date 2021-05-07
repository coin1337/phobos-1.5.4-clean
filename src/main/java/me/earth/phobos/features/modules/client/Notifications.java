// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.client;

import net.minecraft.client.gui.ChatLine;
import me.earth.phobos.event.events.ClientEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketSpawnObject;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.manager.FileManager;
import java.util.Iterator;
import me.earth.phobos.Phobos;
import java.util.Collection;
import me.earth.phobos.features.command.Command;
import java.util.ArrayList;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Notifications extends Module
{
    public Setting<Boolean> totemPops;
    public Setting<Boolean> totemNoti;
    public Setting<Integer> delay;
    public Setting<Boolean> clearOnLogout;
    public Setting<Boolean> moduleMessage;
    public Setting<Boolean> list;
    private Setting<Boolean> readfile;
    public Setting<Boolean> watermark;
    public Setting<Boolean> visualRange;
    public Setting<Boolean> coords;
    public Setting<Boolean> leaving;
    public Setting<Boolean> pearls;
    public Setting<Boolean> crash;
    private List<EntityPlayer> knownPlayers;
    private static List<String> modules;
    private static final String fileName = "phobos/util/ModuleMessage_List.txt";
    private final Timer timer;
    public Timer totemAnnounce;
    private boolean check;
    private static Notifications INSTANCE;
    
    public Notifications() {
        super("Notifications", "Sends Messages.", Category.CLIENT, true, false, false);
        this.totemPops = (Setting<Boolean>)this.register(new Setting("TotemPops", false));
        this.totemNoti = (Setting<Boolean>)this.register(new Setting("TotemNoti", true, v -> this.totemPops.getValue()));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 2000, 0, 5000, v -> this.totemPops.getValue(), "Delays messages."));
        this.clearOnLogout = (Setting<Boolean>)this.register(new Setting("LogoutClear", false));
        this.moduleMessage = (Setting<Boolean>)this.register(new Setting("ModuleMessage", false));
        this.list = (Setting<Boolean>)this.register(new Setting("List", false, v -> this.moduleMessage.getValue()));
        this.readfile = (Setting<Boolean>)this.register(new Setting("LoadFile", false, v -> this.moduleMessage.getValue()));
        this.watermark = (Setting<Boolean>)this.register(new Setting("Watermark", true, v -> this.moduleMessage.getValue()));
        this.visualRange = (Setting<Boolean>)this.register(new Setting("VisualRange", false));
        this.coords = (Setting<Boolean>)this.register(new Setting("Coords", true, v -> this.visualRange.getValue()));
        this.leaving = (Setting<Boolean>)this.register(new Setting("Leaving", false, v -> this.visualRange.getValue()));
        this.pearls = (Setting<Boolean>)this.register(new Setting("PearlNotifs", false));
        this.crash = (Setting<Boolean>)this.register(new Setting("Crash", false));
        this.knownPlayers = new ArrayList<EntityPlayer>();
        this.timer = new Timer();
        this.totemAnnounce = new Timer();
        this.setInstance();
    }
    
    private void setInstance() {
        Notifications.INSTANCE = this;
    }
    
    @Override
    public void onLoad() {
        this.check = true;
        this.loadFile();
        this.check = false;
    }
    
    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<EntityPlayer>();
        if (!this.check) {
            this.loadFile();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.readfile.getValue()) {
            if (!this.check) {
                Command.sendMessage("Loading File...");
                this.timer.reset();
                this.loadFile();
            }
            this.check = true;
        }
        if (this.check && this.timer.passedMs(750L)) {
            this.readfile.setValue(false);
            this.check = false;
        }
        if (this.visualRange.getValue()) {
            final List<EntityPlayer> tickPlayerList = new ArrayList<EntityPlayer>(Notifications.mc.world.playerEntities);
            if (tickPlayerList.size() > 0) {
                for (final EntityPlayer player : tickPlayerList) {
                    if (player.getName().equals(Notifications.mc.player.getName())) {
                        continue;
                    }
                    if (!this.knownPlayers.contains(player)) {
                        this.knownPlayers.add(player);
                        if (Phobos.friendManager.isFriend(player)) {
                            Command.sendMessage("Player §a" + player.getName() + "§r" + " entered your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
                        }
                        else {
                            Command.sendMessage("Player §c" + player.getName() + "§r" + " entered your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
                        }
                        return;
                    }
                }
            }
            if (this.knownPlayers.size() > 0) {
                for (final EntityPlayer player : this.knownPlayers) {
                    if (!tickPlayerList.contains(player)) {
                        this.knownPlayers.remove(player);
                        if (this.leaving.getValue()) {
                            if (Phobos.friendManager.isFriend(player)) {
                                Command.sendMessage("Player §a" + player.getName() + "§r" + " left your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
                            }
                            else {
                                Command.sendMessage("Player §c" + player.getName() + "§r" + " left your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void loadFile() {
        final List<String> fileInput = FileManager.readTextFileAllLines("phobos/util/ModuleMessage_List.txt");
        final Iterator<String> i = fileInput.iterator();
        Notifications.modules.clear();
        while (i.hasNext()) {
            final String s = i.next();
            if (!s.replaceAll("\\s", "").isEmpty()) {
                Notifications.modules.add(s);
            }
        }
    }
    
    @SubscribeEvent
    public void onReceivePacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && this.pearls.getValue()) {
            final SPacketSpawnObject packet = event.getPacket();
            final EntityPlayer player = Notifications.mc.world.getClosestPlayer(packet.getX(), packet.getY(), packet.getZ(), 1.0, false);
            if (player == null) {
                return;
            }
            if (packet.getEntityID() == 85) {
                Command.sendMessage("§cPearl thrown by " + player.getName() + " at X:" + (int)packet.getX() + " Y:" + (int)packet.getY() + " Z:" + (int)packet.getZ());
            }
        }
    }
    
    @SubscribeEvent
    public void onToggleModule(final ClientEvent event) {
        if (!this.moduleMessage.getValue()) {
            return;
        }
        if (event.getStage() == 0) {
            final Module module = (Module)event.getFeature();
            if (!module.equals(this) && (Notifications.modules.contains(module.getDisplayName()) || !this.list.getValue())) {
                /*for (final ChatLine line : Notifications.mc.ingameGUI.getChatGUI().drawnChatLines) {
                    if (line.getChatComponent().getUnformattedText().equalsIgnoreCase(Managers.getInstance().getRawCommandMessage() + " " + module.getDisplayName() + " disabled. ")) {
                        Notifications.mc.ingameGUI.getChatGUI().deleteChatLine(line.getChatLineID());
                    }
                }*/
                if (this.watermark.getValue()) {
                    Command.sendMessage("§c" + module.getDisplayName() + " disabled.");
                }
                else {
                    Command.sendSilentMessage("§c" + module.getDisplayName() + " disabled.");
                }
            }
        }
        if (event.getStage() == 1) {
            final Module module = (Module)event.getFeature();
            if (Notifications.modules.contains(module.getDisplayName()) || !this.list.getValue()) {
                if (this.watermark.getValue()) {
                    Command.sendMessage("§a" + module.getDisplayName() + " enabled.");
                }
                else {
                    Command.sendSilentMessage("§a" + module.getDisplayName() + " enabled.");
                }
            }
        }
    }
    
    public static Notifications getInstance() {
        if (Notifications.INSTANCE == null) {
            Notifications.INSTANCE = new Notifications();
        }
        return Notifications.INSTANCE;
    }
    
    public static void displayCrash(final Exception e) {
        Command.sendMessage("§cException caught: " + e.getMessage());
    }
    
    static {
        Notifications.modules = new ArrayList<String>();
        Notifications.INSTANCE = new Notifications();
    }
}
