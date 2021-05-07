// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.movement;

import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.Packet;
import me.earth.phobos.features.Feature;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.event.events.PushEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.MoveEvent;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.Set;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Phase extends Module
{
    public Setting<Mode> mode;
    public Setting<PacketFlyMode> type;
    public Setting<Integer> xMove;
    public Setting<Integer> yMove;
    public Setting<Boolean> extra;
    public Setting<Integer> offset;
    public Setting<Boolean> fallPacket;
    public Setting<Boolean> teleporter;
    public Setting<Boolean> boundingBox;
    public Setting<Integer> teleportConfirm;
    public Setting<Boolean> ultraPacket;
    public Setting<Boolean> updates;
    public Setting<Boolean> setOnMove;
    public Setting<Boolean> cliperino;
    public Setting<Boolean> scanPackets;
    public Setting<Boolean> resetConfirm;
    public Setting<Boolean> posLook;
    public Setting<Boolean> cancel;
    public Setting<Boolean> cancelType;
    public Setting<Boolean> onlyY;
    public Setting<Integer> cancelPacket;
    private static Phase INSTANCE;
    private Set<CPacketPlayer> packets;
    private boolean teleport;
    private int teleportIds;
    private int posLookPackets;
    
    public Phase() {
        super("Phase", "Makes you able to phase through blocks.", Category.MOVEMENT, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.PACKETFLY));
        this.type = (Setting<PacketFlyMode>)this.register(new Setting("Type", PacketFlyMode.SETBACK, v -> this.mode.getValue() == Mode.PACKETFLY));
        this.xMove = (Setting<Integer>)this.register(new Setting("HMove", 625, 1, 1000, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK, "XMovement speed."));
        this.yMove = (Setting<Integer>)this.register(new Setting("YMove", 625, 1, 1000, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK, "YMovement speed."));
        this.extra = (Setting<Boolean>)this.register(new Setting("ExtraPacket", true, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.offset = (Setting<Integer>)this.register(new Setting("Offset", 1337, (-1337), 1337, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && this.extra.getValue(), "Up speed."));
        this.fallPacket = (Setting<Boolean>)this.register(new Setting("FallPacket", true, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.teleporter = (Setting<Boolean>)this.register(new Setting("Teleport", true, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.boundingBox = (Setting<Boolean>)this.register(new Setting("BoundingBox", true, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.teleportConfirm = (Setting<Integer>)this.register(new Setting("Confirm", 2, 0, 4, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.ultraPacket = (Setting<Boolean>)this.register(new Setting("DoublePacket", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.updates = (Setting<Boolean>)this.register(new Setting("Update", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.setOnMove = (Setting<Boolean>)this.register(new Setting("SetMove", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.cliperino = (Setting<Boolean>)this.register(new Setting("NoClip", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && this.setOnMove.getValue()));
        this.scanPackets = (Setting<Boolean>)this.register(new Setting("ScanPackets", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.resetConfirm = (Setting<Boolean>)this.register(new Setting("Reset", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.posLook = (Setting<Boolean>)this.register(new Setting("PosLook", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK));
        this.cancel = (Setting<Boolean>)this.register(new Setting("Cancel", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && this.posLook.getValue()));
        this.cancelType = (Setting<Boolean>)this.register(new Setting("SetYaw", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && this.posLook.getValue() && this.cancel.getValue()));
        this.onlyY = (Setting<Boolean>)this.register(new Setting("OnlyY", false, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && this.posLook.getValue()));
        this.cancelPacket = (Setting<Integer>)this.register(new Setting("Packets", 20, 0, 20, v -> this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && this.posLook.getValue()));
        this.packets = (Set<CPacketPlayer>)new ConcurrentSet();
        this.teleport = true;
        this.teleportIds = 0;
        this.setInstance();
    }
    
    private void setInstance() {
        Phase.INSTANCE = this;
    }
    
    public static Phase getInstance() {
        if (Phase.INSTANCE == null) {
            Phase.INSTANCE = new Phase();
        }
        return Phase.INSTANCE;
    }
    
    @Override
    public void onDisable() {
        this.packets.clear();
        this.posLookPackets = 0;
        if (Phase.mc.player != null) {
            if (this.resetConfirm.getValue()) {
                this.teleportIds = 0;
            }
            Phase.mc.player.noClip = false;
        }
    }
    
    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }
    
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (this.setOnMove.getValue() && this.type.getValue() == PacketFlyMode.SETBACK && event.getStage() == 0 && !Phase.mc.isSingleplayer() && this.mode.getValue() == Mode.PACKETFLY) {
            event.setX(Phase.mc.player.motionX);
            event.setY(Phase.mc.player.motionY);
            event.setZ(Phase.mc.player.motionZ);
            if (this.cliperino.getValue()) {
                Phase.mc.player.noClip = true;
            }
        }
        if (this.type.getValue() == PacketFlyMode.NONE || event.getStage() != 0 || Phase.mc.isSingleplayer() || this.mode.getValue() != Mode.PACKETFLY) {
            return;
        }
        if (!this.boundingBox.getValue() && !this.updates.getValue()) {
            this.doPhase(event);
        }
    }
    
    @SubscribeEvent
    public void onPush(final PushEvent event) {
        if (event.getStage() == 1 && this.type.getValue() != PacketFlyMode.NONE) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onMove(final UpdateWalkingPlayerEvent event) {
        if (Feature.fullNullCheck() || event.getStage() != 0 || this.type.getValue() != PacketFlyMode.SETBACK || this.mode.getValue() != Mode.PACKETFLY) {
            return;
        }
        if (this.boundingBox.getValue()) {
            this.doBoundingBox();
        }
        else if (this.updates.getValue()) {
            this.doPhase(null);
        }
    }
    
    private void doPhase(final MoveEvent event) {
        if (this.type.getValue() == PacketFlyMode.SETBACK && !this.boundingBox.getValue()) {
            final double[] dirSpeed = this.getMotion(this.teleport ? (this.yMove.getValue() / 10000.0) : ((this.yMove.getValue() - 1) / 10000.0));
            final double posX = Phase.mc.player.posX + dirSpeed[0];
            final double posY = Phase.mc.player.posY + (Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? (this.yMove.getValue() / 10000.0) : ((this.yMove.getValue() - 1) / 10000.0)) : 1.0E-8) - (Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? (this.yMove.getValue() / 10000.0) : ((this.yMove.getValue() - 1) / 10000.0)) : 2.0E-8);
            final double posZ = Phase.mc.player.posZ + dirSpeed[1];
            final CPacketPlayer packetPlayer = (CPacketPlayer)new CPacketPlayer.PositionRotation(posX, posY, posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, false);
            this.packets.add(packetPlayer);
            Phase.mc.player.connection.sendPacket((Packet)packetPlayer);
            if (this.teleportConfirm.getValue() != 3) {
                Phase.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds - 1));
                ++this.teleportIds;
            }
            if (this.extra.getValue()) {
                final CPacketPlayer packet = (CPacketPlayer)new CPacketPlayer.PositionRotation(Phase.mc.player.posX, this.offset.getValue() + Phase.mc.player.posY, Phase.mc.player.posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, true);
                this.packets.add(packet);
                Phase.mc.player.connection.sendPacket((Packet)packet);
            }
            if (this.teleportConfirm.getValue() != 1) {
                Phase.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds + 1));
                ++this.teleportIds;
            }
            if (this.ultraPacket.getValue()) {
                final CPacketPlayer packet2 = (CPacketPlayer)new CPacketPlayer.PositionRotation(posX, posY, posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, false);
                this.packets.add(packet2);
                Phase.mc.player.connection.sendPacket((Packet)packet2);
            }
            if (this.teleportConfirm.getValue() == 4) {
                Phase.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds));
                ++this.teleportIds;
            }
            if (this.fallPacket.getValue()) {
                Phase.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Phase.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
            Phase.mc.player.setPosition(posX, posY, posZ);
            this.teleport = (!this.teleporter.getValue() || !this.teleport);
            if (event != null) {
                event.setX(0.0);
                event.setY(0.0);
                event.setX(0.0);
            }
            else {
                Phase.mc.player.motionX = 0.0;
                Phase.mc.player.motionY = 0.0;
                Phase.mc.player.motionZ = 0.0;
            }
        }
    }
    
    private void doBoundingBox() {
        final double[] dirSpeed = this.getMotion(this.teleport ? 0.02250000089406967 : 0.02239999920129776);
        Phase.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(Phase.mc.player.posX + dirSpeed[0], Phase.mc.player.posY + (Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 1.0E-8) - (Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 2.0E-8), Phase.mc.player.posZ + dirSpeed[1], Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, false));
        Phase.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(Phase.mc.player.posX, -1337.0, Phase.mc.player.posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, true));
        Phase.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Phase.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        Phase.mc.player.setPosition(Phase.mc.player.posX + dirSpeed[0], Phase.mc.player.posY + (Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 1.0E-8) - (Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 2.0E-8), Phase.mc.player.posZ + dirSpeed[1]);
        this.teleport = !this.teleport;
        final EntityPlayerSP player = Phase.mc.player;
        final EntityPlayerSP player2 = Phase.mc.player;
        final EntityPlayerSP player3 = Phase.mc.player;
        final double motionX = 0.0;
        player3.motionZ = motionX;
        player2.motionY = motionX;
        player.motionX = motionX;
        Phase.mc.player.noClip = this.teleport;
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (this.posLook.getValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet = event.getPacket();
            if (Phase.mc.player.isEntityAlive() && Phase.mc.world.isBlockLoaded(new BlockPos(Phase.mc.player.posX, Phase.mc.player.posY, Phase.mc.player.posZ)) && !(Phase.mc.currentScreen instanceof GuiDownloadTerrain)) {
                if (this.teleportIds <= 0) {
                    this.teleportIds = packet.getTeleportId();
                }
                if (this.cancel.getValue() && this.cancelType.getValue()) {
                    //packet.yaw = Phase.mc.player.rotationYaw;
                    //packet.pitch = Phase.mc.player.rotationPitch;
                    return;
                }
                if (this.cancel.getValue() && this.posLookPackets >= this.cancelPacket.getValue() && (!this.onlyY.getValue() || (!Phase.mc.gameSettings.keyBindForward.isKeyDown() && !Phase.mc.gameSettings.keyBindRight.isKeyDown() && !Phase.mc.gameSettings.keyBindLeft.isKeyDown() && !Phase.mc.gameSettings.keyBindBack.isKeyDown()))) {
                    this.posLookPackets = 0;
                    event.setCanceled(true);
                }
                ++this.posLookPackets;
            }
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Send event) {
        if (this.scanPackets.getValue() && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packetPlayer = event.getPacket();
            if (this.packets.contains(packetPlayer)) {
                this.packets.remove(packetPlayer);
            }
            else {
                event.setCanceled(true);
            }
        }
    }
    
    private double[] getMotion(final double speed) {
        float moveForward = Phase.mc.player.movementInput.moveForward;
        float moveStrafe = Phase.mc.player.movementInput.moveStrafe;
        float rotationYaw = Phase.mc.player.prevRotationYaw + (Phase.mc.player.rotationYaw - Phase.mc.player.prevRotationYaw) * Phase.mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        final double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[] { posX, posZ };
    }
    
    static {
        Phase.INSTANCE = new Phase();
    }
    
    public enum Mode
    {
        PACKETFLY;
    }
    
    public enum PacketFlyMode
    {
        NONE, 
        SETBACK;
    }
}
