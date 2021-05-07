// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.player.Freecam;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import me.earth.phobos.features.Feature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.MathUtil;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.Phobos;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class HoleFiller extends Module
{
    public Setting<Mode> mode;
    public Setting<PlaceMode> placeMode;
    public Setting<Bind> obbyBind;
    public Setting<Bind> webBind;
    private final Setting<Double> smartRange;
    private final Setting<Double> range;
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<Boolean> disable;
    private final Setting<Integer> disableTime;
    private final Setting<Boolean> offhand;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> onlySafe;
    private final Setting<Boolean> webSelf;
    private final Setting<Boolean> highWeb;
    private final Setting<Boolean> freecam;
    private final Setting<Boolean> midSafeHoles;
    private final Setting<Boolean> packet;
    private static HoleFiller INSTANCE;
    public Mode currentMode;
    private final Timer offTimer;
    private final Timer timer;
    private boolean accessedViaBind;
    private int targetSlot;
    private int blocksThisTick;
    private Offhand.Mode offhandMode;
    private Offhand.Mode2 offhandMode2;
    private final Map<BlockPos, Integer> retries;
    private final Timer retryTimer;
    private boolean isSneaking;
    private boolean hasOffhand;
    private boolean placeHighWeb;
    private int lastHotbarSlot;
    private boolean switchedItem;
    
    public HoleFiller() {
        super("HoleFiller", "Fills holes around you.", Category.COMBAT, true, false, true);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.OBSIDIAN));
        this.placeMode = (Setting<PlaceMode>)this.register(new Setting("PlaceMode", PlaceMode.ALL));
        this.obbyBind = (Setting<Bind>)this.register(new Setting("Obsidian", new Bind(-1)));
        this.webBind = (Setting<Bind>)this.register(new Setting("Webs", new Bind(-1)));
        this.smartRange = (Setting<Double>)this.register(new Setting("SmartRange", 6.0, 0.0, 10.0, v -> this.placeMode.getValue() == PlaceMode.SMART));
        this.range = (Setting<Double>)this.register(new Setting("PlaceRange", 6.0, 0.0, 10.0));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", 50, 0, 250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Block/Place", 8, 1, 20));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", false));
        this.disable = (Setting<Boolean>)this.register(new Setting("Disable", true));
        this.disableTime = (Setting<Integer>)this.register(new Setting("Ms/Disable", 200, 1, 250));
        this.offhand = (Setting<Boolean>)this.register(new Setting("OffHand", true));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
        this.onlySafe = (Setting<Boolean>)this.register(new Setting("OnlySafe", true, v -> this.offhand.getValue()));
        this.webSelf = (Setting<Boolean>)this.register(new Setting("SelfWeb", false));
        this.highWeb = (Setting<Boolean>)this.register(new Setting("HighWeb", false));
        this.freecam = (Setting<Boolean>)this.register(new Setting("Freecam", false));
        this.midSafeHoles = (Setting<Boolean>)this.register(new Setting("MidSafe", false));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", false));
        this.currentMode = Mode.OBSIDIAN;
        this.offTimer = new Timer();
        this.timer = new Timer();
        this.accessedViaBind = false;
        this.targetSlot = -1;
        this.blocksThisTick = 0;
        this.offhandMode = Offhand.Mode.CRYSTALS;
        this.offhandMode2 = Offhand.Mode2.CRYSTALS;
        this.retries = new HashMap<BlockPos, Integer>();
        this.retryTimer = new Timer();
        this.hasOffhand = false;
        this.placeHighWeb = false;
        this.lastHotbarSlot = -1;
        this.switchedItem = false;
        this.setInstance();
    }
    
    private void setInstance() {
        HoleFiller.INSTANCE = this;
    }
    
    public static HoleFiller getInstance() {
        if (HoleFiller.INSTANCE == null) {
            HoleFiller.INSTANCE = new HoleFiller();
        }
        return HoleFiller.INSTANCE;
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }
        this.lastHotbarSlot = HoleFiller.mc.player.inventory.currentItem;
        if (!this.accessedViaBind) {
            this.currentMode = this.mode.getValue();
        }
        final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
        this.offhandMode = module.mode;
        this.offhandMode2 = module.currentMode;
        if (this.offhand.getValue() && (EntityUtil.isSafe((Entity)HoleFiller.mc.player) || !this.onlySafe.getValue())) {
            if (module.type.getValue() == Offhand.Type.NEW) {
                if (this.currentMode == Mode.WEBS) {
                    module.setSwapToTotem(false);
                    module.setMode(Offhand.Mode.WEBS);
                }
                else {
                    module.setSwapToTotem(false);
                    module.setMode(Offhand.Mode.OBSIDIAN);
                }
            }
            else {
                if (this.currentMode == Mode.WEBS) {
                    module.setMode(Offhand.Mode2.WEBS);
                }
                else {
                    module.setMode(Offhand.Mode2.OBSIDIAN);
                }
                if (!module.didSwitchThisTick) {
                    module.doOffhand();
                }
            }
        }
        Phobos.holeManager.update();
        this.offTimer.reset();
    }
    
    @Override
    public void onTick() {
        if (this.isOn() && (this.blocksPerTick.getValue() != 1 || !this.rotate.getValue())) {
            this.doHoleFill();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (this.isOn() && event.getStage() == 0 && this.blocksPerTick.getValue() == 1 && this.rotate.getValue()) {
            this.doHoleFill();
        }
    }
    
    @Override
    public void onDisable() {
        if (this.offhand.getValue()) {
            Phobos.moduleManager.getModuleByClass(Offhand.class).setMode(this.offhandMode);
            Phobos.moduleManager.getModuleByClass(Offhand.class).setMode(this.offhandMode2);
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.retries.clear();
        this.accessedViaBind = false;
        this.hasOffhand = false;
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            if (this.obbyBind.getValue().getKey() == Keyboard.getEventKey()) {
                this.accessedViaBind = true;
                this.currentMode = Mode.OBSIDIAN;
                this.toggle();
            }
            if (this.webBind.getValue().getKey() == Keyboard.getEventKey()) {
                this.accessedViaBind = true;
                this.currentMode = Mode.WEBS;
                this.toggle();
            }
        }
    }
    
    private void doHoleFill() {
        if (this.check()) {
            return;
        }
        if (this.placeHighWeb) {
            final BlockPos pos = new BlockPos(HoleFiller.mc.player.posX, HoleFiller.mc.player.posY + 1.0, HoleFiller.mc.player.posZ);
            this.placeBlock(pos);
            this.placeHighWeb = false;
        }
        List<BlockPos> targets;
        if (this.midSafeHoles.getValue()) {
            synchronized (Phobos.holeManager.getMidSafety()) {
                targets = new ArrayList<BlockPos>(Phobos.holeManager.getMidSafety());
            }
        }
        else {
            synchronized (Phobos.holeManager.getHoles()) {
                targets = new ArrayList<BlockPos>(Phobos.holeManager.getHoles());
            }
        }
        for (final BlockPos position : targets) {
            if (HoleFiller.mc.player.getDistanceSq(position) > MathUtil.square(this.range.getValue())) {
                continue;
            }
            if (this.placeMode.getValue() == PlaceMode.SMART && !this.isPlayerInRange(position)) {
                continue;
            }
            if (position.equals((Object)new BlockPos(HoleFiller.mc.player.getPositionVector()))) {
                if (this.currentMode != Mode.WEBS) {
                    continue;
                }
                if (!this.webSelf.getValue()) {
                    continue;
                }
                if (this.highWeb.getValue()) {
                    this.placeHighWeb = true;
                }
            }
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
            if (placeability == 1 && (this.currentMode == Mode.WEBS || this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && BlockTweaks.getINSTANCE().noBlock.getValue())) && (this.currentMode == Mode.WEBS || this.retries.get(position) == null || this.retries.get(position) < 4)) {
                this.placeBlock(position);
                if (this.currentMode == Mode.WEBS) {
                    continue;
                }
                this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
            }
            else {
                if (placeability != 3) {
                    continue;
                }
                this.placeBlock(position);
            }
        }
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.blocksThisTick < this.blocksPerTick.getValue() && this.switchItem(false)) {
            final boolean smartRotate = this.blocksPerTick.getValue() == 1 && this.rotate.getValue();
            if (smartRotate) {
                this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking);
            }
            else {
                this.isSneaking = BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            }
            this.timer.reset();
            ++this.blocksThisTick;
        }
    }
    
    private boolean isPlayerInRange(final BlockPos pos) {
        for (final EntityPlayer player : HoleFiller.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, this.smartRange.getValue())) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    private boolean check() {
        if (Feature.fullNullCheck() || (this.disable.getValue() && this.offTimer.passedMs(this.disableTime.getValue()))) {
            this.disable();
            return true;
        }
        if (HoleFiller.mc.player.inventory.currentItem != this.lastHotbarSlot && HoleFiller.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock((Class)((this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class))) {
            this.lastHotbarSlot = HoleFiller.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        if (!this.freecam.getValue() && Phobos.moduleManager.isModuleEnabled(Freecam.class)) {
            return true;
        }
        this.blocksThisTick = 0;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        switch (this.currentMode) {
            case WEBS: {
                this.hasOffhand = InventoryUtil.isBlock(HoleFiller.mc.player.getHeldItemOffhand().getItem(), BlockWeb.class);
                this.targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
                break;
            }
            case OBSIDIAN: {
                this.hasOffhand = InventoryUtil.isBlock(HoleFiller.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
                this.targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                break;
            }
        }
        if (this.onlySafe.getValue() && !EntityUtil.isSafe((Entity)HoleFiller.mc.player)) {
            this.disable();
            return true;
        }
        return (!this.hasOffhand && this.targetSlot == -1 && (!this.offhand.getValue() || (!EntityUtil.isSafe((Entity)HoleFiller.mc.player) && this.onlySafe.getValue()))) || (this.offhand.getValue() && !this.hasOffhand) || !this.timer.passedMs(this.delay.getValue());
    }
    
    private boolean switchItem(final boolean back) {
        if (this.offhand.getValue()) {
            return true;
        }
        final boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), (Class)((this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class));
        this.switchedItem = value[0];
        return value[1];
    }
    
    static {
        HoleFiller.INSTANCE = new HoleFiller();
    }
    
    public enum Mode
    {
        WEBS, 
        OBSIDIAN;
    }
    
    public enum PlaceMode
    {
        SMART, 
        ALL;
    }
}
