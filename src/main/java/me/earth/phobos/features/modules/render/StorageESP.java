// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import net.minecraft.item.ItemShulkerBox;
import me.earth.phobos.util.ColorUtil;
import java.util.Iterator;
import me.earth.phobos.util.RenderUtil;
import java.awt.Color;
import me.earth.phobos.features.modules.client.Colors;
import java.util.Map;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.MathUtil;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class StorageESP extends Module
{
    private final Setting<Float> range;
    private final Setting<Boolean> colorSync;
    private final Setting<Boolean> chest;
    private final Setting<Boolean> dispenser;
    private final Setting<Boolean> shulker;
    private final Setting<Boolean> echest;
    private final Setting<Boolean> furnace;
    private final Setting<Boolean> hopper;
    private final Setting<Boolean> cart;
    private final Setting<Boolean> frame;
    private final Setting<Boolean> box;
    private final Setting<Integer> boxAlpha;
    private final Setting<Boolean> outline;
    private final Setting<Float> lineWidth;
    
    public StorageESP() {
        super("StorageESP", "Highlights Containers.", Category.RENDER, false, false, false);
        this.range = (Setting<Float>)this.register(new Setting("Range", 50.0f, 1.0f, 300.0f));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.chest = (Setting<Boolean>)this.register(new Setting("Chest", true));
        this.dispenser = (Setting<Boolean>)this.register(new Setting("Dispenser", false));
        this.shulker = (Setting<Boolean>)this.register(new Setting("Shulker", true));
        this.echest = (Setting<Boolean>)this.register(new Setting("Ender Chest", true));
        this.furnace = (Setting<Boolean>)this.register(new Setting("Furnace", false));
        this.hopper = (Setting<Boolean>)this.register(new Setting("Hopper", false));
        this.cart = (Setting<Boolean>)this.register(new Setting("Minecart", false));
        this.frame = (Setting<Boolean>)this.register(new Setting("Item Frame", false));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", false));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255, v -> this.box.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getValue()));
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        final Map<BlockPos, Integer> positions = new HashMap<BlockPos, Integer>();
        for (final TileEntity tileEntity : StorageESP.mc.world.loadedTileEntityList) {
            if ((tileEntity instanceof TileEntityChest && this.chest.getValue()) || (tileEntity instanceof TileEntityDispenser && this.dispenser.getValue()) || (tileEntity instanceof TileEntityShulkerBox && this.shulker.getValue()) || (tileEntity instanceof TileEntityEnderChest && this.echest.getValue()) || (tileEntity instanceof TileEntityFurnace && this.furnace.getValue()) || (tileEntity instanceof TileEntityHopper && this.hopper.getValue())) {
                final BlockPos pos = tileEntity.getPos();
                if (StorageESP.mc.player.getDistanceSq(pos) > MathUtil.square(this.range.getValue())) {
                    continue;
                }
                final int color = this.getTileEntityColor(tileEntity);
                if (color == -1) {
                    continue;
                }
                positions.put(pos, color);
            }
        }
        for (final Entity entity : StorageESP.mc.world.loadedEntityList) {
            if ((entity instanceof EntityItemFrame && this.frame.getValue()) || (entity instanceof EntityMinecartChest && this.cart.getValue())) {
                final BlockPos pos = entity.getPosition();
                if (StorageESP.mc.player.getDistanceSq(pos) > MathUtil.square(this.range.getValue())) {
                    continue;
                }
                final int color = this.getEntityColor(entity);
                if (color == -1) {
                    continue;
                }
                positions.put(pos, color);
            }
        }
        for (final Map.Entry<BlockPos, Integer> entry : positions.entrySet()) {
            final BlockPos blockPos = entry.getKey();
            final int color = entry.getValue();
            RenderUtil.drawBoxESP(blockPos, ((boolean)this.colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor() : new Color(color), false, new Color(color), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }
    
    private int getTileEntityColor(final TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            return ColorUtil.Colors.BLUE;
        }
        if (tileEntity instanceof TileEntityShulkerBox) {
            return ColorUtil.Colors.RED;
        }
        if (tileEntity instanceof TileEntityEnderChest) {
            return ColorUtil.Colors.PURPLE;
        }
        if (tileEntity instanceof TileEntityFurnace) {
            return ColorUtil.Colors.GRAY;
        }
        if (tileEntity instanceof TileEntityHopper) {
            return ColorUtil.Colors.DARK_RED;
        }
        if (tileEntity instanceof TileEntityDispenser) {
            return ColorUtil.Colors.ORANGE;
        }
        return -1;
    }
    
    private int getEntityColor(final Entity entity) {
        if (entity instanceof EntityMinecartChest) {
            return ColorUtil.Colors.ORANGE;
        }
        if (entity instanceof EntityItemFrame && ((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof ItemShulkerBox) {
            return ColorUtil.Colors.YELLOW;
        }
        if (entity instanceof EntityItemFrame && !(((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof ItemShulkerBox)) {
            return ColorUtil.Colors.ORANGE;
        }
        return -1;
    }
}
