// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.render;

import net.minecraft.util.EnumParticleTypes;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Trails extends Module
{
    private final Setting<ParticleType> type;
    private final Setting<Integer> delay;
    private final Setting<Double> xOffset;
    private final Setting<Double> yOffset;
    private final Setting<Double> zOffset;
    public Timer timer;
    
    public Trails() {
        super("Trails", "Renders trails.", Category.RENDER, true, false, false);
        this.type = (Setting<ParticleType>)this.register(new Setting("Type", ParticleType.HEART));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 50, 1, 500));
        this.xOffset = (Setting<Double>)this.register(new Setting("XOffset", 0.0, (-10.0), 10.0));
        this.yOffset = (Setting<Double>)this.register(new Setting("YOffset", 2.7, (-10.0), 10.0));
        this.zOffset = (Setting<Double>)this.register(new Setting("ZOffset", 0.0, (-10.0), 10.0));
        this.timer = new Timer();
    }
    
    @Override
    public void onUpdate() {
        if (this.timer.passedMs(this.delay.getValue())) {
            Trails.mc.world.spawnParticle(this.type.getValue().particleType, Trails.mc.player.posX + this.xOffset.getValue(), Trails.mc.player.posY + this.yOffset.getValue(), Trails.mc.player.posZ + this.zOffset.getValue(), 0.0, 0.0, 0.0, new int[0]);
            this.timer.reset();
        }
    }
    
    public enum ParticleType
    {
        HEART(EnumParticleTypes.HEART), 
        MOB_APPEARANCE(EnumParticleTypes.MOB_APPEARANCE), 
        WATER_DROP(EnumParticleTypes.WATER_DROP), 
        SLIME(EnumParticleTypes.SLIME), 
        SNOW_SHOVEL(EnumParticleTypes.SNOW_SHOVEL), 
        SNOWBALL(EnumParticleTypes.SNOWBALL), 
        REDSTONE(EnumParticleTypes.REDSTONE), 
        FOOTSTEP(EnumParticleTypes.FOOTSTEP), 
        LAVA(EnumParticleTypes.LAVA), 
        FLAME(EnumParticleTypes.FLAME), 
        ENCHANTMENT_TABLE(EnumParticleTypes.ENCHANTMENT_TABLE), 
        PORTAL(EnumParticleTypes.PORTAL), 
        NOTE(EnumParticleTypes.NOTE), 
        TOWN_AURA(EnumParticleTypes.TOWN_AURA), 
        VILLAGER_HAPPY(EnumParticleTypes.VILLAGER_HAPPY), 
        VILLAGER_ANGRY(EnumParticleTypes.VILLAGER_ANGRY), 
        SPELL(EnumParticleTypes.SPELL), 
        SPELL_INSTANT(EnumParticleTypes.SPELL_INSTANT), 
        SPELL_MOB(EnumParticleTypes.SPELL_MOB), 
        SPELL_MOB_AMBIENT(EnumParticleTypes.SPELL_MOB_AMBIENT), 
        SPELL_WITCH(EnumParticleTypes.SPELL_WITCH), 
        SMOKE_LARGE(EnumParticleTypes.SMOKE_LARGE), 
        SMOKE_NORMAL(EnumParticleTypes.SMOKE_NORMAL), 
        CRIT_MAGIC(EnumParticleTypes.CRIT_MAGIC), 
        SUSPENDED_DEPTH(EnumParticleTypes.SUSPENDED_DEPTH), 
        WATER_WAKE(EnumParticleTypes.WATER_WAKE), 
        WATER_SPLASH(EnumParticleTypes.WATER_SPLASH), 
        FIREWORKS_SPARK(EnumParticleTypes.FIREWORKS_SPARK), 
        BARRIER(EnumParticleTypes.BARRIER), 
        CLOUD(EnumParticleTypes.CLOUD), 
        CRIT(EnumParticleTypes.CRIT), 
        EXPLOSION_NORMAL(EnumParticleTypes.EXPLOSION_NORMAL), 
        EXPLOSION_LARGE(EnumParticleTypes.EXPLOSION_LARGE), 
        EXPLOSION_HUGE(EnumParticleTypes.EXPLOSION_HUGE), 
        DRIP_LAVA(EnumParticleTypes.DRIP_LAVA), 
        DRIP_WATER(EnumParticleTypes.DRIP_WATER);
        
        public EnumParticleTypes particleType;
        
        private ParticleType(final EnumParticleTypes particleType) {
            this.particleType = particleType;
        }
    }
}
