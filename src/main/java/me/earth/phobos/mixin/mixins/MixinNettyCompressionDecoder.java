// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.mixin.mixins;

import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import me.earth.phobos.features.modules.misc.Bypass;
import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ NettyCompressionDecoder.class })
public abstract class MixinNettyCompressionDecoder
{
    @ModifyConstant(method = { "decode" }, constant = { @Constant(intValue = 2097152) })
    private int decodeHook(final int n) {
        if (Bypass.getInstance().isOn() && Bypass.getInstance().packets.getValue() && Bypass.getInstance().noLimit.getValue()) {
            return Integer.MAX_VALUE;
        }
        return n;
    }
}
