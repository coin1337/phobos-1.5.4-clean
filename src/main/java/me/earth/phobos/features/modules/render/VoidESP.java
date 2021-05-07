// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import net.minecraft.init.Blocks;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import me.earth.phobos.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.util.EntityUtil;
import java.util.Iterator;
import me.earth.phobos.util.RenderUtil;
import java.awt.Color;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.Feature;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class VoidESP extends Module
{
    private final Setting<Float> radius;
    public Setting<Boolean> air;
    public Setting<Boolean> noEnd;
    private Setting<Integer> updates;
    private Setting<Integer> voidCap;
    public Setting<Boolean> box;
    public Setting<Boolean> outline;
    public Setting<Boolean> colorSync;
    public Setting<Double> height;
    private Setting<Integer> red;
    private Setting<Integer> green;
    private Setting<Integer> blue;
    private Setting<Integer> alpha;
    private Setting<Integer> boxAlpha;
    private Setting<Float> lineWidth;
    public Setting<Boolean> customOutline;
    private Setting<Integer> cRed;
    private Setting<Integer> cGreen;
    private Setting<Integer> cBlue;
    private Setting<Integer> cAlpha;
    private final Timer timer;
    private List<BlockPos> voidHoles;
    
    public VoidESP() {
        super("VoidEsp", "Esps the void", Category.RENDER, true, false, false);
        this.radius = (Setting<Float>)this.register(new Setting("Radius", 8.0f, 0.0f, 50.0f));
        this.air = (Setting<Boolean>)this.register(new Setting("OnlyAir", true));
        this.noEnd = (Setting<Boolean>)this.register(new Setting("NoEnd", true));
        this.updates = (Setting<Integer>)this.register(new Setting("Updates", 500, 0, 1000));
        this.voidCap = (Setting<Integer>)this.register(new Setting("VoidCap", 500, 0, 1000));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", true));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.height = (Setting<Double>)this.register(new Setting("Height", 0.0, (-2.0), 2.0));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 0, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 0, 0, 255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255, v -> this.box.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", false, v -> this.outline.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", 0, 0, 255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", 0, 0, 255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", 255, 0, 255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", 255, 0, 255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.timer = new Timer();
        this.voidHoles = new CopyOnWriteArrayList<BlockPos>();
    }
    
    @Override
    public void onToggle() {
        this.timer.reset();
    }
    
    @Override
    public void onLogin() {
        this.timer.reset();
    }
    
    @Override
    public void onTick() {
        if (!Feature.fullNullCheck() && (!this.noEnd.getValue() || VoidESP.mc.player.dimension != 1) && this.timer.passedMs(this.updates.getValue())) {
            this.voidHoles.clear();
            this.voidHoles = this.findVoidHoles();
            if (this.voidHoles.size() > this.voidCap.getValue()) {
                this.voidHoles.clear();
            }
            this.timer.reset();
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (Feature.fullNullCheck() || (this.noEnd.getValue() && VoidESP.mc.player.dimension == 1)) {
            return;
        }
        for (final BlockPos pos : this.voidHoles) {
            if (RotationUtil.isInFov(pos)) {
                RenderUtil.drawBoxESP(pos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue());
            }
        }
    }
    
    private List<BlockPos> findVoidHoles() {
        final BlockPos playerPos = EntityUtil.getPlayerPos((EntityPlayer)VoidESP.mc.player);
        return BlockUtil.getDisc(playerPos.add(0, -playerPos.getY(), 0), this.radius.getValue()).stream().filter(this::isVoid).collect(Collectors.toList());
    }
    
    private boolean isVoid(final BlockPos pos) {
        return (VoidESP.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || (!this.air.getValue() && VoidESP.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK)) && pos.getY() < 1 && pos.getY() >= 0;
    }
}
