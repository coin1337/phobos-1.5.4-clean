// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import java.util.Iterator;
import me.earth.phobos.util.RenderUtil;
import java.awt.Color;
import me.earth.phobos.event.events.Render3DEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import me.earth.phobos.features.modules.Module;

public class PortalESP extends Module
{
    private int cooldownTicks;
    private final ArrayList<BlockPos> blockPosArrayList;
    private final Setting<Integer> distance;
    private final Setting<Boolean> box;
    private final Setting<Integer> boxAlpha;
    private final Setting<Boolean> outline;
    private final Setting<Float> lineWidth;
    
    public PortalESP() {
        super("PortalESP", "Draws portals", Category.RENDER, true, false, false);
        this.blockPosArrayList = new ArrayList<BlockPos>();
        this.distance = (Setting<Integer>)this.register(new Setting("Distance", 60, 10, 100));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", false));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255, v -> this.box.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getValue()));
    }
    
    @SubscribeEvent
    public void onTickEvent(final TickEvent.ClientTickEvent event) {
        if (PortalESP.mc.world == null) {
            return;
        }
        if (this.cooldownTicks < 1) {
            this.blockPosArrayList.clear();
            this.compileDL();
            this.cooldownTicks = 80;
        }
        --this.cooldownTicks;
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (PortalESP.mc.world == null) {
            return;
        }
        for (final BlockPos pos : this.blockPosArrayList) {
            RenderUtil.drawBoxESP(pos, new Color(204, 0, 153, 255), false, new Color(204, 0, 153, 255), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }
    
    private void compileDL() {
        if (PortalESP.mc.world == null || PortalESP.mc.player == null) {
            return;
        }
        for (int x = (int)PortalESP.mc.player.posX - this.distance.getValue(); x <= (int)PortalESP.mc.player.posX + this.distance.getValue(); ++x) {
            for (int y = (int)PortalESP.mc.player.posZ - this.distance.getValue(); y <= (int)PortalESP.mc.player.posZ + this.distance.getValue(); ++y) {
                for (int z = (int)Math.max(PortalESP.mc.player.posY - this.distance.getValue(), 0.0); z <= Math.min(PortalESP.mc.player.posY + this.distance.getValue(), 255.0); ++z) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    final Block block = PortalESP.mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.PORTAL) {
                        this.blockPosArrayList.add(pos);
                    }
                }
            }
        }
    }
}
