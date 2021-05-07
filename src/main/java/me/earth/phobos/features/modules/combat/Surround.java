// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.util.EnumHand;
import me.earth.phobos.features.command.Command;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.util.BlockUtil;
import java.util.Iterator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.init.Blocks;
import me.earth.phobos.Phobos;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.util.math.Vec3d;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Surround extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> center;
    private final Setting<Boolean> helpingBlocks;
    private final Setting<Boolean> intelligent;
    private final Setting<Boolean> antiPedo;
    private final Setting<Integer> extender;
    private final Setting<Boolean> extendMove;
    private final Setting<MovementMode> movementMode;
    private final Setting<Double> speed;
    private final Setting<Integer> eventMode;
    private final Setting<Boolean> floor;
    private final Setting<Boolean> echests;
    private final Setting<Boolean> noGhost;
    private final Setting<Boolean> info;
    private final Setting<Integer> retryer;
    private final Timer timer;
    private final Timer retryTimer;
    private int isSafe;
    private BlockPos startPos;
    private boolean didPlace;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements;
    private final Set<Vec3d> extendingBlocks;
    private int extenders;
    public static boolean isPlacing;
    private int obbySlot;
    private boolean offHand;
    private final Map<BlockPos, Integer> retries;
    
    public Surround() {
        super("Surround", "Surrounds you with Obsidian", Category.COMBAT, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", 50, 0, 250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Block/Place", 8, 1, 20));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", false));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
        this.center = (Setting<Boolean>)this.register(new Setting("Center", false));
        this.helpingBlocks = (Setting<Boolean>)this.register(new Setting("HelpingBlocks", true));
        this.intelligent = (Setting<Boolean>)this.register(new Setting("Intelligent", false, v -> this.helpingBlocks.getValue()));
        this.antiPedo = (Setting<Boolean>)this.register(new Setting("NoPedo", false));
        this.extender = (Setting<Integer>)this.register(new Setting("Extend", 1, 1, 4));
        this.extendMove = (Setting<Boolean>)this.register(new Setting("MoveExtend", false, v -> this.extender.getValue() > 1));
        this.movementMode = (Setting<MovementMode>)this.register(new Setting("Movement", MovementMode.STATIC));
        this.speed = (Setting<Double>)this.register(new Setting("Speed", 10.0, 0.0, 30.0, v -> this.movementMode.getValue() == MovementMode.LIMIT || this.movementMode.getValue() == MovementMode.OFF, "Maximum Movement Speed"));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", 3, 1, 3));
        this.floor = (Setting<Boolean>)this.register(new Setting("Floor", false));
        this.echests = (Setting<Boolean>)this.register(new Setting("Echests", false));
        this.noGhost = (Setting<Boolean>)this.register(new Setting("Packet", false));
        this.info = (Setting<Boolean>)this.register(new Setting("Info", false));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", 4, 1, 15));
        this.timer = new Timer();
        this.retryTimer = new Timer();
        this.didPlace = false;
        this.placements = 0;
        this.extendingBlocks = new HashSet<Vec3d>();
        this.extenders = 1;
        this.obbySlot = -1;
        this.offHand = false;
        this.retries = new HashMap<BlockPos, Integer>();
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }
        this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)Surround.mc.player);
        if (this.center.getValue() && !Phobos.moduleManager.isModuleEnabled("Freecam")) {
            if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.WEB) {
                Phobos.positionManager.setPositionPacket(Surround.mc.player.posX, this.startPos.getY(), Surround.mc.player.posZ, true, true, true);
            }
            else {
                Phobos.positionManager.setPositionPacket(this.startPos.getX() + 0.5, this.startPos.getY(), this.startPos.getZ() + 0.5, true, true, true);
            }
        }
        this.retries.clear();
        this.retryTimer.reset();
    }
    
    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.doFeetPlace();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.doFeetPlace();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.doFeetPlace();
        }
    }
    
    @Override
    public void onDisable() {
        if (nullCheck()) {
            return;
        }
        Surround.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }
    
    @Override
    public String getDisplayInfo() {
        if (!this.info.getValue()) {
            return null;
        }
        switch (this.isSafe) {
            case 0: {
                return "§cUnsafe";
            }
            case 1: {
                return "§eSecure";
            }
            default: {
                return "§aSecure";
            }
        }
    }
    
    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        if (!EntityUtil.isSafe((Entity)Surround.mc.player, 0, this.floor.getValue())) {
            this.isSafe = 0;
            this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player, 0, this.floor.getValue()), this.helpingBlocks.getValue(), false, false);
        }
        else if (!EntityUtil.isSafe((Entity)Surround.mc.player, -1, false)) {
            this.isSafe = 1;
            if (this.antiPedo.getValue()) {
                this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player, -1, false), false, false, true);
            }
        }
        else {
            this.isSafe = 2;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < this.extender.getValue()) {
            final Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (final Vec3d vec3d : this.extendingBlocks) {
                array[i] = vec3d;
                ++i;
            }
            final int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, this.floor.getValue()), this.helpingBlocks.getValue(), false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        }
        else if (this.extendingBlocks.size() > 2 || this.extenders >= this.extender.getValue()) {
            this.extendingBlocks.clear();
        }
    }
    
    private Vec3d areClose(final Vec3d[] vec3ds) {
        int matches = 0;
        for (final Vec3d vec3d : vec3ds) {
            for (final Vec3d pos : EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player, 0, this.floor.getValue())) {
                if (vec3d.equals((Object)pos)) {
                    ++matches;
                }
            }
        }
        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }
    
    private boolean placeBlocks(final Vec3d pos, final Vec3d[] vec3ds, final boolean hasHelpingBlocks, final boolean isHelping, final boolean isExtending) {
        int helpings = 0;
        boolean gotHelp = true;
        for (final Vec3d vec3d : vec3ds) {
            gotHelp = true;
            ++helpings;
            if (isHelping && !this.intelligent.getValue() && helpings > 1) {
                return false;
            }
            final BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            Label_0456: {
                switch (BlockUtil.isPositionPlaceable(position, this.raytrace.getValue())) {
                    case 1: {
                        if ((this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && BlockTweaks.getINSTANCE().noBlock.getValue())) && (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue())) {
                            this.placeBlock(position);
                            this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                            this.retryTimer.reset();
                            break;
                        }
                        if ((this.extendMove.getValue() || Phobos.speedManager.getSpeedKpH() == 0.0) && !isExtending && this.extenders < this.extender.getValue()) {
                            this.placeBlocks(Surround.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(Surround.mc.player.getPositionVector().add(vec3d), 0, this.floor.getValue()), hasHelpingBlocks, false, true);
                            this.extendingBlocks.add(vec3d);
                            ++this.extenders;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (hasHelpingBlocks) {
                            gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                            break Label_0456;
                        }
                        break;
                    }
                    case 3: {
                        if (gotHelp) {
                            this.placeBlock(position);
                        }
                        if (isHelping) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean check() {
        if (nullCheck()) {
            return true;
        }
        this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        Surround.isPlacing = false;
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        final int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.isOff()) {
            return true;
        }
        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        this.switchItem(true);
        if (this.obbySlot == -1 && !this.offHand && (!this.echests.getValue() || echestSlot == -1)) {
            if (this.info.getValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "You are out of Obsidian.");
            }
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (Surround.mc.player.inventory.currentItem != this.lastHotbarSlot && Surround.mc.player.inventory.currentItem != this.obbySlot && Surround.mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        }
        switch (this.movementMode.getValue()) {
            case STATIC: {
                if (!this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)Surround.mc.player))) {
                    this.disable();
                    return true;
                }
            }
            case LIMIT: {
                if (Phobos.speedManager.getSpeedKpH() > this.speed.getValue()) {
                    return true;
                }
                break;
            }
            case OFF: {
                if (Phobos.speedManager.getSpeedKpH() > this.speed.getValue()) {
                    this.disable();
                    return true;
                }
                break;
            }
        }
        return Phobos.moduleManager.isModuleEnabled("Freecam") || !this.timer.passedMs(this.delay.getValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && Surround.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockObsidian.class));
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue() && this.switchItem(false)) {
            Surround.isPlacing = true;
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking);
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    private boolean switchItem(final boolean back) {
        if (this.offHand) {
            return true;
        }
        final boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), (Class)((this.obbySlot == -1) ? BlockEnderChest.class : BlockObsidian.class));
        this.switchedItem = value[0];
        return value[1];
    }
    
    static {
        Surround.isPlacing = false;
    }
    
    public enum MovementMode
    {
        NONE, 
        STATIC, 
        LIMIT, 
        OFF;
    }
}
