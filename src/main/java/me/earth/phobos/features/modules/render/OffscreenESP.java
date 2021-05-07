// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import java.util.Iterator;
import me.earth.phobos.util.Util;
import com.google.common.collect.Maps;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.Display;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.util.math.MathHelper;
import me.earth.phobos.features.modules.client.Colors;
import java.awt.Color;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class OffscreenESP extends Module
{
    private final Setting<Boolean> colorSync;
    private final Setting<Boolean> invisibles;
    private final Setting<Boolean> offscreenOnly;
    private final Setting<Boolean> outline;
    private final Setting<Float> outlineWidth;
    private final Setting<Integer> fadeDistance;
    private final Setting<Integer> radius;
    private final Setting<Float> size;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final EntityListener entityListener;
    
    private static float partialTicks = 0.0F;
    
    public OffscreenESP() {
        super("ArrowESP", "Shows the direction players are in with cool little triangles :3", Category.RENDER, true, false, false);
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.invisibles = (Setting<Boolean>)this.register(new Setting("Invisibles", false));
        this.offscreenOnly = (Setting<Boolean>)this.register(new Setting("Offscreen-Only", true));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        this.outlineWidth = (Setting<Float>)this.register(new Setting("Outline-Width", 1.0f, 0.1f, 3.0f));
        this.fadeDistance = (Setting<Integer>)this.register(new Setting("Fade-Distance", 100, 10, 200));
        this.radius = (Setting<Integer>)this.register(new Setting("Radius", 45, 10, 200));
        this.size = (Setting<Float>)this.register(new Setting("Size", 10.0f, 5.0f, 25.0f));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 0, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255));
        this.entityListener = new EntityListener();
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
    	partialTicks = event.partialTicks;
        this.entityListener.render();
        OffscreenESP.mc.world.loadedEntityList.forEach(o -> {
            Vec3d pos;
            Color color2 = null;
            Color color;
            int x;
            int y;
            float yaw;
            if (o instanceof EntityPlayer && o != OffscreenESP.mc.player && (!o.isInvisible() || this.invisibles.getValue()) && o.isEntityAlive()) {
            	EntityPlayer entity = ( EntityPlayer )o;
            	pos = this.entityListener.getEntityLowerBounds().get(entity);
                if (pos != null && !this.isOnScreen(pos) && (!RenderUtil.isInViewFrustrum((Entity)entity) || !this.offscreenOnly.getValue())) {
                    if (this.colorSync.getValue()) {
                        // new(java.awt.Color.class)
                        new Color(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), (int)MathHelper.clamp(255.0f - 255.0f / this.fadeDistance.getValue() * OffscreenESP.mc.player.getDistance((Entity)entity), 100.0f, 255.0f));
                    }
                    else {
                        color2 = EntityUtil.getColor((Entity)entity, this.red.getValue(), this.green.getValue(), this.blue.getValue(), (int)MathHelper.clamp(255.0f - 255.0f / this.fadeDistance.getValue() * OffscreenESP.mc.player.getDistance((Entity)entity), 100.0f, 255.0f), true);
                    }
                    color = color2;
                    x = Display.getWidth() / 2 / ((OffscreenESP.mc.gameSettings.guiScale == 0) ? 1 : OffscreenESP.mc.gameSettings.guiScale);
                    y = Display.getHeight() / 2 / ((OffscreenESP.mc.gameSettings.guiScale == 0) ? 1 : OffscreenESP.mc.gameSettings.guiScale);
                    yaw = this.getRotations((EntityLivingBase)entity) - OffscreenESP.mc.player.rotationYaw;
                    GL11.glTranslatef((float)x, (float)y, 0.0f);
                    GL11.glRotatef(yaw, 0.0f, 0.0f, 1.0f);
                    GL11.glTranslatef((float)(-x), (float)(-y), 0.0f);
                    RenderUtil.drawTracerPointer((float)x, (float)(y - this.radius.getValue()), this.size.getValue(), 2.0f, 1.0f, this.outline.getValue(), this.outlineWidth.getValue(), color.getRGB());
                    GL11.glTranslatef((float)x, (float)y, 0.0f);
                    GL11.glRotatef(-yaw, 0.0f, 0.0f, 1.0f);
                    GL11.glTranslatef((float)(-x), (float)(-y), 0.0f);
                }
            }
        });
    }
    
    private boolean isOnScreen(final Vec3d pos) {
        return pos.x > -1.0 && pos.y < 1.0 && pos.x / ((OffscreenESP.mc.gameSettings.guiScale == 0) ? 1 : OffscreenESP.mc.gameSettings.guiScale) >= 0.0 && pos.x / ((OffscreenESP.mc.gameSettings.guiScale == 0) ? 1 : OffscreenESP.mc.gameSettings.guiScale) <= Display.getWidth() && pos.y / ((OffscreenESP.mc.gameSettings.guiScale == 0) ? 1 : OffscreenESP.mc.gameSettings.guiScale) >= 0.0 && pos.y / ((OffscreenESP.mc.gameSettings.guiScale == 0) ? 1 : OffscreenESP.mc.gameSettings.guiScale) <= Display.getHeight();
    }
    
    private float getRotations(final EntityLivingBase ent) {
        final double x = ent.posX - OffscreenESP.mc.player.posX;
        final double z = ent.posZ - OffscreenESP.mc.player.posZ;
        return (float)(-(Math.atan2(x, z) * 57.29577951308232));
    }
    
    private static class EntityListener
    {
        private final Map<Entity, Vec3d> entityUpperBounds;
        private final Map<Entity, Vec3d> entityLowerBounds;
        
        private EntityListener() {
            this.entityUpperBounds = Maps.newHashMap();
            this.entityLowerBounds = Maps.newHashMap();
        }
        
        private void render() {
            if (!this.entityUpperBounds.isEmpty()) {
                this.entityUpperBounds.clear();
            }
            if (!this.entityLowerBounds.isEmpty()) {
                this.entityLowerBounds.clear();
            }
            for (final Entity e : Util.mc.world.loadedEntityList) {
                final Vec3d bound = this.getEntityRenderPosition(e);
                bound.add(new Vec3d(0.0, e.height + 0.2, 0.0));
                final Vec3d upperBounds = RenderUtil.to2D(bound.x, bound.y, bound.z);
                final Vec3d lowerBounds = RenderUtil.to2D(bound.x, bound.y - 2.0, bound.z);
                if (upperBounds != null && lowerBounds != null) {
                    this.entityUpperBounds.put(e, upperBounds);
                    this.entityLowerBounds.put(e, lowerBounds);
                }
            }
        }
        
        private Vec3d getEntityRenderPosition(final Entity entity) {
            final double partial = partialTicks;
            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - Util.mc.getRenderManager().viewerPosX;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - Util.mc.getRenderManager().viewerPosY;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - Util.mc.getRenderManager().viewerPosZ;
            return new Vec3d(x, y, z);
        }
        
        public Map<Entity, Vec3d> getEntityLowerBounds() {
            return this.entityLowerBounds;
        }
    }
}
