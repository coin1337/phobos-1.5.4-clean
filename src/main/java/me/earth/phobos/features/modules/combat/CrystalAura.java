// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.init.Items;
import me.earth.phobos.util.BlockUtil;
import java.util.Iterator;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.DamageUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import me.earth.phobos.util.RenderUtil;
import java.awt.Color;
import me.earth.phobos.event.events.Render3DEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import me.earth.phobos.features.modules.misc.Tracker;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.client.CPacketPlayer;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.Timer;
import java.util.Map;
import net.minecraft.entity.Entity;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class CrystalAura extends Module implements Runnable
{
    private final Setting<Settings> setting;
    public Setting<Raytrace> raytrace;
    public Setting<Boolean> place;
    public Setting<Integer> placeDelay;
    public Setting<Float> placeRange;
    public Setting<Float> minDamage;
    public Setting<Integer> wasteAmount;
    public Setting<Boolean> wasteMinDmgCount;
    public Setting<Float> facePlace;
    public Setting<Float> placetrace;
    public Setting<Boolean> antiSurround;
    public Setting<Boolean> oneDot15;
    public Setting<Boolean> explode;
    public Setting<Switch> switchMode;
    public Setting<Integer> breakDelay;
    public Setting<Float> breakRange;
    public Setting<Integer> packets;
    public Setting<Float> breaktrace;
    public Setting<Boolean> manual;
    public Setting<Boolean> manualMinDmg;
    public Setting<Integer> manualBreak;
    public Setting<Boolean> render;
    public Setting<Boolean> box;
    public Setting<Boolean> outline;
    public Setting<Boolean> text;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> boxAlpha;
    private final Setting<Float> lineWidth;
    public Setting<Boolean> customOutline;
    private final Setting<Integer> cRed;
    private final Setting<Integer> cGreen;
    private final Setting<Integer> cBlue;
    private final Setting<Integer> cAlpha;
    public Setting<Float> range;
    public Setting<Target> targetMode;
    public Setting<Integer> minArmor;
    private final Setting<Integer> switchCooldown;
    public Setting<AutoSwitch> autoSwitch;
    public Setting<Bind> switchBind;
    public Setting<Boolean> offhandSwitch;
    public Setting<Boolean> switchBack;
    public Setting<Boolean> lethalSwitch;
    public Setting<Boolean> mineSwitch;
    public Setting<Rotate> rotate;
    public Setting<Logic> logic;
    public Setting<Boolean> doubleMap;
    public Setting<Boolean> suicide;
    public Setting<Boolean> webAttack;
    public Setting<Boolean> fullCalc;
    public Setting<Boolean> extraSelfCalc;
    public Setting<DamageSync> damageSync;
    public Setting<Integer> damageSyncTime;
    public Setting<Float> dropOff;
    public Setting<Integer> confirm;
    public Setting<Integer> threadDelay;
    public Setting<Integer> syncThreads;
    public Setting<ThreadMode> threadMode;
    private ScheduledExecutorService executor;
    private Queue<Entity> attackList;
    private Map<Entity, Float> crystalMap;
    private final Timer switchTimer;
    private final Timer manualTimer;
    private final Timer breakTimer;
    private final Timer placeTimer;
    private final Timer syncTimer;
    private final Timer syncroTimer;
    public static EntityPlayer target;
    private Entity efficientTarget;
    private double currentDamage;
    private double renderDamage;
    private double lastDamage;
    private boolean didRotation;
    private boolean switching;
    private BlockPos placePos;
    private BlockPos renderPos;
    private boolean mainHand;
    private boolean rotating;
    private boolean offHand;
    private int crystalCount;
    private int minDmgCount;
    private int lastSlot;
    private float yaw;
    private float pitch;
    private BlockPos webPos;
    private final Timer renderTimer;
    private BlockPos lastPos;
    public static ArrayList<BlockPos> placedPos;
    public static ArrayList<BlockPos> brokenPos;
    private boolean posConfirmed;
    private final AtomicBoolean shouldInterrupt;
    private Thread thread;
    
    public CrystalAura() {
        super("CrystalAura", "Beta-CA", Category.COMBAT, true, false, false);
        this.setting = (Setting<Settings>)this.register(new Setting("Settings", Settings.PLACE));
        this.raytrace = (Setting<Raytrace>)this.register(new Setting("Raytrace", Raytrace.NONE, v -> this.setting.getValue() == Settings.MISC));
        this.place = (Setting<Boolean>)this.register(new Setting("Place", true, v -> this.setting.getValue() == Settings.PLACE));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", 12, 0, 1000, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 6.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", 7.0f, 0.1f, 20.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteAmount = (Setting<Integer>)this.register(new Setting("WasteAmount", 1, 1, 5, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteMinDmgCount = (Setting<Boolean>)this.register(new Setting("CountMinDmg", true, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.facePlace = (Setting<Float>)this.register(new Setting("FacePlace", 8.0f, 0.1f, 20.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placetrace = (Setting<Float>)this.register(new Setting("Placetrace", 4.5f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK));
        this.antiSurround = (Setting<Boolean>)this.register(new Setting("AntiSurround", false, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.oneDot15 = (Setting<Boolean>)this.register(new Setting("1.15", false, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.explode = (Setting<Boolean>)this.register(new Setting("Break", true, v -> this.setting.getValue() == Settings.BREAK));
        this.switchMode = (Setting<Switch>)this.register(new Setting("Attack", Switch.BREAKSLOT, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", 25, 0, 1000, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", 6.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.packets = (Setting<Integer>)this.register(new Setting("Packets", 1, 1, 6, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breaktrace = (Setting<Float>)this.register(new Setting("Breaktrace", 4.5f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE));
        this.manual = (Setting<Boolean>)this.register(new Setting("Manual", false, v -> this.setting.getValue() == Settings.BREAK));
        this.manualMinDmg = (Setting<Boolean>)this.register(new Setting("ManMinDmg", false, v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.manualBreak = (Setting<Integer>)this.register(new Setting("ManualDelay", 500, 0, 1000, v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", true, v -> this.setting.getValue() == Settings.RENDER));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", true, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.text = (Setting<Boolean>)this.register(new Setting("Text", false, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.5f, 0.1f, 5.0f, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", false, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.range = (Setting<Float>)this.register(new Setting("Range", 12.0f, 0.1f, 20.0f, v -> this.setting.getValue() == Settings.MISC));
        this.targetMode = (Setting<Target>)this.register(new Setting("Target", Target.CLOSEST, v -> this.setting.getValue() == Settings.MISC));
        this.minArmor = (Setting<Integer>)this.register(new Setting("MinArmor", 0, 0, 125, v -> this.setting.getValue() == Settings.MISC));
        this.switchCooldown = (Setting<Integer>)this.register(new Setting("Cooldown", 500, 0, 1000, v -> this.setting.getValue() == Settings.MISC));
        this.autoSwitch = (Setting<AutoSwitch>)this.register(new Setting("Switch", AutoSwitch.TOGGLE, v -> this.setting.getValue() == Settings.MISC));
        this.switchBind = (Setting<Bind>)this.register(new Setting("SwitchBind", new Bind(-1), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE));
        this.offhandSwitch = (Setting<Boolean>)this.register(new Setting("Offhand", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.switchBack = (Setting<Boolean>)this.register(new Setting("Switchback", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.offhandSwitch.getValue()));
        this.lethalSwitch = (Setting<Boolean>)this.register(new Setting("LethalSwitch", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.mineSwitch = (Setting<Boolean>)this.register(new Setting("MineSwitch", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.rotate = (Setting<Rotate>)this.register(new Setting("Rotate", Rotate.OFF, v -> this.setting.getValue() == Settings.MISC));
        this.logic = (Setting<Logic>)this.register(new Setting("Logic", Logic.BREAKPLACE, v -> this.setting.getValue() == Settings.MISC));
        this.doubleMap = (Setting<Boolean>)this.register(new Setting("DoubleMap", false, v -> this.setting.getValue() == Settings.MISC && this.logic.getValue() == Logic.PLACEBREAK));
        this.suicide = (Setting<Boolean>)this.register(new Setting("Suicide", false, v -> this.setting.getValue() == Settings.MISC));
        this.webAttack = (Setting<Boolean>)this.register(new Setting("WebAttack", false, v -> this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE));
        this.fullCalc = (Setting<Boolean>)this.register(new Setting("ExtraCalc", false, v -> this.setting.getValue() == Settings.MISC));
        this.extraSelfCalc = (Setting<Boolean>)this.register(new Setting("MinSelfDmg", true, v -> this.setting.getValue() == Settings.MISC));
        this.damageSync = (Setting<DamageSync>)this.register(new Setting("DamageSync", DamageSync.NONE, v -> this.setting.getValue() == Settings.MISC));
        this.damageSyncTime = (Setting<Integer>)this.register(new Setting("SyncDelay", 500, 0, 1000, v -> this.setting.getValue() == Settings.MISC && this.damageSync.getValue() != DamageSync.NONE));
        this.dropOff = (Setting<Float>)this.register(new Setting("DropOff", 5.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.MISC && this.damageSync.getValue() == DamageSync.BREAK));
        this.confirm = (Setting<Integer>)this.register(new Setting("Confirm", 250, 0, 1000, v -> this.setting.getValue() == Settings.MISC && this.damageSync.getValue() != DamageSync.NONE));
        this.threadDelay = (Setting<Integer>)this.register(new Setting("ThreadDelay", 50, 1, 100, v -> this.setting.getValue() == Settings.DEV));
        this.syncThreads = (Setting<Integer>)this.register(new Setting("SyncThreads", 1000, 1, 10000, v -> this.setting.getValue() == Settings.DEV));
        this.threadMode = (Setting<ThreadMode>)this.register(new Setting("ThreadMode", ThreadMode.POOL, v -> this.setting.getValue() == Settings.DEV));
        this.attackList = new ConcurrentLinkedQueue<Entity>();
        this.crystalMap = new HashMap<Entity, Float>();
        this.switchTimer = new Timer();
        this.manualTimer = new Timer();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
        this.syncTimer = new Timer();
        this.syncroTimer = new Timer();
        this.efficientTarget = null;
        this.currentDamage = 0.0;
        this.renderDamage = 0.0;
        this.lastDamage = 0.0;
        this.didRotation = false;
        this.switching = false;
        this.placePos = null;
        this.renderPos = null;
        this.mainHand = false;
        this.rotating = false;
        this.offHand = false;
        this.crystalCount = 0;
        this.minDmgCount = 0;
        this.lastSlot = -1;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.webPos = null;
        this.renderTimer = new Timer();
        this.lastPos = null;
        this.posConfirmed = false;
        this.shouldInterrupt = new AtomicBoolean(false);
    }
    
    @Override
    public void onEnable() {
        this.thread = new Thread(this);
        if (this.threadMode.getValue() == ThreadMode.WHILE) {
            this.thread.start();
        }
        this.executor = this.getExecutor();
    }
    
    @Override
    public void onDisable() {
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.threadMode.getValue() == ThreadMode.POOL) {
            if (this.executor == null || this.executor.isTerminated() || this.syncroTimer.passedMs(this.syncThreads.getValue())) {
                if (this.executor != null) {
                    this.executor.shutdown();
                }
                this.executor = this.getExecutor();
                this.syncroTimer.reset();
            }
        }
        else if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs(this.syncThreads.getValue())) {
            if (this.thread == null) {
                this.thread = new Thread(this);
            }
            else if (this.syncroTimer.passedMs(this.syncThreads.getValue()) && !this.shouldInterrupt.get()) {
                this.shouldInterrupt.set(true);
                this.syncroTimer.reset();
                return;
            }
            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                this.thread = new Thread(this);
            }
            if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                try {
                    this.thread.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.syncroTimer.reset();
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "§aSwitch";
        }
        if (CrystalAura.target != null) {
            return CrystalAura.target.getName();
        }
        return null;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && event.getPacket() instanceof CPacketPlayer) {
            /*final CPacketPlayer packet = event.getPacket();
            packet.yaw = this.yaw;
            packet.pitch = this.pitch;*/
            this.rotating = false;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion) {
            final SPacketExplosion packet = event.getPacket();
            final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ()).down();
            if (this.damageSync.getValue() == DamageSync.PLACE) {
                if (CrystalAura.placedPos.contains(pos)) {
                    if (!Tracker.getInstance().isOn()) {
                        CrystalAura.placedPos.remove(pos);
                    }
                    this.posConfirmed = true;
                }
            }
            else if (this.damageSync.getValue() == DamageSync.BREAK && CrystalAura.brokenPos.contains(pos)) {
                CrystalAura.brokenPos.remove(pos);
                this.posConfirmed = true;
            }
        }
        else if (event.getPacket() instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packet2 = event.getPacket();
            for (final int id : packet2.getEntityIDs()) {
                final Entity entity = CrystalAura.mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    CrystalAura.brokenPos.remove(new BlockPos(entity.getPositionVector()).down());
                    if (!Tracker.getInstance().isOn()) {
                        CrystalAura.placedPos.remove(new BlockPos(entity.getPositionVector()).down());
                    }
                }
            }
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && this.render.getValue()) {
            if (!this.box.getValue() && !this.text.getValue()) {
                if (!this.outline.getValue()) {
                    return;
                }
            }
            try {
                RenderUtil.drawBoxESP(this.renderPos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                if (this.text.getValue()) {
                    RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(CrystalAura.mc.currentScreen instanceof PhobosGui) && this.switchBind.getValue().getKey() == Keyboard.getEventKey()) {
            if (this.switchBack.getValue() && this.offhandSwitch.getValue() && this.offHand) {
                final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
                if (module.isOff()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
                }
                else if (module.type.getValue() == Offhand.Type.NEW) {
                    module.setSwapToTotem(true);
                    module.doOffhand();
                }
                else {
                    module.setMode(Offhand.Mode2.TOTEMS);
                    module.doSwitch();
                }
                return;
            }
            this.switching = !this.switching;
        }
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.thread != null) {
                this.shouldInterrupt.set(true);
            }
        }
    }
    
    private ScheduledExecutorService getExecutor() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        if (this.threadMode.getValue() == ThreadMode.POOL) {
            service.scheduleAtFixedRate(this, 0L, this.threadDelay.getValue(), TimeUnit.MILLISECONDS);
        }
        return service;
    }
    
    @Override
    public void run() {
        if (this.threadMode.getValue() == ThreadMode.POOL) {
            try {
                this.doCrystalAura();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            while (this.isOn()) {
                if (this.shouldInterrupt.get()) {
                    this.shouldInterrupt.set(false);
                    this.syncroTimer.reset();
                    this.thread.interrupt();
                    break;
                }
                this.doCrystalAura();
                try {
                    Thread.sleep(this.threadDelay.getValue());
                }
                catch (InterruptedException e2) {
                    this.thread.interrupt();
                    e2.printStackTrace();
                }
            }
        }
    }
    
    private void doCrystalAura() {
        if (this.check()) {
            switch (this.logic.getValue()) {
                case PLACEBREAK: {
                    this.placeCrystal();
                    if (this.doubleMap.getValue()) {
                        this.mapCrystals();
                    }
                    this.breakCrystal();
                    break;
                }
                case BREAKPLACE: {
                    this.breakCrystal();
                    this.placeCrystal();
                    break;
                }
            }
            this.manualBreaker();
        }
    }
    
    private void mapCrystals() {
        this.efficientTarget = null;
        if (this.packets.getValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue<Entity>();
            this.crystalMap = new HashMap<Entity, Float>();
        }
        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5f;
        for (final Entity crystal : CrystalAura.mc.world.loadedEntityList) {
            if (crystal instanceof EntityEnderCrystal && this.isValid(crystal)) {
                boolean count = false;
                boolean countMin = false;
                final float selfDamage = DamageUtil.calculateDamage(crystal, (Entity)CrystalAura.mc.player);
                if (selfDamage + 0.5 < EntityUtil.getHealth((Entity)CrystalAura.mc.player) || !DamageUtil.canTakeDamage(this.suicide.getValue())) {
                    for (final EntityPlayer player : CrystalAura.mc.world.playerEntities) {
                        if (player.getDistanceSq(crystal) < MathUtil.square(this.range.getValue()) && EntityUtil.isValid((Entity)player, this.range.getValue() + this.breakRange.getValue())) {
                            final float damage = DamageUtil.calculateDamage(crystal, (Entity)player);
                            if (damage <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth((Entity)player)) {
                                continue;
                            }
                            if (damage > maxDamage) {
                                maxDamage = damage;
                                maxCrystal = crystal;
                            }
                            if (this.packets.getValue() == 1) {
                                if (damage >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                                    count = true;
                                }
                                countMin = true;
                            }
                            else {
                                if (this.crystalMap.get(crystal) != null && this.crystalMap.get(crystal) >= damage) {
                                    continue;
                                }
                                this.crystalMap.put(crystal, damage);
                            }
                        }
                    }
                }
                if (!countMin) {
                    continue;
                }
                ++this.minDmgCount;
                if (!count) {
                    continue;
                }
                ++this.crystalCount;
            }
        }
        if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
            this.lastDamage = maxDamage;
        }
        if (this.webAttack.getValue() && this.webPos != null) {
            if (CrystalAura.mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square(this.breakRange.getValue())) {
                this.webPos = null;
            }
            else {
                for (final Entity entity : CrystalAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.webPos.up()))) {
                    if (entity instanceof EntityEnderCrystal) {
                        this.attackList.add(entity);
                        this.efficientTarget = entity;
                        this.webPos = null;
                        this.lastDamage = 0.5;
                        return;
                    }
                }
            }
        }
        if (this.manual.getValue() && this.manualMinDmg.getValue() && CrystalAura.mc.gameSettings.keyBindUseItem.isKeyDown() && ((this.offHand && CrystalAura.mc.player.getActiveHand() == EnumHand.OFF_HAND) || (this.mainHand && CrystalAura.mc.player.getActiveHand() == EnumHand.MAIN_HAND)) && maxDamage < this.minDamage.getValue()) {
            this.efficientTarget = null;
            return;
        }
        if (this.packets.getValue() == 1) {
            this.efficientTarget = maxCrystal;
        }
        else {
            this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
            for (final Map.Entry<Entity, Float> entry : this.crystalMap.entrySet()) {
                final Entity crystal2 = entry.getKey();
                final float damage2 = entry.getValue();
                if (damage2 >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                    ++this.crystalCount;
                }
                this.attackList.add(crystal2);
                ++this.minDmgCount;
            }
        }
    }
    
    private void placeCrystal() {
        int crystalLimit = this.wasteAmount.getValue();
        if (this.placeTimer.passedMs(this.placeDelay.getValue()) && this.place.getValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
            if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!this.antiSurround.getValue() || this.lastPos == null || !this.lastPos.equals((Object)this.placePos))) {
                return;
            }
            this.calculateDamage(this.getTarget(this.targetMode.getValue() == Target.UNSAFE));
            if (CrystalAura.target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > this.minDamage.getValue() || (this.lethalSwitch.getValue() && EntityUtil.getHealth((Entity)CrystalAura.target) < this.facePlace.getValue())) && !this.switchItem()) {
                    return;
                }
                if (this.currentDamage < this.minDamage.getValue()) {
                    crystalLimit = 1;
                }
                if ((this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (this.antiSurround.getValue() && this.lastPos != null && this.lastPos.equals((Object)this.placePos))) && (this.currentDamage > this.minDamage.getValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0 && (DamageUtil.isArmorLow(CrystalAura.target, this.minArmor.getValue()) || EntityUtil.getHealth((Entity)CrystalAura.target) < this.facePlace.getValue() || this.currentDamage > this.minDamage.getValue())) {
                    final float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (this.dropOff.getValue() - 5.0f) : 0.0f;
                    if (this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE) {
                        if (this.damageSync.getValue() != DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }
                        this.renderPos = this.placePos;
                        this.renderDamage = this.currentDamage;
                        if (this.switchItem()) {
                            this.rotateToPos(this.placePos);
                            BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                            CrystalAura.placedPos.add(this.placePos);
                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs(this.damageSyncTime.getValue())) {
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            }
            else {
                this.renderPos = null;
            }
        }
    }
    
    private boolean switchItem() {
        if (this.offHand || this.mainHand) {
            return true;
        }
        switch (this.autoSwitch.getValue()) {
            case NONE: {
                return false;
            }
            case TOGGLE: {
                if (!this.switching) {
                    return false;
                }
            }
            case ALWAYS: {
                if (this.doSwitch()) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    private boolean doSwitch() {
        if (!this.offhandSwitch.getValue()) {
            if (CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                this.mainHand = false;
            }
            else {
                InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
                this.mainHand = true;
            }
            this.switching = false;
            return true;
        }
        final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
        if (module.isOff()) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
            return this.switching = false;
        }
        if (module.type.getValue() == Offhand.Type.NEW) {
            module.setSwapToTotem(false);
            module.setMode(Offhand.Mode.CRYSTALS);
            module.doOffhand();
        }
        else {
            module.setMode(Offhand.Mode2.CRYSTALS);
            module.doSwitch();
        }
        this.switching = false;
        return true;
    }
    
    private void calculateDamage(final EntityPlayer targettedPlayer) {
        if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !this.fullCalc.getValue()) {
            return;
        }
        float maxDamage = 0.5f;
        EntityPlayer currentTarget = null;
        BlockPos currentPos = null;
        float maxSelfDamage = 0.0f;
        BlockPos setToAir = null;
        IBlockState state = null;
        if (this.webAttack.getValue() && targettedPlayer != null) {
            final BlockPos playerPos = new BlockPos(targettedPlayer.getPositionVector());
            final Block web = CrystalAura.mc.world.getBlockState(playerPos).getBlock();
            if (web == Blocks.WEB) {
                setToAir = playerPos;
                state = CrystalAura.mc.world.getBlockState(playerPos);
                CrystalAura.mc.world.setBlockToAir(playerPos);
            }
        }
        for (final BlockPos pos : BlockUtil.possiblePlacePositions(this.placeRange.getValue(), this.antiSurround.getValue(), this.oneDot15.getValue())) {
            if (BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && CrystalAura.mc.player.getDistanceSq(pos) > MathUtil.square(this.placetrace.getValue()), 1.0f)) {
                float selfDamage = -1.0f;
                if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                    selfDamage = DamageUtil.calculateDamage(pos, (Entity)CrystalAura.mc.player);
                }
                if (selfDamage + 0.5 >= EntityUtil.getHealth((Entity)CrystalAura.mc.player)) {
                    continue;
                }
                if (targettedPlayer != null) {
                    final float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
                    if ((playerDamage <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage <= selfDamage && (playerDamage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage <= EntityUtil.getHealth((Entity)targettedPlayer))) {
                        continue;
                    }
                    maxDamage = playerDamage;
                    currentTarget = targettedPlayer;
                    currentPos = pos;
                    maxSelfDamage = selfDamage;
                }
                else {
                    for (final EntityPlayer player : CrystalAura.mc.world.playerEntities) {
                        if (EntityUtil.isValid((Entity)player, this.placeRange.getValue() + this.range.getValue())) {
                            final float playerDamage2 = DamageUtil.calculateDamage(pos, (Entity)player);
                            if ((playerDamage2 <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage2 < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage2 <= selfDamage && (playerDamage2 <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage2 <= EntityUtil.getHealth((Entity)player))) {
                                continue;
                            }
                            maxDamage = playerDamage2;
                            currentTarget = player;
                            currentPos = pos;
                            maxSelfDamage = selfDamage;
                        }
                    }
                }
            }
        }
        if (setToAir != null) {
            CrystalAura.mc.world.setBlockState(setToAir, state);
            this.webPos = currentPos;
        }
        CrystalAura.target = currentTarget;
        this.currentDamage = maxDamage;
        this.placePos = currentPos;
    }
    
    private EntityPlayer getTarget(final boolean unsafe) {
        if (this.targetMode.getValue() == Target.DAMAGE) {
            return null;
        }
        EntityPlayer currentTarget = null;
        for (final EntityPlayer player : CrystalAura.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, this.placeRange.getValue() + this.range.getValue())) {
                continue;
            }
            if (unsafe && EntityUtil.isSafe((Entity)player)) {
                continue;
            }
            if (this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player, this.minArmor.getValue())) {
                currentTarget = player;
                break;
            }
            if (currentTarget == null) {
                currentTarget = player;
            }
            else {
                if (CrystalAura.mc.player.getDistanceSq((Entity)player) >= CrystalAura.mc.player.getDistanceSq((Entity)currentTarget)) {
                    continue;
                }
                currentTarget = player;
            }
        }
        if (unsafe && currentTarget == null) {
            return this.getTarget(false);
        }
        return currentTarget;
    }
    
    private void breakCrystal() {
        if (this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (this.packets.getValue() == 1 && this.efficientTarget != null) {
                this.rotateTo(this.efficientTarget);
                EntityUtil.attackEntity(this.efficientTarget, true, true);
                CrystalAura.brokenPos.add(new BlockPos(this.efficientTarget.getPositionVector()).down());
            }
            else if (!this.attackList.isEmpty()) {
                for (int i = 0; i < this.packets.getValue(); ++i) {
                    final Entity entity = this.attackList.poll();
                    if (entity != null) {
                        this.rotateTo(entity);
                        EntityUtil.attackEntity(entity, true, true);
                        CrystalAura.brokenPos.add(new BlockPos(entity.getPositionVector()).down());
                    }
                }
            }
            this.breakTimer.reset();
        }
    }
    
    private void manualBreaker() {
        if (this.rotate.getValue() != Rotate.OFF && this.rotating) {
            if (this.didRotation) {
                final EntityPlayerSP player = CrystalAura.mc.player;
                player.rotationPitch += (float)4.0E-4;
                this.didRotation = false;
            }
            else {
                final EntityPlayerSP player2 = CrystalAura.mc.player;
                player2.rotationPitch -= (float)4.0E-4;
                this.didRotation = true;
            }
        }
        if ((this.offHand || this.mainHand) && this.manual.getValue() && this.manualTimer.passedMs(this.manualBreak.getValue()) && CrystalAura.mc.gameSettings.keyBindUseItem.isKeyDown() && CrystalAura.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && CrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && CrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && CrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE) {
            final RayTraceResult result = CrystalAura.mc.objectMouseOver;
            if (result != null) {
                switch (result.typeOfHit) {
                    case ENTITY: {
                        final Entity entity = result.entityHit;
                        if (entity instanceof EntityEnderCrystal) {
                            EntityUtil.attackEntity(entity, true, true);
                            this.manualTimer.reset();
                            break;
                        }
                        break;
                    }
                    case BLOCK: {
                        final BlockPos mousePos = new BlockPos((double)CrystalAura.mc.objectMouseOver.getBlockPos().getX(), CrystalAura.mc.objectMouseOver.getBlockPos().getY() + 1.0, (double)CrystalAura.mc.objectMouseOver.getBlockPos().getZ());
                        for (final Entity target : CrystalAura.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(mousePos))) {
                            if (target instanceof EntityEnderCrystal) {
                                EntityUtil.attackEntity(target, true, true);
                                this.manualTimer.reset();
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void rotateTo(final Entity entity) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case BREAK:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(CrystalAura.mc.player.getPositionEyes(CrystalAura.mc.getRenderPartialTicks()), entity.getPositionVector());
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }
    
    private void rotateToPos(final BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case PLACE:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(CrystalAura.mc.player.getPositionEyes(CrystalAura.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() - 0.5f), (double)(pos.getZ() + 0.5f)));
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }
    
    private boolean isValid(final Entity entity) {
        return entity != null && CrystalAura.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breakRange.getValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || CrystalAura.mc.player.canEntityBeSeen(entity) || (!CrystalAura.mc.player.canEntityBeSeen(entity) && CrystalAura.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breaktrace.getValue())));
    }
    
    private boolean check() {
        if (fullNullCheck()) {
            return false;
        }
        if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.mainHand = (CrystalAura.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        this.offHand = (CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != CrystalAura.mc.player.inventory.currentItem || AutoTrap.isPlacing || Surround.isPlacing) {
            this.lastSlot = CrystalAura.mc.player.inventory.currentItem;
            this.switchTimer.reset();
        }
        if (this.offHand || this.mainHand) {
            this.switching = false;
        }
        if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness((EntityPlayer)CrystalAura.mc.player) || !this.switchTimer.passedMs(this.switchCooldown.getValue())) {
            this.renderPos = null;
            CrystalAura.target = null;
            return this.rotating = false;
        }
        if (this.mineSwitch.getValue() && CrystalAura.mc.gameSettings.keyBindAttack.isKeyDown() && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && CrystalAura.mc.gameSettings.keyBindUseItem.isKeyDown() && CrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            this.switchItem();
        }
        this.mapCrystals();
        if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(this.confirm.getValue())) {
            this.syncTimer.setMs(this.damageSyncTime.getValue() + 1);
        }
        return true;
    }
    
    static {
        CrystalAura.target = null;
        CrystalAura.placedPos = new ArrayList<BlockPos>();
        CrystalAura.brokenPos = new ArrayList<BlockPos>();
    }
    
    public enum Settings
    {
        PLACE, 
        BREAK, 
        RENDER, 
        MISC, 
        DEV;
    }
    
    public enum DamageSync
    {
        NONE, 
        PLACE, 
        BREAK;
    }
    
    public enum Rotate
    {
        OFF, 
        PLACE, 
        BREAK, 
        ALL;
    }
    
    public enum Target
    {
        CLOSEST, 
        UNSAFE, 
        DAMAGE;
    }
    
    public enum Logic
    {
        BREAKPLACE, 
        PLACEBREAK;
    }
    
    public enum Switch
    {
        ALWAYS, 
        BREAKSLOT, 
        CALC;
    }
    
    public enum Raytrace
    {
        NONE, 
        PLACE, 
        BREAK, 
        FULL;
    }
    
    public enum AutoSwitch
    {
        NONE, 
        TOGGLE, 
        ALWAYS;
    }
    
    public enum ThreadMode
    {
        POOL, 
        WHILE;
    }
}
