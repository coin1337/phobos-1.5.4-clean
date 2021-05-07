// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.movement;

import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.network.play.client.CPacketPlayer;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class NoFall extends Module
{
    private Setting<Mode> mode;
    private Setting<Integer> distance;
    private Setting<Boolean> glide;
    private Setting<Boolean> silent;
    private Setting<Boolean> bypass;
    private Timer timer;
    private boolean equipped;
    private boolean gotElytra;
    private State currentState;
    private static Timer bypassTimer;
    private static int ogslot;
    
    public NoFall() {
        super("NoFall", "Prevents fall damage.", Category.MOVEMENT, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.PACKET));
        this.distance = (Setting<Integer>)this.register(new Setting("Distance", 15, 0, 50, v -> this.mode.getValue() == Mode.BUCKET));
        this.glide = (Setting<Boolean>)this.register(new Setting("Glide", false, v -> this.mode.getValue() == Mode.ELYTRA));
        this.silent = (Setting<Boolean>)this.register(new Setting("Silent", true, v -> this.mode.getValue() == Mode.ELYTRA));
        this.bypass = (Setting<Boolean>)this.register(new Setting("Bypass", false, v -> this.mode.getValue() == Mode.ELYTRA));
        this.timer = new Timer();
        this.equipped = false;
        this.gotElytra = false;
        this.currentState = State.FALL_CHECK;
    }
    
    @Override
    public void onEnable() {
        NoFall.ogslot = -1;
        this.currentState = State.FALL_CHECK;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.ELYTRA) {
            if (this.bypass.getValue()) {
                this.currentState = this.currentState.onSend(event);
            }
            else if (!this.equipped && event.getPacket() instanceof CPacketPlayer && NoFall.mc.player.fallDistance >= 3.0f) {
                RayTraceResult result = null;
                if (!this.glide.getValue()) {
                    result = NoFall.mc.world.rayTraceBlocks(NoFall.mc.player.getPositionVector(), NoFall.mc.player.getPositionVector().add(0.0, -3.0, 0.0), true, true, false);
                }
                if (this.glide.getValue() || (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)) {
                    if (NoFall.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
                        NoFall.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoFall.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    }
                    else if (this.silent.getValue()) {
                        final int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
                        if (slot != -1) {
                            NoFall.mc.playerController.windowClick(NoFall.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, (EntityPlayer)NoFall.mc.player);
                            NoFall.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoFall.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                        }
                        NoFall.ogslot = slot;
                        this.equipped = true;
                    }
                }
            }
        }
        if (this.mode.getValue() == Mode.PACKET && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = event.getPacket();
            //packet.onGround = true;
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if ((this.equipped || this.bypass.getValue()) && this.mode.getValue() == Mode.ELYTRA && (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot)) {
            if (this.bypass.getValue()) {
                this.currentState = this.currentState.onReceive(event);
            }
            else {
                this.gotElytra = true;
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.ELYTRA) {
            if (this.bypass.getValue()) {
                this.currentState = this.currentState.onUpdate();
            }
            else if (this.silent.getValue() && this.equipped && this.gotElytra) {
                NoFall.mc.playerController.windowClick(NoFall.mc.player.inventoryContainer.windowId, 6, NoFall.ogslot, ClickType.SWAP, (EntityPlayer)NoFall.mc.player);
                NoFall.mc.playerController.updateController();
                this.equipped = false;
                this.gotElytra = false;
            }
            else if (this.silent.getValue() && InventoryUtil.getItemHotbar(Items.ELYTRA) == -1) {
                final int slot = InventoryUtil.findStackInventory(Items.ELYTRA);
                if (slot != -1 && NoFall.ogslot != -1) {
                    System.out.println(String.format("Moving %d to hotbar %d", slot, NoFall.ogslot));
                    NoFall.mc.playerController.windowClick(NoFall.mc.player.inventoryContainer.windowId, slot, NoFall.ogslot, ClickType.SWAP, (EntityPlayer)NoFall.mc.player);
                    NoFall.mc.playerController.updateController();
                }
            }
        }
    }
    
    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.BUCKET && NoFall.mc.player.fallDistance >= this.distance.getValue() && !EntityUtil.isAboveWater((Entity)NoFall.mc.player) && this.timer.passedMs(100L)) {
            final Vec3d posVec = NoFall.mc.player.getPositionVector();
            final RayTraceResult result = NoFall.mc.world.rayTraceBlocks(posVec, posVec.add(0.0, -5.329999923706055, 0.0), true, true, false);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                EnumHand hand = EnumHand.MAIN_HAND;
                if (NoFall.mc.player.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) {
                    hand = EnumHand.OFF_HAND;
                }
                else if (NoFall.mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
                    for (int i = 0; i < 9; ++i) {
                        if (NoFall.mc.player.inventory.getStackInSlot(i).getItem() == Items.WATER_BUCKET) {
                            NoFall.mc.player.inventory.currentItem = i;
                            NoFall.mc.player.rotationPitch = 90.0f;
                            this.timer.reset();
                            return;
                        }
                    }
                    return;
                }
                NoFall.mc.player.rotationPitch = 90.0f;
                NoFall.mc.playerController.processRightClick((EntityPlayer)NoFall.mc.player, (World)NoFall.mc.world, hand);
                this.timer.reset();
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }
    
    static {
        NoFall.bypassTimer = new Timer();
        NoFall.ogslot = -1;
    }
    
    public enum Mode
    {
        PACKET, 
        BUCKET, 
        ELYTRA;
    }
    
    public enum State
    {
        FALL_CHECK {
            @Override
            public State onSend(final PacketEvent.Send event) {
                final RayTraceResult result = Util.mc.world.rayTraceBlocks(Util.mc.player.getPositionVector(), Util.mc.player.getPositionVector().add(0.0, -3.0, 0.0), true, true, false);
                if (!(event.getPacket() instanceof CPacketPlayer) || Util.mc.player.fallDistance < 3.0f || result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
                    return this;
                }
                final int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
                if (slot != -1) {
                    Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, (EntityPlayer)Util.mc.player);
                    NoFall.ogslot = slot;
                    Util.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Util.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    return NoFall.State.WAIT_FOR_ELYTRA_DEQUIP;
                }
                return this;
            }
        }, 
        WAIT_FOR_ELYTRA_DEQUIP {
            @Override
            public State onReceive(final PacketEvent.Receive event) {
                if (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot) {
                    return NoFall.State.REEQUIP_ELYTRA;
                }
                return this;
            }
        }, 
        REEQUIP_ELYTRA {
            @Override
            public State onUpdate() {
                Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, NoFall.ogslot, ClickType.SWAP, (EntityPlayer)Util.mc.player);
                Util.mc.playerController.updateController();
                final int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);
                if (slot == -1) {
                    Command.sendMessage("§cElytra not found after regain?");
                    return NoFall.State.WAIT_FOR_NEXT_REQUIP;
                }
                Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, slot, NoFall.ogslot, ClickType.SWAP, (EntityPlayer)Util.mc.player);
                Util.mc.playerController.updateController();
                NoFall.bypassTimer.reset();
                return NoFall.State.RESET_TIME;
            }
        }, 
        WAIT_FOR_NEXT_REQUIP {
            @Override
            public State onUpdate() {
                if (NoFall.bypassTimer.passedMs(250L)) {
                    return NoFall.State.REEQUIP_ELYTRA;
                }
                return this;
            }
        }, 
        RESET_TIME {
            @Override
            public State onUpdate() {
                if (Util.mc.player.onGround || NoFall.bypassTimer.passedMs(250L)) {
                    Util.mc.player.connection.sendPacket((Packet)new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), (short)1337));
                    return NoFall.State.FALL_CHECK;
                }
                return this;
            }
        };
        
        public State onSend(final PacketEvent.Send e) {
            return this;
        }
        
        public State onReceive(final PacketEvent.Receive e) {
            return this;
        }
        
        public State onUpdate() {
            return this;
        }
    }
}
