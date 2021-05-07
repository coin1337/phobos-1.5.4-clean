// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.combat;

import java.util.Iterator;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import org.lwjgl.input.Mouse;
import net.minecraft.item.ItemEndCrystal;
import me.earth.phobos.Phobos;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.item.ItemBow;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class BowSpam extends Module
{
    public Setting<Mode> mode;
    public Setting<Boolean> bowbomb;
    public Setting<Boolean> allowOffhand;
    public Setting<Integer> ticks;
    public Setting<Integer> delay;
    public Setting<Boolean> tpsSync;
    public Setting<Boolean> autoSwitch;
    public Setting<Boolean> onlyWhenSave;
    public Setting<Target> targetMode;
    public Setting<Float> range;
    public Setting<Float> health;
    public Setting<Float> ownHealth;
    private final Timer timer;
    private boolean offhand;
    private boolean switched;
    private int lastHotbarSlot;
    
    public BowSpam() {
        super("BowSpam", "Spams your bow", Category.COMBAT, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.FAST));
        this.bowbomb = (Setting<Boolean>)this.register(new Setting("BowBomb", false, v -> this.mode.getValue() != Mode.BOWBOMB));
        this.allowOffhand = (Setting<Boolean>)this.register(new Setting("Offhand", true, v -> this.mode.getValue() != Mode.AUTORELEASE));
        this.ticks = (Setting<Integer>)this.register(new Setting("Ticks", 3, 0, 20, v -> this.mode.getValue() == Mode.BOWBOMB || this.mode.getValue() == Mode.FAST, "Speed"));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 50, 0, 500, v -> this.mode.getValue() == Mode.AUTORELEASE, "Speed"));
        this.tpsSync = (Setting<Boolean>)this.register(new Setting("TpsSync", true));
        this.autoSwitch = (Setting<Boolean>)this.register(new Setting("AutoSwitch", false));
        this.onlyWhenSave = (Setting<Boolean>)this.register(new Setting("OnlyWhenSave", true, v -> this.autoSwitch.getValue()));
        this.targetMode = (Setting<Target>)this.register(new Setting("Target", Target.LOWEST, v -> this.autoSwitch.getValue()));
        this.range = (Setting<Float>)this.register(new Setting("Range", 3.0f, 0.0f, 6.0f, v -> this.autoSwitch.getValue(), "Range of the target"));
        this.health = (Setting<Float>)this.register(new Setting("Lethal", 6.0f, 0.1f, 36.0f, v -> this.autoSwitch.getValue(), "When should it switch?"));
        this.ownHealth = (Setting<Float>)this.register(new Setting("OwnHealth", 20.0f, 0.1f, 36.0f, v -> this.autoSwitch.getValue(), "Own Health."));
        this.timer = new Timer();
        this.offhand = false;
        this.switched = false;
        this.lastHotbarSlot = -1;
    }
    
    @Override
    public void onEnable() {
        this.lastHotbarSlot = BowSpam.mc.player.inventory.currentItem;
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.autoSwitch.getValue() && InventoryUtil.findHotbarBlock(ItemBow.class) != -1 && this.ownHealth.getValue() <= EntityUtil.getHealth((Entity)BowSpam.mc.player) && (!this.onlyWhenSave.getValue() || EntityUtil.isSafe((Entity)BowSpam.mc.player))) {
            final EntityPlayer target = this.getTarget();
            if (target != null) {
                final AutoCrystal crystal = Phobos.moduleManager.getModuleByClass(AutoCrystal.class);
                if (!crystal.isOn() || !InventoryUtil.holdingItem(ItemEndCrystal.class)) {
                    final Vec3d pos = target.getPositionVector();
                    final double xPos = pos.x;
                    double yPos = pos.y;
                    final double zPos = pos.z;
                    if (BowSpam.mc.player.canEntityBeSeen((Entity)target)) {
                        yPos += target.eyeHeight;
                    }
                    else {
                        if (!EntityUtil.canEntityFeetBeSeen((Entity)target)) {
                            return;
                        }
                        yPos += 0.1;
                    }
                    if (!(BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)) {
                        this.lastHotbarSlot = BowSpam.mc.player.inventory.currentItem;
                        InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
                        BowSpam.mc.gameSettings.keyBindUseItem.setKeyBindState(BowSpam.mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        this.switched = true;
                    }
                    Phobos.rotationManager.lookAtVec3d(xPos, yPos, zPos);
                    if (BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
                        this.switched = true;
                    }
                }
            }
        }
        else if (event.getStage() == 0 && this.switched && this.lastHotbarSlot != -1) {
            InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
            //BowSpam.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            BowSpam.mc.gameSettings.keyBindUseItem.setKeyBindState(BowSpam.mc.gameSettings.keyBindUseItem.getKeyCode(), Mouse.isButtonDown(1));
            this.switched = false;
        }
        else {
        	BowSpam.mc.gameSettings.keyBindUseItem.setKeyBindState(BowSpam.mc.gameSettings.keyBindUseItem.getKeyCode(), Mouse.isButtonDown(1));
            //BowSpam.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
        }
        if (this.mode.getValue() == Mode.FAST && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.isHandActive() && BowSpam.mc.player.getItemInUseMaxCount() >= this.ticks.getValue() * (this.tpsSync.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0f)) {
            BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
            BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            BowSpam.mc.player.stopActiveHand();
        }
    }
    
    @Override
    public void onUpdate() {
        this.offhand = (BowSpam.mc.player.getHeldItemOffhand().getItem() == Items.BOW && this.allowOffhand.getValue());
        switch (this.mode.getValue()) {
            case AUTORELEASE: {
                if ((this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && this.timer.passedMs((int)(this.delay.getValue() * (this.tpsSync.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0f)))) {
                    BowSpam.mc.playerController.onStoppedUsingItem((EntityPlayer)BowSpam.mc.player);
                    this.timer.reset();
                    break;
                }
                break;
            }
            case BOWBOMB: {
                if ((this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.isHandActive() && BowSpam.mc.player.getItemInUseMaxCount() >= this.ticks.getValue() * (this.tpsSync.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0f)) {
                    BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                    BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 0.0624, BowSpam.mc.player.posZ, BowSpam.mc.player.rotationYaw, BowSpam.mc.player.rotationPitch, false));
                    BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 999.0, BowSpam.mc.player.posZ, BowSpam.mc.player.rotationYaw, BowSpam.mc.player.rotationPitch, true));
                    BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                    BowSpam.mc.player.stopActiveHand();
                    break;
                }
                break;
            }
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.bowbomb.getValue() && this.mode.getValue() != Mode.BOWBOMB && event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging packet = event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.getItemInUseMaxCount() >= 20 && !BowSpam.mc.player.onGround) {
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 0.10000000149011612, BowSpam.mc.player.posZ, false));
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 10000.0, BowSpam.mc.player.posZ, true));
            }
        }
    }
    
    private EntityPlayer getTarget() {
        double maxHealth = 36.0;
        EntityPlayer target = null;
        for (final EntityPlayer player : BowSpam.mc.world.playerEntities) {
            if (player == null) {
                continue;
            }
            if (EntityUtil.isDead((Entity)player)) {
                continue;
            }
            if (EntityUtil.getHealth((Entity)player) > this.health.getValue()) {
                continue;
            }
            if (player.equals((Object)BowSpam.mc.player)) {
                continue;
            }
            if (Phobos.friendManager.isFriend(player)) {
                continue;
            }
            if (BowSpam.mc.player.getDistanceSq((Entity)player) > MathUtil.square(this.range.getValue())) {
                continue;
            }
            if (!BowSpam.mc.player.canEntityBeSeen((Entity)player) && !EntityUtil.canEntityFeetBeSeen((Entity)player)) {
                continue;
            }
            if (target == null) {
                target = player;
                maxHealth = EntityUtil.getHealth((Entity)player);
            }
            if (this.targetMode.getValue() == Target.CLOSEST && BowSpam.mc.player.getDistanceSq((Entity)player) < BowSpam.mc.player.getDistanceSq((Entity)target)) {
                target = player;
                maxHealth = EntityUtil.getHealth((Entity)player);
            }
            if (this.targetMode.getValue() != Target.LOWEST || EntityUtil.getHealth((Entity)player) >= maxHealth) {
                continue;
            }
            target = player;
            maxHealth = EntityUtil.getHealth((Entity)player);
        }
        return target;
    }
    
    public enum Target
    {
        CLOSEST, 
        LOWEST;
    }
    
    public enum Mode
    {
        FAST, 
        AUTORELEASE, 
        BOWBOMB;
    }
}
