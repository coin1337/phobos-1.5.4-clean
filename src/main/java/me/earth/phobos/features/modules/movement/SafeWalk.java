// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;

public class SafeWalk extends Module
{
    public SafeWalk() {
        super("SafeWalk", "Walks safe", Category.MOVEMENT, true, false, false);
    }
    
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (event.getStage() == 0) {
            double x = event.getX();
            final double y = event.getY();
            double z = event.getZ();
            if (SafeWalk.mc.player.onGround) {
                final double increment = 0.05;
                while (x != 0.0 && this.isOffsetBBEmpty(x, -1.0, 0.0)) {
                    if (x < increment && x >= -increment) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= increment;
                    }
                    else {
                        x += increment;
                    }
                }
                while (z != 0.0 && this.isOffsetBBEmpty(0.0, -1.0, z)) {
                    if (z < increment && z >= -increment) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= increment;
                    }
                    else {
                        z += increment;
                    }
                }
                while (x != 0.0 && z != 0.0 && this.isOffsetBBEmpty(x, -1.0, z)) {
                    if (x < increment && x >= -increment) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= increment;
                    }
                    else {
                        x += increment;
                    }
                    if (z < increment && z >= -increment) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= increment;
                    }
                    else {
                        z += increment;
                    }
                }
            }
            event.setX(x);
            event.setY(y);
            event.setZ(z);
        }
    }
    
    public boolean isOffsetBBEmpty(final double offsetX, final double offsetY, final double offsetZ) {
        final EntityPlayerSP playerSP = SafeWalk.mc.player;
        return SafeWalk.mc.world.getCollisionBoxes((Entity)playerSP, playerSP.getEntityBoundingBox().offset(offsetX, offsetY, offsetZ)).isEmpty();
    }
}
