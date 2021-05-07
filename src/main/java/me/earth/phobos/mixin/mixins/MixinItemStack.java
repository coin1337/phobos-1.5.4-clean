// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.player.TrueDurability;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ItemStack.class })
public abstract class MixinItemStack
{
    @Shadow
    private int itemDamage;
    
    @Inject(method = { "<init>(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)V" }, at = { @At("RETURN") })
    @Dynamic
    private void initHook(final Item item, final int idkWhatDisIsIPastedThis, final int dura, final NBTTagCompound compound, final CallbackInfo info) {
        this.itemDamage = this.checkDurability(ItemStack.class.cast(this), this.itemDamage, dura);
    }
    
    @Inject(method = { "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V" }, at = { @At("RETURN") })
    private void initHook2(final NBTTagCompound compound, final CallbackInfo info) {
        this.itemDamage = this.checkDurability(ItemStack.class.cast(this), this.itemDamage, compound.getShort("Damage"));
    }
    
    private int checkDurability(final ItemStack item, final int damage, final int dura) {
        int trueDura = damage;
        if (TrueDurability.getInstance().isOn() && dura < 0) {
            trueDura = dura;
        }
        return trueDura;
    }
}
