// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import java.util.Random;
import me.earth.phobos.manager.FileManager;
import net.minecraft.world.World;
import net.minecraft.network.play.client.CPacketUseEntity;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.Phobos;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import java.util.Iterator;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.command.Command;
import java.util.ArrayList;
import java.util.HashMap;
import me.earth.phobos.util.Timer;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Map;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class AutoGG extends Module
{
    private final Setting<Boolean> greentext;
    private final Setting<Boolean> loadFiles;
    private final Setting<Integer> targetResetTimer;
    private final Setting<Integer> delay;
    public Map<EntityPlayer, Integer> targets;
    public List<String> messages;
    public EntityPlayer attackedPlayer;
    private static final String path = "phobos/autogg.txt";
    private Timer timer;
    private Timer cooldownTimer;
    private boolean cooldown;
    
    public AutoGG() {
        super("AutoGG", "Automatically GGs", Category.MISC, true, false, false);
        this.greentext = (Setting<Boolean>)this.register(new Setting("Greentext", false));
        this.loadFiles = (Setting<Boolean>)this.register(new Setting("LoadFiles", false));
        this.targetResetTimer = (Setting<Integer>)this.register(new Setting("Reset", 30, 0, 90));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 10, 0, 30));
        this.targets = new HashMap<EntityPlayer, Integer>();
        this.messages = new ArrayList<String>();
    }
    
    @Override
    public void onEnable() {
        this.timer = new Timer();
        this.cooldownTimer = new Timer();
    }
    
    @Override
    public void onTick() {
        if (this.loadFiles.getValue()) {
            this.loadMessages();
            Command.sendMessage("<AutoGG> Loaded messages.");
            this.loadFiles.setValue(false);
        }
        if (this.cooldownTimer.passedS(this.delay.getValue()) && this.cooldown) {
            this.cooldown = false;
            this.cooldownTimer.reset();
        }
        if (this.timer.passedS(this.targetResetTimer.getValue())) {
            this.attackedPlayer = null;
            this.timer.reset();
        }
        if (AutoCrystal.target != null) {
            this.targets.put(AutoCrystal.target, (int)(this.timer.getPassedTimeMs() / 1000L));
        }
        this.targets.replaceAll((p, v) -> Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L)));
        for (final EntityPlayer player : this.targets.keySet()) {
            if (this.targets.get(player) > this.targetResetTimer.getValue()) {
                this.targets.remove(player);
            }
        }
    }
    
    @SubscribeEvent
    public void onEntityDeath(final LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer && (this.targets.containsKey(event.getEntity()) || event.getEntity() == this.attackedPlayer) && !this.cooldown) {
            this.announceDeath((EntityPlayer)event.getEntity());
            this.cooldown = true;
        }
    }
    
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer && !Phobos.friendManager.isFriend(event.getEntityPlayer())) {
            this.attackedPlayer = (EntityPlayer)event.getTarget();
        }
    }
    
    @SubscribeEvent
    public void onSendAttackPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoGG.mc.world) instanceof EntityPlayer && !Phobos.friendManager.isFriend((EntityPlayer)packet.getEntityFromWorld((World)AutoGG.mc.world))) {
                this.attackedPlayer = (EntityPlayer)packet.getEntityFromWorld((World)AutoGG.mc.world);
            }
        }
    }
    
    public void loadMessages() {
        this.messages = FileManager.readTextFileAllLines("phobos/autogg.txt");
    }
    
    public String getRandomMessage() {
        final Random rand = new Random();
        return this.messages.get(rand.nextInt(this.messages.size() - 1));
    }
    
    public void announceDeath(final EntityPlayer target) {
        AutoGG.mc.player.connection.sendPacket((Packet)new CPacketChatMessage((this.greentext.getValue() ? ">" : "") + this.getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
    }
}
