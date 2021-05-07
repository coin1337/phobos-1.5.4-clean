// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import java.util.Comparator;
import me.earth.phobos.util.BlockUtil;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Crasher extends Module
{
    private final Setting<Mode> mode;
    private final Setting<Boolean> oneDot15;
    private final Setting<Float> placeRange;
    private final Setting<Integer> crystals;
    private final Setting<Integer> coolDown;
    private final Setting<InventoryUtil.Switch> switchMode;
    public Setting<Integer> sort;
    private boolean offhand;
    private boolean mainhand;
    private Timer timer;
    private int lastHotbarSlot;
    private boolean switchedItem;
    private boolean chinese;
    
    public Crasher() {
        super("CrystalCrash", "Attempts to crash chinese AutoCrystals", Category.COMBAT, false, false, true);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.ONCE));
        this.oneDot15 = (Setting<Boolean>)this.register(new Setting("1.15", false));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 6.0f, 0.0f, 10.0f));
        this.crystals = (Setting<Integer>)this.register(new Setting("Positions", 100, 0, 1000));
        this.coolDown = (Setting<Integer>)this.register(new Setting("CoolDown", 400, 0, 1000));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
        this.sort = (Setting<Integer>)this.register(new Setting("Sort", 0, 0, 2));
        this.offhand = false;
        this.mainhand = false;
        this.timer = new Timer();
        this.lastHotbarSlot = -1;
        this.switchedItem = false;
        this.chinese = false;
    }
    
    @Override
    public void onEnable() {
        this.chinese = false;
        if (Feature.fullNullCheck() || !this.timer.passedMs(this.coolDown.getValue())) {
            this.disable();
            return;
        }
        this.lastHotbarSlot = Crasher.mc.player.inventory.currentItem;
        if (this.mode.getValue() == Mode.ONCE) {
            this.placeCrystals();
            this.disable();
        }
    }
    
    @Override
    public void onDisable() {
        this.timer.reset();
        if (this.mode.getValue() == Mode.SPAM) {
            this.switchItem(true);
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (Feature.fullNullCheck() || event.phase == TickEvent.Phase.START || (this.isOff() && (this.timer.passedMs(10L) || this.mode.getValue() == Mode.SPAM))) {
            return;
        }
        if (this.mode.getValue() == Mode.SPAM) {
            this.placeCrystals();
        }
        else {
            this.switchItem(true);
        }
    }
    
    private void placeCrystals() {
        this.offhand = (Crasher.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.mainhand = (Crasher.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        int crystalcount = 0;
        final List<BlockPos> blocks = BlockUtil.possiblePlacePositions(this.placeRange.getValue(), false, this.oneDot15.getValue());
        if (this.sort.getValue() == 1) {
            blocks.sort(Comparator.comparingDouble(hole -> Crasher.mc.player.getDistanceSq(hole)));
        }
        else if (this.sort.getValue() == 2) {
            blocks.sort(Comparator.comparingDouble(hole -> -Crasher.mc.player.getDistanceSq(hole)));
        }
        for (final BlockPos pos : blocks) {
            if (this.isOff()) {
                break;
            }
            if (crystalcount >= this.crystals.getValue()) {
                break;
            }
            this.placeCrystal(pos);
            ++crystalcount;
        }
    }
    
    private void placeCrystal(final BlockPos pos) {
        if (!this.chinese && !this.mainhand && !this.offhand && !this.switchItem(false)) {
            this.disable();
            return;
        }
        final RayTraceResult result = Crasher.mc.world.rayTraceBlocks(new Vec3d(Crasher.mc.player.posX, Crasher.mc.player.posY + Crasher.mc.player.getEyeHeight(), Crasher.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        Crasher.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        Crasher.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
    
    private boolean switchItem(final boolean back) {
        this.chinese = true;
        if (this.offhand) {
            return true;
        }
        final boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), Items.END_CRYSTAL);
        this.switchedItem = value[0];
        return value[1];
    }
    
    public enum Mode
    {
        ONCE, 
        SPAM;
    }
}
