// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.util.EnumHand;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import net.minecraft.block.BlockObsidian;
import java.util.Iterator;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.util.BlockUtil;
import java.util.Comparator;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class AutoTrap extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerPlace;
    private final Setting<Double> targetRange;
    private final Setting<Double> range;
    private final Setting<TargetMode> targetMode;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<Pattern> pattern;
    private final Setting<Integer> extend;
    private final Setting<Boolean> antiScaffold;
    private final Setting<Boolean> antiStep;
    private final Setting<Boolean> legs;
    private final Setting<Boolean> platform;
    private final Setting<Boolean> antiDrop;
    private final Setting<Double> speed;
    private final Setting<Boolean> antiSelf;
    private final Setting<Integer> eventMode;
    private final Setting<Boolean> freecam;
    private final Setting<Boolean> info;
    private final Setting<Boolean> entityCheck;
    private final Setting<Boolean> disable;
    private final Setting<Boolean> packet;
    private final Setting<Integer> retryer;
    private final Timer timer;
    private boolean didPlace;
    private boolean switchedItem;
    public EntityPlayer target;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements;
    public static boolean isPlacing;
    private boolean smartRotate;
    private final Map<BlockPos, Integer> retries;
    private final Timer retryTimer;
    private BlockPos startPos;
    
    public AutoTrap() {
        super("AutoTrap", "Traps other players", Category.COMBAT, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", 50, 0, 250));
        this.blocksPerPlace = (Setting<Integer>)this.register(new Setting("Block/Place", 8, 1, 30));
        this.targetRange = (Setting<Double>)this.register(new Setting("TargetRange", 10.0, 0.0, 20.0));
        this.range = (Setting<Double>)this.register(new Setting("PlaceRange", 6.0, 0.0, 10.0));
        this.targetMode = (Setting<TargetMode>)this.register(new Setting("Target", TargetMode.CLOSEST));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", false));
        this.pattern = (Setting<Pattern>)this.register(new Setting("Pattern", Pattern.STATIC));
        this.extend = (Setting<Integer>)this.register(new Setting("Extend", 4, 1, 4, v -> this.pattern.getValue() != Pattern.STATIC, "Extending the Trap."));
        this.antiScaffold = (Setting<Boolean>)this.register(new Setting("AntiScaffold", false));
        this.antiStep = (Setting<Boolean>)this.register(new Setting("AntiStep", false));
        this.legs = (Setting<Boolean>)this.register(new Setting("Legs", false, v -> this.pattern.getValue() != Pattern.OPEN));
        this.platform = (Setting<Boolean>)this.register(new Setting("Platform", false, v -> this.pattern.getValue() != Pattern.OPEN));
        this.antiDrop = (Setting<Boolean>)this.register(new Setting("AntiDrop", false));
        this.speed = (Setting<Double>)this.register(new Setting("Speed", 10.0, 0.0, 30.0));
        this.antiSelf = (Setting<Boolean>)this.register(new Setting("AntiSelf", false));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", 3, 1, 3));
        this.freecam = (Setting<Boolean>)this.register(new Setting("Freecam", false));
        this.info = (Setting<Boolean>)this.register(new Setting("Info", false));
        this.entityCheck = (Setting<Boolean>)this.register(new Setting("NoBlock", true));
        this.disable = (Setting<Boolean>)this.register(new Setting("TSelfMove", false));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", false));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", 4, 1, 15));
        this.timer = new Timer();
        this.didPlace = false;
        this.placements = 0;
        this.smartRotate = false;
        this.retries = new HashMap<BlockPos, Integer>();
        this.retryTimer = new Timer();
        this.startPos = null;
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)AutoTrap.mc.player);
        this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        this.retries.clear();
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
        AutoTrap.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }
    
    private void doTrap() {
        if (this.check()) {
            return;
        }
        switch (this.pattern.getValue()) {
            case STATIC: {
                this.doStaticTrap();
                break;
            }
            case SMART:
            case OPEN: {
                this.doSmartTrap();
                break;
            }
        }
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void doSmartTrap() {
        final List<Vec3d> placeTargets = EntityUtil.getUntrappedBlocksExtended(this.extend.getValue(), this.target, this.antiScaffold.getValue(), this.antiStep.getValue(), this.legs.getValue(), this.platform.getValue(), this.antiDrop.getValue(), this.raytrace.getValue());
        this.placeList(placeTargets);
    }
    
    private void doStaticTrap() {
        final List<Vec3d> placeTargets = EntityUtil.targets(this.target.getPositionVector(), this.antiScaffold.getValue(), this.antiStep.getValue(), this.legs.getValue(), this.platform.getValue(), this.antiDrop.getValue(), this.raytrace.getValue());
        this.placeList(placeTargets);
    }
    
    private void placeList(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(AutoTrap.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoTrap.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
            if (this.entityCheck.getValue() && placeability == 1 && (this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && BlockTweaks.getINSTANCE().noBlock.getValue())) && (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue())) {
                this.placeBlock(position);
                this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                this.retryTimer.reset();
            }
            else {
                if (placeability != 3 || (this.antiSelf.getValue() && MathUtil.areVec3dsAligned(AutoTrap.mc.player.getPositionVector(), vec3d3))) {
                    continue;
                }
                this.placeBlock(position);
            }
        }
    }
    
    private boolean check() {
        AutoTrap.isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        final int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.isOff()) {
            return true;
        }
        if (this.disable.getValue() && !this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)AutoTrap.mc.player))) {
            this.disable();
            return true;
        }
        if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (obbySlot == -1) {
            if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
                if (this.info.getValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "You are out of Obsidian.");
                }
                this.disable();
            }
            return true;
        }
        if (AutoTrap.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoTrap.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(this.targetRange.getValue(), this.targetMode.getValue() == TargetMode.UNTRAPPED);
        return this.target == null || (Phobos.moduleManager.isModuleEnabled("Freecam") && !this.freecam.getValue()) || !this.timer.passedMs(this.delay.getValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && AutoTrap.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockObsidian.class));
    }
    
    private EntityPlayer getTarget(final double range, final boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (final EntityPlayer player : AutoTrap.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, range)) {
                continue;
            }
            if (this.pattern.getValue() == Pattern.STATIC && trapped && EntityUtil.isTrapped(player, this.antiScaffold.getValue(), this.antiStep.getValue(), this.legs.getValue(), this.platform.getValue(), this.antiDrop.getValue())) {
                continue;
            }
            if (this.pattern.getValue() != Pattern.STATIC && trapped && EntityUtil.isTrappedExtended(this.extend.getValue(), player, this.antiScaffold.getValue(), this.antiStep.getValue(), this.legs.getValue(), this.platform.getValue(), this.antiDrop.getValue(), this.raytrace.getValue())) {
                continue;
            }
            if (EntityUtil.getRoundedBlockPos((Entity)AutoTrap.mc.player).equals((Object)EntityUtil.getRoundedBlockPos((Entity)player)) && this.antiSelf.getValue()) {
                continue;
            }
            if (Phobos.speedManager.getPlayerSpeed(player) > this.speed.getValue()) {
                continue;
            }
            if (target == null) {
                target = player;
                distance = AutoTrap.mc.player.getDistanceSq((Entity)player);
            }
            else {
                if (AutoTrap.mc.player.getDistanceSq((Entity)player) >= distance) {
                    continue;
                }
                target = player;
                distance = AutoTrap.mc.player.getDistanceSq((Entity)player);
            }
        }
        return target;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && AutoTrap.mc.player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue()) && this.switchItem(false)) {
            AutoTrap.isPlacing = true;
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
        final boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), BlockObsidian.class);
        this.switchedItem = value[0];
        return value[1];
    }
    
    static {
        AutoTrap.isPlacing = false;
    }
    
    public enum Pattern
    {
        STATIC, 
        SMART, 
        OPEN;
    }
    
    public enum TargetMode
    {
        CLOSEST, 
        UNTRAPPED;
    }
}
