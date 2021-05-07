// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import java.util.Map;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import me.earth.phobos.util.RotationUtil;
import java.util.Comparator;
import net.minecraft.util.EnumFacing;
import java.util.HashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntity;
import java.util.ArrayList;
import me.earth.phobos.util.BlockUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import me.earth.phobos.features.Feature;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraft.util.math.BlockPos;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class BedBomb extends Module
{
    private final Setting<Boolean> place;
    private final Setting<Integer> placeDelay;
    private final Setting<Float> placeRange;
    private final Setting<Boolean> extraPacket;
    private final Setting<Boolean> packet;
    private final Setting<Boolean> explode;
    private final Setting<Integer> breakDelay;
    private final Setting<Float> breakRange;
    private final Setting<Float> minDamage;
    private final Setting<Float> range;
    private final Setting<Boolean> suicide;
    private final Setting<Boolean> removeTiles;
    private final Setting<Boolean> rotate;
    private final Setting<Logic> logic;
    private final Timer breakTimer;
    private final Timer placeTimer;
    private EntityPlayer target;
    private boolean sendRotationPacket;
    private final AtomicDouble yaw;
    private final AtomicDouble pitch;
    private final AtomicBoolean shouldRotate;
    private BlockPos maxPos;
    private int lastHotbarSlot;
    private int bedSlot;
    
    public BedBomb() {
        super("BedBomb", "AutoPlace and Break for beds", Category.COMBAT, true, false, false);
        this.place = (Setting<Boolean>)this.register(new Setting("Place", false));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("Placedelay", 50, 0, 500, v -> this.place.getValue()));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 6.0f, 1.0f, 10.0f, v -> this.place.getValue()));
        this.extraPacket = (Setting<Boolean>)this.register(new Setting("InsanePacket", false, v -> this.place.getValue()));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", false, v -> this.place.getValue()));
        this.explode = (Setting<Boolean>)this.register(new Setting("Break", true));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("Breakdelay", 50, 0, 500, v -> this.explode.getValue()));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", 6.0f, 1.0f, 10.0f, v -> this.explode.getValue()));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", 5.0f, 1.0f, 36.0f, v -> this.explode.getValue()));
        this.range = (Setting<Float>)this.register(new Setting("Range", 10.0f, 1.0f, 12.0f, v -> this.explode.getValue()));
        this.suicide = (Setting<Boolean>)this.register(new Setting("Suicide", false, v -> this.explode.getValue()));
        this.removeTiles = (Setting<Boolean>)this.register(new Setting("RemoveTiles", false));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", false));
        this.logic = (Setting<Logic>)this.register(new Setting("Logic", Logic.BREAKPLACE, v -> this.place.getValue() && this.explode.getValue()));
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
        this.target = null;
        this.sendRotationPacket = false;
        this.yaw = new AtomicDouble(-1.0);
        this.pitch = new AtomicDouble(-1.0);
        this.shouldRotate = new AtomicBoolean(false);
        this.maxPos = null;
        this.lastHotbarSlot = -1;
        this.bedSlot = -1;
    }
    
    @SubscribeEvent
    public void onPacket(final PacketEvent.Send event) {
        if (this.shouldRotate.get() && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = event.getPacket();
            /*packet.yaw = (float)this.yaw.get();
            packet.pitch = (float)this.pitch.get();*/
            this.shouldRotate.set(false);
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0 || Feature.fullNullCheck() || (BedBomb.mc.player.dimension != -1 && BedBomb.mc.player.dimension != 1)) {
            return;
        }
        this.doBedBomb();
    }
    
    private void doBedBomb() {
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.mapBeds();
                this.breakBeds();
                this.placeBeds();
                break;
            }
            case PLACEBREAK: {
                this.mapBeds();
                this.placeBeds();
                this.breakBeds();
                break;
            }
        }
    }
    
    private void breakBeds() {
        if (this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue()) && this.maxPos != null) {
            BedBomb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BedBomb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            BlockUtil.rightClickBlockLegit(this.maxPos, this.range.getValue(), this.rotate.getValue() && !this.place.getValue(), EnumHand.MAIN_HAND, this.yaw, this.pitch, this.shouldRotate, true);
            if (BedBomb.mc.player.isSneaking()) {
                BedBomb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            this.breakTimer.reset();
        }
    }
    
    private void mapBeds() {
        this.maxPos = null;
        float maxDamage = 0.5f;
        if (this.removeTiles.getValue()) {
            final List<BedData> removedBlocks = new ArrayList<BedData>();
            for (final TileEntity tile : BedBomb.mc.world.loadedTileEntityList) {
                if (tile instanceof TileEntityBed) {
                    final TileEntityBed bed = (TileEntityBed)tile;
                    final BedData data = new BedData(tile.getPos(), BedBomb.mc.world.getBlockState(tile.getPos()), bed, bed.isHeadPiece());
                    removedBlocks.add(data);
                }
            }
            for (final BedData data2 : removedBlocks) {
                BedBomb.mc.world.setBlockToAir(data2.getPos());
            }
            for (final BedData data2 : removedBlocks) {
                if (data2.isHeadPiece()) {
                    final BlockPos pos = data2.getPos();
                    if (BedBomb.mc.player.getDistanceSq(pos) > MathUtil.square(this.breakRange.getValue())) {
                        continue;
                    }
                    final float selfDamage = DamageUtil.calculateDamage(pos, (Entity)BedBomb.mc.player);
                    if (selfDamage + 1.0 >= EntityUtil.getHealth((Entity)BedBomb.mc.player) && DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        continue;
                    }
                    for (final EntityPlayer player : BedBomb.mc.world.playerEntities) {
                        if (player.getDistanceSq(pos) < MathUtil.square(this.range.getValue()) && EntityUtil.isValid((Entity)player, this.range.getValue() + this.breakRange.getValue())) {
                            final float damage = DamageUtil.calculateDamage(pos, (Entity)player);
                            if ((damage <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth((Entity)player)) || damage <= maxDamage) {
                                continue;
                            }
                            maxDamage = damage;
                            this.maxPos = pos;
                        }
                    }
                }
            }
            for (final BedData data2 : removedBlocks) {
                BedBomb.mc.world.setBlockState(data2.getPos(), data2.getState());
            }
        }
        else {
            for (final TileEntity tile2 : BedBomb.mc.world.loadedTileEntityList) {
                if (tile2 instanceof TileEntityBed) {
                    final TileEntityBed bed2 = (TileEntityBed)tile2;
                    if (!bed2.isHeadPiece()) {
                        continue;
                    }
                    final BlockPos pos = bed2.getPos();
                    if (BedBomb.mc.player.getDistanceSq(pos) > MathUtil.square(this.breakRange.getValue())) {
                        continue;
                    }
                    final float selfDamage = DamageUtil.calculateDamage(pos, (Entity)BedBomb.mc.player);
                    if (selfDamage + 1.0 >= EntityUtil.getHealth((Entity)BedBomb.mc.player) && DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        continue;
                    }
                    for (final EntityPlayer player : BedBomb.mc.world.playerEntities) {
                        if (player.getDistanceSq(pos) < MathUtil.square(this.range.getValue()) && EntityUtil.isValid((Entity)player, this.range.getValue() + this.breakRange.getValue())) {
                            final float damage = DamageUtil.calculateDamage(pos, (Entity)player);
                            if ((damage <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth((Entity)player)) || damage <= maxDamage) {
                                continue;
                            }
                            maxDamage = damage;
                            this.maxPos = pos;
                        }
                    }
                }
            }
        }
    }
    
    private void placeBeds() {
        if (this.place.getValue() && this.placeTimer.passedMs(this.placeDelay.getValue()) && this.maxPos == null) {
            this.bedSlot = this.findBedSlot();
            if (this.bedSlot == -1) {
                if (BedBomb.mc.player.getHeldItemOffhand().getItem() != Items.BED) {
                    return;
                }
                this.bedSlot = -2;
            }
            this.lastHotbarSlot = BedBomb.mc.player.inventory.currentItem;
            this.target = EntityUtil.getClosestEnemy(this.placeRange.getValue());
            if (this.target != null) {
                final BlockPos targetPos = new BlockPos(this.target.getPositionVector());
                this.placeBed(targetPos, true);
            }
        }
    }
    
    private void placeBed(final BlockPos pos, final boolean firstCheck) {
        if (BedBomb.mc.world.getBlockState(pos).getBlock() == Blocks.BED) {
            return;
        }
        final float damage = DamageUtil.calculateDamage(pos, (Entity)BedBomb.mc.player);
        if (damage > EntityUtil.getHealth((Entity)BedBomb.mc.player) + 0.5) {
            if (firstCheck) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        if (!BedBomb.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (firstCheck) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        final List<BlockPos> positions = new ArrayList<BlockPos>();
        final Map<BlockPos, EnumFacing> facings = new HashMap<BlockPos, EnumFacing>();
        for (final EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.DOWN) {
                if (facing != EnumFacing.UP) {
                    final BlockPos position = pos.offset(facing);
                    if (BedBomb.mc.player.getDistanceSq(position) <= MathUtil.square(this.placeRange.getValue()) && BedBomb.mc.world.getBlockState(position).getMaterial().isReplaceable() && !BedBomb.mc.world.getBlockState(position.down()).getMaterial().isReplaceable()) {
                        positions.add(position);
                        facings.put(position, facing.getOpposite());
                    }
                }
            }
        }
        if (positions.isEmpty()) {
            if (firstCheck) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        positions.sort(Comparator.comparingDouble(pos2 -> BedBomb.mc.player.getDistanceSq(pos2)));
        final BlockPos finalPos = positions.get(0);
        final EnumFacing finalFacing = facings.get(finalPos);
        final float[] rotation = RotationUtil.simpleFacing(finalFacing);
        if (!this.sendRotationPacket && this.extraPacket.getValue()) {
            RotationUtil.faceYawAndPitch(rotation[0], rotation[1]);
            this.sendRotationPacket = true;
        }
        this.yaw.set((double)rotation[0]);
        this.pitch.set((double)rotation[1]);
        this.shouldRotate.set(true);
        final Vec3d hitVec = new Vec3d((Vec3i)finalPos.down()).add(0.5, 0.5, 0.5).add(new Vec3d(finalFacing.getOpposite().getDirectionVec()).scale(0.5));
        BedBomb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        InventoryUtil.switchToHotbarSlot(this.bedSlot, false);
        BlockUtil.rightClickBlock(finalPos.down(), hitVec, (this.bedSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, this.packet.getValue());
        BedBomb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BedBomb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.placeTimer.reset();
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }
    
    @Override
    public void onToggle() {
        this.lastHotbarSlot = -1;
        this.bedSlot = -1;
        this.sendRotationPacket = false;
        this.target = null;
        this.yaw.set(-1.0);
        this.pitch.set(-1.0);
        this.shouldRotate.set(false);
    }
    
    private int findBedSlot() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = BedBomb.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() == Items.BED) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static class BedData
    {
        private final BlockPos pos;
        private final IBlockState state;
        private final boolean isHeadPiece;
        private final TileEntityBed entity;
        
        public BedData(final BlockPos pos, final IBlockState state, final TileEntityBed bed, final boolean isHeadPiece) {
            this.pos = pos;
            this.state = state;
            this.entity = bed;
            this.isHeadPiece = isHeadPiece;
        }
        
        public BlockPos getPos() {
            return this.pos;
        }
        
        public IBlockState getState() {
            return this.state;
        }
        
        public boolean isHeadPiece() {
            return this.isHeadPiece;
        }
        
        public TileEntityBed getEntity() {
            return this.entity;
        }
    }
    
    public enum Logic
    {
        BREAKPLACE, 
        PLACEBREAK;
    }
}
