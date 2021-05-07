// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.util.math.BlockPos;
import me.earth.phobos.features.Feature;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.MoveEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.util.internal.ConcurrentSet;
import java.util.Map;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.Set;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class TestPhase extends Module
{
    public Setting<Boolean> flight;
    public Setting<Integer> flightMode;
    public Setting<Boolean> doAntiFactor;
    public Setting<Double> antiFactor;
    public Setting<Double> extraFactor;
    public Setting<Boolean> strafeFactor;
    public Setting<Integer> loops;
    public Setting<Boolean> clearTeleMap;
    public Setting<Integer> mapTime;
    public Setting<Boolean> clearIDs;
    public Setting<Boolean> setYaw;
    public Setting<Boolean> setID;
    public Setting<Boolean> setMove;
    public Setting<Boolean> nocliperino;
    public Setting<Boolean> sendTeleport;
    public Setting<Boolean> resetID;
    public Setting<Boolean> setPos;
    public Setting<Boolean> invalidPacket;
    private final Set<CPacketPlayer> packets;
    private final Map<Integer, IDtime> teleportmap;
    private int flightCounter;
    private int teleportID;
    private static TestPhase instance;
    
    public TestPhase() {
        super("Packetfly", "Uses packets to fly!", Category.MOVEMENT, true, false, false);
        this.flight = (Setting<Boolean>)this.register(new Setting("Flight", true));
        this.flightMode = (Setting<Integer>)this.register(new Setting("FMode", 0, 0, 1));
        this.doAntiFactor = (Setting<Boolean>)this.register(new Setting("Factorize", true));
        this.antiFactor = (Setting<Double>)this.register(new Setting("AntiFactor", 2.5, 0.1, 3.0));
        this.extraFactor = (Setting<Double>)this.register(new Setting("ExtraFactor", 1.0, 0.1, 3.0));
        this.strafeFactor = (Setting<Boolean>)this.register(new Setting("StrafeFactor", true));
        this.loops = (Setting<Integer>)this.register(new Setting("Loops", 1, 1, 10));
        this.clearTeleMap = (Setting<Boolean>)this.register(new Setting("ClearMap", true));
        this.mapTime = (Setting<Integer>)this.register(new Setting("ClearTime", 30, 1, 500));
        this.clearIDs = (Setting<Boolean>)this.register(new Setting("ClearIDs", true));
        this.setYaw = (Setting<Boolean>)this.register(new Setting("SetYaw", true));
        this.setID = (Setting<Boolean>)this.register(new Setting("SetID", true));
        this.setMove = (Setting<Boolean>)this.register(new Setting("SetMove", false));
        this.nocliperino = (Setting<Boolean>)this.register(new Setting("NoClip", false));
        this.sendTeleport = (Setting<Boolean>)this.register(new Setting("Teleport", true));
        this.resetID = (Setting<Boolean>)this.register(new Setting("ResetID", true));
        this.setPos = (Setting<Boolean>)this.register(new Setting("SetPos", false));
        this.invalidPacket = (Setting<Boolean>)this.register(new Setting("InvalidPacket", true));
        this.packets = (Set<CPacketPlayer>)new ConcurrentSet();
        this.teleportmap = new ConcurrentHashMap<Integer, IDtime>();
        this.flightCounter = 0;
        this.teleportID = 0;
        TestPhase.instance = this;
    }
    
    public static TestPhase getInstance() {
        if (TestPhase.instance == null) {
            TestPhase.instance = new TestPhase();
        }
        return TestPhase.instance;
    }
    
    @Override
    public void onToggle() {
    }
    
    @Override
    public void onTick() {
        this.teleportmap.entrySet().removeIf(idTime -> this.clearTeleMap.getValue() && idTime.getValue().getTimer().passedS(this.mapTime.getValue()));
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        TestPhase.mc.player.setVelocity(0.0, 0.0, 0.0);
        double speed = 0.0;
        final boolean checkCollisionBoxes = this.checkHitBoxes();
        if (TestPhase.mc.player.movementInput.jump && (checkCollisionBoxes || !EntityUtil.isMoving())) {
            if (this.flight.getValue() && !checkCollisionBoxes) {
                if (this.flightMode.getValue() == 0) {
                    speed = (this.resetCounter(10) ? -0.032 : 0.062);
                }
                else {
                    speed = (this.resetCounter(20) ? -0.032 : 0.062);
                }
            }
            else {
                speed = 0.062;
            }
        }
        else if (TestPhase.mc.player.movementInput.sneak) {
            speed = -0.062;
        }
        else if (!checkCollisionBoxes) {
            speed = (this.resetCounter(4) ? (this.flight.getValue() ? -0.04 : 0.0) : 0.0);
        }
        else {
            speed = 0.0;
        }
        if (this.doAntiFactor.getValue() && checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0) {
            speed /= this.antiFactor.getValue();
        }
        final double[] strafing = this.getMotion((this.strafeFactor.getValue() && checkCollisionBoxes) ? 0.031 : 0.26);
        for (int i = 1; i < this.loops.getValue() + 1; ++i) {
            TestPhase.mc.player.motionX = strafing[0] * i * this.extraFactor.getValue();
            TestPhase.mc.player.motionY = speed * i;
            TestPhase.mc.player.motionZ = strafing[1] * i * this.extraFactor.getValue();
            this.sendPackets(TestPhase.mc.player.motionX, TestPhase.mc.player.motionY, TestPhase.mc.player.motionZ, this.sendTeleport.getValue());
        }
    }
    
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (this.setMove.getValue() && this.flightCounter != 0) {
            event.setX(TestPhase.mc.player.motionX);
            event.setY(TestPhase.mc.player.motionY);
            event.setZ(TestPhase.mc.player.motionZ);
            if (this.nocliperino.getValue() && this.checkHitBoxes()) {
                TestPhase.mc.player.noClip = true;
            }
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = event.getPacket();
            if (this.packets.contains(packet)) {
                this.packets.remove(packet);
            }
            else {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onPushOutOfBlocks(final PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !Feature.fullNullCheck()) {
            final SPacketPlayerPosLook packet = event.getPacket();
            if (TestPhase.mc.player.isEntityAlive()) {
                final BlockPos pos = new BlockPos(TestPhase.mc.player.posX, TestPhase.mc.player.posY, TestPhase.mc.player.posZ);
                if (TestPhase.mc.world.isBlockLoaded(pos, false) && !(TestPhase.mc.currentScreen instanceof GuiDownloadTerrain) && this.clearIDs.getValue()) {
                    this.teleportmap.remove(packet.getTeleportId());
                }
            }
            if (this.setYaw.getValue()) {
                /*packet.yaw = TestPhase.mc.player.rotationYaw;
                packet.pitch = TestPhase.mc.player.rotationPitch;*/
            }
            if (this.setID.getValue()) {
                this.teleportID = packet.getTeleportId();
            }
        }
    }
    
    private boolean checkHitBoxes() {
        return !TestPhase.mc.world.getCollisionBoxes((Entity)TestPhase.mc.player, TestPhase.mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }
    
    private boolean resetCounter(final int counter) {
        if (++this.flightCounter >= counter) {
            this.flightCounter = 0;
            return true;
        }
        return false;
    }
    
    private double[] getMotion(final double speed) {
        float moveForward = TestPhase.mc.player.movementInput.moveForward;
        float moveStrafe = TestPhase.mc.player.movementInput.moveStrafe;
        float rotationYaw = TestPhase.mc.player.prevRotationYaw + (TestPhase.mc.player.rotationYaw - TestPhase.mc.player.prevRotationYaw) * TestPhase.mc.getRenderPartialTicks();
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
    
    private void sendPackets(final double x, final double y, final double z, final boolean teleport) {
        final Vec3d vec = new Vec3d(x, y, z);
        final Vec3d position = TestPhase.mc.player.getPositionVector().add(vec);
        final Vec3d outOfBoundsVec = this.outOfBoundsVec(vec, position);
        this.packetSender((CPacketPlayer)new CPacketPlayer.Position(position.x, position.y, position.z, TestPhase.mc.player.onGround));
        if (this.invalidPacket.getValue()) {
            this.packetSender((CPacketPlayer)new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, TestPhase.mc.player.onGround));
        }
        if (this.setPos.getValue()) {
            TestPhase.mc.player.setPosition(position.x, position.y, position.z);
        }
        this.teleportPacket(position, teleport);
    }
    
    private void teleportPacket(final Vec3d pos, final boolean shouldTeleport) {
        if (shouldTeleport) {
            TestPhase.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(++this.teleportID));
            this.teleportmap.put(this.teleportID, new IDtime(pos, new Timer()));
        }
    }
    
    private Vec3d outOfBoundsVec(final Vec3d offset, final Vec3d position) {
        return position.add(0.0, 1337.0, 0.0);
    }
    
    private void packetSender(final CPacketPlayer packet) {
        this.packets.add(packet);
        TestPhase.mc.player.connection.sendPacket((Packet)packet);
    }
    
    private void clean() {
        this.teleportmap.clear();
        this.flightCounter = 0;
        if (this.resetID.getValue()) {
            this.teleportID = 0;
        }
        this.packets.clear();
    }
    
    public static class IDtime
    {
        private final Vec3d pos;
        private final Timer timer;
        
        public IDtime(final Vec3d pos, final Timer timer) {
            this.pos = pos;
            (this.timer = timer).reset();
        }
        
        public Vec3d getPos() {
            return this.pos;
        }
        
        public Timer getTimer() {
            return this.timer;
        }
    }
}
