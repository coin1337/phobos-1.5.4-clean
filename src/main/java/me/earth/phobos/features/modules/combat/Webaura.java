// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.util.EnumHand;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;

import java.util.Iterator;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.BlockUtil;
import java.util.Comparator;
import java.util.ArrayList;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Webaura extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerPlace;
    private final Setting<Double> targetRange;
    private final Setting<Double> range;
    private final Setting<TargetMode> targetMode;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<Double> speed;
    private final Setting<Boolean> upperBody;
    private final Setting<Boolean> lowerbody;
    private final Setting<Boolean> ylower;
    private final Setting<Boolean> antiSelf;
    private final Setting<Integer> eventMode;
    private final Setting<Boolean> freecam;
    private final Setting<Boolean> info;
    private final Setting<Boolean> disable;
    private final Setting<Boolean> packet;
    private final Timer timer;
    private boolean didPlace;
    private boolean switchedItem;
    public EntityPlayer target;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements;
    public static boolean isPlacing;
    private boolean smartRotate;
    private BlockPos startPos;
    
    public Webaura() {
        super("Webaura", "Traps other players in webs", Category.COMBAT, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", 50, 0, 250));
        this.blocksPerPlace = (Setting<Integer>)this.register(new Setting("Block/Place", 8, 1, 30));
        this.targetRange = (Setting<Double>)this.register(new Setting("TargetRange", 10.0, 0.0, 20.0));
        this.range = (Setting<Double>)this.register(new Setting("PlaceRange", 6.0, 0.0, 10.0));
        this.targetMode = (Setting<TargetMode>)this.register(new Setting("Target", TargetMode.CLOSEST));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", false));
        this.speed = (Setting<Double>)this.register(new Setting("Speed", 30.0, 0.0, 30.0));
        this.upperBody = (Setting<Boolean>)this.register(new Setting("Upper", false));
        this.lowerbody = (Setting<Boolean>)this.register(new Setting("Lower", true));
        this.ylower = (Setting<Boolean>)this.register(new Setting("Y-1", false));
        this.antiSelf = (Setting<Boolean>)this.register(new Setting("AntiSelf", false));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", 3, 1, 3));
        this.freecam = (Setting<Boolean>)this.register(new Setting("Freecam", false));
        this.info = (Setting<Boolean>)this.register(new Setting("Info", false));
        this.disable = (Setting<Boolean>)this.register(new Setting("TSelfMove", false));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", false));
        this.timer = new Timer();
        this.didPlace = false;
        this.placements = 0;
        this.smartRotate = false;
        this.startPos = null;
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)Webaura.mc.player);
        this.lastHotbarSlot = Webaura.mc.player.inventory.currentItem;
    }
    
    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.smartRotate = false;
            this.doTrap();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.smartRotate = (this.rotate.getValue() && this.blocksPerPlace.getValue() == 1);
            this.doTrap();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.smartRotate = false;
            this.doTrap();
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.info.getValue() && this.target != null) {
            return this.target.getName();
        }
        return null;
    }
    
    @Override
    public void onDisable() {
        Webaura.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }
    
    private void doTrap() {
        if (this.check()) {
            return;
        }
        this.doWebTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void doWebTrap() {
        final List<Vec3d> placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }
    
    private List<Vec3d> getPlacements() {
        final List<Vec3d> list = new ArrayList<Vec3d>();
        final Vec3d baseVec = this.target.getPositionVector();
        if (this.ylower.getValue()) {
            list.add(baseVec.add(0.0, -1.0, 0.0));
        }
        if (this.lowerbody.getValue()) {
            list.add(baseVec);
        }
        if (this.upperBody.getValue()) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }
    
    private void placeList(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(Webaura.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), Webaura.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
            if ((placeability == 3 || placeability == 1) && (!this.antiSelf.getValue() || !MathUtil.areVec3dsAligned(Webaura.mc.player.getPositionVector(), vec3d3))) {
                this.placeBlock(position);
            }
        }
    }
    
    private boolean check() {
        Webaura.isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        final int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (this.isOff()) {
            return true;
        }
        if (this.disable.getValue() && !this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)Webaura.mc.player))) {
            this.disable();
            return true;
        }
        if (obbySlot == -1) {
            if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
                if (this.info.getValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "You are out of Webs.");
                }
                this.disable();
            }
            return true;
        }
        if (Webaura.mc.player.inventory.currentItem != this.lastHotbarSlot && Webaura.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = Webaura.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(this.targetRange.getValue(), this.targetMode.getValue() == TargetMode.UNTRAPPED);
        return this.target == null || (Phobos.moduleManager.isModuleEnabled("Freecam") && !this.freecam.getValue()) || !this.timer.passedMs(this.delay.getValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && Webaura.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockWeb.class));
    }
    
    private EntityPlayer getTarget(final double range, final boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (final EntityPlayer player : Webaura.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, range)) {
                continue;
            }
            if (trapped && this.mc.world.isMaterialInBB(player.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.WEB)) {
                continue;
            }
            if (EntityUtil.getRoundedBlockPos((Entity)Webaura.mc.player).equals((Object)EntityUtil.getRoundedBlockPos((Entity)player)) && this.antiSelf.getValue()) {
                continue;
            }
            if (Phobos.speedManager.getPlayerSpeed(player) > this.speed.getValue()) {
                continue;
            }
            if (target == null) {
                target = player;
                distance = Webaura.mc.player.getDistanceSq((Entity)player);
            }
            else {
                if (Webaura.mc.player.getDistanceSq((Entity)player) >= distance) {
                    continue;
                }
                target = player;
                distance = Webaura.mc.player.getDistanceSq((Entity)player);
            }
        }
        return target;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && Webaura.mc.player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue()) && this.switchItem(false)) {
            Webaura.isPlacing = true;
            if (this.smartRotate) {
                this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking);
            }
            else {
                this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            }
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    private boolean switchItem(final boolean back) {
        final boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), BlockWeb.class);
        this.switchedItem = value[0];
        return value[1];
    }
    
    static {
        Webaura.isPlacing = false;
    }
    
    public enum TargetMode
    {
        CLOSEST, 
        UNTRAPPED;
    }
}
