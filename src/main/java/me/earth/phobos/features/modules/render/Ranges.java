// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import java.util.Iterator;
import net.minecraft.util.math.RayTraceResult;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderManager;
import me.earth.phobos.Phobos;
import net.minecraft.entity.Entity;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.awt.Color;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Ranges extends Module
{
    private final Setting<Boolean> hitSpheres;
    private final Setting<Boolean> circle;
    private final Setting<Boolean> ownSphere;
    private final Setting<Boolean> raytrace;
    private final Setting<Float> lineWidth;
    private final Setting<Double> radius;
    
    public Ranges() {
        super("Ranges", "Draws a circle around the player.", Category.RENDER, false, false, false);
        this.hitSpheres = (Setting<Boolean>)this.register(new Setting("HitSpheres", false));
        this.circle = (Setting<Boolean>)this.register(new Setting("Circle", true));
        this.ownSphere = (Setting<Boolean>)this.register(new Setting("OwnSphere", false, v -> this.hitSpheres.getValue()));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("RayTrace", false, v -> this.circle.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.5f, 0.1f, 5.0f));
        this.radius = (Setting<Double>)this.register(new Setting("Radius", 4.5, 0.1, 8.0));
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.circle.getValue()) {
            GlStateManager.pushMatrix();
            RenderUtil.GLPre(this.lineWidth.getValue());
            GlStateManager.enableBlend();
            GlStateManager.glLineWidth(3.0f);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            final RenderManager renderManager = Ranges.mc.getRenderManager();
            final Color color = Color.RED;
            final List<Vec3d> hVectors = new ArrayList<Vec3d>();
            final double x = Ranges.mc.player.lastTickPosX + (Ranges.mc.player.posX - Ranges.mc.player.lastTickPosX) * event.getPartialTicks() - renderManager.viewerPosX;
            final double y = Ranges.mc.player.lastTickPosY + (Ranges.mc.player.posY - Ranges.mc.player.lastTickPosY) * event.getPartialTicks() - renderManager.viewerPosY;
            final double z = Ranges.mc.player.lastTickPosZ + (Ranges.mc.player.posZ - Ranges.mc.player.lastTickPosZ) * event.getPartialTicks() - renderManager.viewerPosZ;
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glLineWidth((float)this.lineWidth.getValue());
            GL11.glBegin(1);
            for (int i = 0; i <= 360; ++i) {
                final Vec3d vec = new Vec3d(x + Math.sin(i * 3.141592653589793 / 180.0) * this.radius.getValue(), y + 0.1, z + Math.cos(i * 3.141592653589793 / 180.0) * this.radius.getValue());
                final RayTraceResult result = Ranges.mc.world.rayTraceBlocks(new Vec3d(x, y + 0.1, z), vec, false, true, false);
                if (result != null && this.raytrace.getValue()) {
                    hVectors.add(result.hitVec);
                }
                else {
                    hVectors.add(vec);
                }
            }
            for (int j = 0; j < hVectors.size() - 1; ++j) {
                GL11.glVertex3d(hVectors.get(j).x, hVectors.get(j).y, hVectors.get(j).z);
                GL11.glVertex3d(hVectors.get(j + 1).x, hVectors.get(j + 1).y, hVectors.get(j + 1).z);
            }
            GL11.glEnd();
            GlStateManager.resetColor();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            RenderUtil.GlPost();
            GlStateManager.popMatrix();
        }
        if (this.hitSpheres.getValue()) {
            for (final EntityPlayer player : Ranges.mc.world.playerEntities) {
                if (player != null && (!player.equals((Object)Ranges.mc.player) || this.ownSphere.getValue())) {
                    final Vec3d interpolated = EntityUtil.interpolateEntity((Entity)player, event.getPartialTicks());
                    if (Phobos.friendManager.isFriend(player.getName())) {
                        GL11.glColor4f(0.15f, 0.15f, 1.0f, 1.0f);
                    }
                    else if (Ranges.mc.player.getDistance((Entity)player) >= 64.0f) {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    else {
                        GL11.glColor4f(1.0f, Ranges.mc.player.getDistance((Entity)player) / 150.0f, 0.0f, 1.0f);
                    }
                    RenderUtil.drawSphere(interpolated.x, interpolated.y, interpolated.z, this.radius.getValue().floatValue(), 20, 15);
                }
            }
        }
    }
}
