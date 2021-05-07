// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.client;

import net.minecraft.client.gui.ScaledResolution;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import java.util.function.ToIntFunction;
import net.minecraft.init.Items;
import me.earth.phobos.util.EntityUtil;
import java.util.Iterator;
import me.earth.phobos.manager.TextManager;
import net.minecraft.client.Minecraft;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.potion.PotionEffect;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.Phobos;
import net.minecraft.client.gui.GuiChat;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.util.ColorUtil;
import java.awt.Color;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.ClientEvent;
import net.minecraft.network.play.client.CPacketUseEntity;
import me.earth.phobos.event.events.PacketEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import java.util.HashMap;
import net.minecraft.util.SoundEvent;
import me.earth.phobos.util.Timer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.Map;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class HUD extends Module
{
    private final Setting<Boolean> renderingUp;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> rainbow;
    public Setting<Integer> factor;
    public Setting<Boolean> rolling;
    public Setting<Boolean> staticRainbow;
    public Setting<Integer> rainbowSpeed;
    public Setting<Integer> rainbowSaturation;
    public Setting<Integer> rainbowBrightness;
    public Setting<Boolean> potionIcons;
    public Setting<Boolean> shadow;
    private final Setting<WaterMark> watermark;
    private final Setting<Boolean> modeVer;
    private final Setting<Boolean> arrayList;
    public Setting<Integer> animationHorizontalTime;
    public Setting<Integer> animationVerticalTime;
    private final Setting<Boolean> alphabeticalSorting;
    private final Setting<Boolean> serverBrand;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> tps;
    private final Setting<Boolean> fps;
    private final Setting<Boolean> coords;
    private final Setting<Boolean> direction;
    private final Setting<Boolean> speed;
    private final Setting<Boolean> potions;
    public Setting<Boolean> textRadar;
    private final Setting<Boolean> armor;
    private final Setting<Boolean> durability;
    private final Setting<Boolean> percent;
    private final Setting<Boolean> totems;
    private final Setting<Boolean> queue;
    private final Setting<Greeter> greeter;
    private final Setting<String> spoofGreeter;
    public Setting<Boolean> time;
    private final Setting<LagNotify> lag;
    private final Setting<Boolean> hitMarkers;
    private final Setting<Sound> sound;
    public Setting<Integer> hudRed;
    public Setting<Integer> hudGreen;
    public Setting<Integer> hudBlue;
    private final Setting<Boolean> grayNess;
    public Setting<Boolean> potions1;
    private static HUD INSTANCE;
    private Map<String, Integer> players;
    public Map<Module, Float> moduleProgressMap;
    private static final ResourceLocation box;
    private static final ItemStack totem;
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    private final Timer timer;
    private final Timer moduleTimer;
    public Map<Integer, Integer> colorMap;
    private static final ResourceLocation codHitmarker;
    private static final ResourceLocation csgoHitmarker;
    public static final SoundEvent COD_EVENT;
    public static final SoundEvent CSGO_EVENT;
    
    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Category.CLIENT, true, false, false);
        this.renderingUp = (Setting<Boolean>)this.register(new Setting("RenderingUp", false, "Orientation of the HUD-Elements."));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false, "Universal colors for hud."));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", false, "Rainbow hud."));
        this.factor = (Setting<Integer>)this.register(new Setting("Factor", 1, 0, 20, v -> this.rainbow.getValue()));
        this.rolling = (Setting<Boolean>)this.register(new Setting("Rolling", false, v -> this.rainbow.getValue()));
        this.staticRainbow = (Setting<Boolean>)this.register(new Setting("Static", false, v -> this.rainbow.getValue()));
        this.rainbowSpeed = (Setting<Integer>)this.register(new Setting("Speed", 20, 0, 100, v -> this.rainbow.getValue()));
        this.rainbowSaturation = (Setting<Integer>)this.register(new Setting("Saturation", 255, 0, 255, v -> this.rainbow.getValue()));
        this.rainbowBrightness = (Setting<Integer>)this.register(new Setting("Brightness", 255, 0, 255, v -> this.rainbow.getValue()));
        this.potionIcons = (Setting<Boolean>)this.register(new Setting("PotionIcons", true, "Draws Potion Icons."));
        this.shadow = (Setting<Boolean>)this.register(new Setting("Shadow", false, "Draws the text with a shadow."));
        this.watermark = (Setting<WaterMark>)this.register(new Setting("Logo", WaterMark.NONE, "WaterMark"));
        this.modeVer = (Setting<Boolean>)this.register(new Setting("Version", false, v -> this.watermark.getValue() != WaterMark.NONE));
        this.arrayList = (Setting<Boolean>)this.register(new Setting("ActiveModules", false, "Lists the active modules."));
        this.animationHorizontalTime = (Setting<Integer>)this.register(new Setting("AnimationHTime", 500, 1, 1000, v -> this.arrayList.getValue()));
        this.animationVerticalTime = (Setting<Integer>)this.register(new Setting("AnimationVTime", 50, 1, 500, v -> this.arrayList.getValue()));
        this.alphabeticalSorting = (Setting<Boolean>)this.register(new Setting("AlphabeticalSorting", false, v -> this.arrayList.getValue()));
        this.serverBrand = (Setting<Boolean>)this.register(new Setting("ServerBrand", false, "Brand of the server you are on."));
        this.ping = (Setting<Boolean>)this.register(new Setting("Ping", false, "Your response time to the server."));
        this.tps = (Setting<Boolean>)this.register(new Setting("TPS", false, "Ticks per second of the server."));
        this.fps = (Setting<Boolean>)this.register(new Setting("FPS", false, "Your frames per second."));
        this.coords = (Setting<Boolean>)this.register(new Setting("Coords", false, "Your current coordinates"));
        this.direction = (Setting<Boolean>)this.register(new Setting("Direction", false, "The Direction you are facing."));
        this.speed = (Setting<Boolean>)this.register(new Setting("Speed", false, "Your Speed"));
        this.potions = (Setting<Boolean>)this.register(new Setting("Potions", false, "Your Speed"));
        this.textRadar = (Setting<Boolean>)this.register(new Setting("TextRadar", false, "A TextRadar"));
        this.armor = (Setting<Boolean>)this.register(new Setting("Armor", false, "ArmorHUD"));
        this.durability = (Setting<Boolean>)this.register(new Setting("Durability", false, "Durability"));
        this.percent = (Setting<Boolean>)this.register(new Setting("Percent", true, v -> this.armor.getValue()));
        this.totems = (Setting<Boolean>)this.register(new Setting("Totems", false, "TotemHUD"));
        this.queue = (Setting<Boolean>)this.register(new Setting("2b2tQueue", false, "Shows the 2b2t queue."));
        this.greeter = (Setting<Greeter>)this.register(new Setting("Greeter", Greeter.NONE, "Greets you."));
        this.spoofGreeter = (Setting<String>)this.register(new Setting("GreeterName", "3arthqu4ke", v -> this.greeter.getValue() == Greeter.CUSTOM));
        this.time = (Setting<Boolean>)this.register(new Setting("Time", false, "The time"));
        this.lag = (Setting<LagNotify>)this.register(new Setting("Lag", LagNotify.GRAY, "Lag Notifier"));
        this.hitMarkers = (Setting<Boolean>)this.register(new Setting("HitMarkers", true));
        this.sound = (Setting<Sound>)this.register(new Setting("Sound", Sound.NONE, v -> this.hitMarkers.getValue()));
        this.hudRed = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255, v -> !this.rainbow.getValue()));
        this.hudGreen = (Setting<Integer>)this.register(new Setting("Green", 0, 0, 255, v -> !this.rainbow.getValue()));
        this.hudBlue = (Setting<Integer>)this.register(new Setting("Blue", 0, 0, 255, v -> !this.rainbow.getValue()));
        this.grayNess = (Setting<Boolean>)this.register(new Setting("FutureColour", true));
        this.potions1 = (Setting<Boolean>)this.register(new Setting("LevelPotions", false, v -> this.potions.getValue()));
        this.players = new HashMap<String, Integer>();
        this.moduleProgressMap = new HashMap<Module, Float>();
        this.timer = new Timer();
        this.moduleTimer = new Timer();
        this.colorMap = new HashMap<Integer, Integer>();
        this.setInstance();
    }
    
    private void setInstance() {
        HUD.INSTANCE = this;
    }
    
    public static HUD getInstance() {
        if (HUD.INSTANCE == null) {
            HUD.INSTANCE = new HUD();
        }
        return HUD.INSTANCE;
    }
    
    @Override
    public void onUpdate() {
        if (this.timer.passedMs(Managers.getInstance().textRadarUpdates.getValue())) {
            this.players = this.getTextRadarPlayers();
            this.timer.reset();
        }
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
    }
    
    @SubscribeEvent
    public void onAttack(final AttackEntityEvent event) {
        this.shouldIncrement = true;
        switch (this.sound.getValue()) {
            case COD: {
                HUD.mc.world.playSound((EntityPlayer)HUD.mc.player, HUD.mc.player.posX, HUD.mc.player.posY, HUD.mc.player.posZ, HUD.COD_EVENT, SoundCategory.PLAYERS, 3.0f, 1.0f);
                break;
            }
            case CSGO: {
                HUD.mc.world.playSound((EntityPlayer)HUD.mc.player, HUD.mc.player.posX, HUD.mc.player.posY, HUD.mc.player.posZ, HUD.CSGO_EVENT, SoundCategory.PLAYERS, 3.0f, 1.0f);
                break;
            }
        }
    }
    
    @SubscribeEvent
    public void onSendPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
            this.shouldIncrement = true;
            switch (this.sound.getValue()) {
                case COD: {
                    HUD.mc.world.playSound((EntityPlayer)HUD.mc.player, HUD.mc.player.posX, HUD.mc.player.posY, HUD.mc.player.posZ, HUD.COD_EVENT, SoundCategory.PLAYERS, 3.0f, 1.0f);
                    break;
                }
                case CSGO: {
                    HUD.mc.world.playSound((EntityPlayer)HUD.mc.player, HUD.mc.player.posX, HUD.mc.player.posY, HUD.mc.player.posZ, HUD.CSGO_EVENT, SoundCategory.PLAYERS, 3.0f, 1.0f);
                    break;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onModuleToggle(final ClientEvent event) {
        if (event.getFeature() instanceof Module) {
            if (event.getStage() == 0) {
                for (float i = 0.0f; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0f) {
                    if (this.moduleTimer.passedMs(1L)) {
                        this.moduleProgressMap.put((Module)event.getFeature(), this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i);
                    }
                    this.timer.reset();
                }
            }
            else if (event.getStage() == 1) {
                for (float i = 0.0f; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0f) {
                    if (this.moduleTimer.passedMs(1L)) {
                        this.moduleProgressMap.put((Module)event.getFeature(), this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i);
                    }
                    this.timer.reset();
                }
            }
        }
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        if (fullNullCheck()) {
            return;
        }
        final int colorSpeed = 101 - this.rainbowSpeed.getValue();
        final float hue = this.colorSync.getValue() ? Colors.INSTANCE.hue : (System.currentTimeMillis() % (360 * colorSpeed) / (360.0f * colorSpeed));
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        float tempHue = hue;
        for (int i = 0; i <= height; ++i) {
            if (this.colorSync.getValue()) {
                this.colorMap.put(i, Color.HSBtoRGB(tempHue, Colors.INSTANCE.rainbowSaturation.getValue() / 255.0f, Colors.INSTANCE.rainbowBrightness.getValue() / 255.0f));
            }
            else {
                this.colorMap.put(i, Color.HSBtoRGB(tempHue, this.rainbowSaturation.getValue() / 255.0f, this.rainbowBrightness.getValue() / 255.0f));
            }
            tempHue += 1.0f / height * this.factor.getValue();
        }
        if (this.rainbow.getValue() && !this.rolling.getValue()) {
            this.color = (this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, this.rainbowSaturation.getValue() / 255.0f, this.rainbowBrightness.getValue() / 255.0f));
        }
        else if (!this.rainbow.getValue()) {
            this.color = (this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(this.hudRed.getValue(), this.hudGreen.getValue(), this.hudBlue.getValue()));
        }
        final String grayString = this.grayNess.getValue() ? "§7" : "";
        switch (this.watermark.getValue()) {
            case PHOBOS: {
                this.renderer.drawString("Phobos" + (this.modeVer.getValue() ? " v1.5.4" : ""), 2.0f, 2.0f, (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2)) : this.color, true);
                break;
            }
            case EARTH: {
                this.renderer.drawString("3arthh4ck" + (this.modeVer.getValue() ? " v1.5.4" : ""), 2.0f, 2.0f, (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2)) : this.color, true);
                break;
            }
        }
        if (this.textRadar.getValue()) {
            this.drawTextRadar((ToolTips.getInstance().isOff() || !ToolTips.getInstance().shulkerSpy.getValue() || !ToolTips.getInstance().render.getValue()) ? 0 : ToolTips.getInstance().getTextRadarY());
        }
        int j = this.renderingUp.getValue() ? 0 : ((HUD.mc.currentScreen instanceof GuiChat) ? 14 : 0);
        if (this.arrayList.getValue()) {
            if (this.renderingUp.getValue()) {
                for (int k = 0; k < (this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++k) {
                    final Module module = this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(k) : Phobos.moduleManager.sortedModules.get(k);
                    final Module module2 = this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(MathUtil.clamp(k - 1, 0, Phobos.moduleManager.alphabeticallySortedModules.size() - 1)) : Phobos.moduleManager.sortedModules.get(MathUtil.clamp(k - 1, 0, Phobos.moduleManager.sortedModules.size() - 1));
                    final String text = module.getDisplayName() + "§7" + ((module.getDisplayInfo() != null) ? (" [§f" + module.getDisplayInfo() + "§7" + "]") : "");
                    this.renderer.drawString(text, width - 2 - this.renderer.getStringWidth(text) + ((this.animationHorizontalTime.getValue() == 1) ? 0.0f : module.arrayListOffset), (float)(2 + j * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(MathUtil.clamp(2 + j * 10, 0, height))) : this.color, true);
                    ++j;
                }
            }
            else {
                for (int k = 0; k < (this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++k) {
                    final Module module = this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(Phobos.moduleManager.alphabeticallySortedModules.size() - 1 - k) : Phobos.moduleManager.sortedModules.get(k);
                    final Module module2 = this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(MathUtil.clamp(k + 1, 0, Phobos.moduleManager.alphabeticallySortedModules.size() - 1)) : Phobos.moduleManager.sortedModules.get(MathUtil.clamp(k + 1, 0, Phobos.moduleManager.sortedModules.size() - 1));
                    final String text = module.getDisplayName() + "§7" + ((module.getDisplayInfo() != null) ? (" [§f" + module.getDisplayInfo() + "§7" + "]") : "");
                    final TextManager renderer = this.renderer;
                    final String text5 = text;
                    final float x = width - 2 - this.renderer.getStringWidth(text) + ((this.animationHorizontalTime.getValue() == 1) ? 0.0f : module.arrayListOffset);
                    final int n = height;
                    j += 10;
                    renderer.drawString(text5, x, (float)(n - j), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(MathUtil.clamp(height - j, 0, height))) : this.color, true);
                }
            }
        }
        int k = this.renderingUp.getValue() ? ((HUD.mc.currentScreen instanceof GuiChat) ? 0 : 0) : 0;
        if (this.renderingUp.getValue()) {
            if (this.serverBrand.getValue()) {
                final String text2 = grayString + "Server brand " + "§f" + Phobos.serverManager.getServerBrand();
                final TextManager renderer2 = this.renderer;
                final String text6 = text2;
                final float x2 = (float)(width - (this.renderer.getStringWidth(text2) + 2));
                final int n2 = height - 2;
                k += 10;
                renderer2.drawString(text6, x2, (float)(n2 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
            }
            if (this.potions.getValue()) {
                for (final PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
                    final String text = Phobos.potionManager.getColoredPotionString(effect);
                    final TextManager renderer3 = this.renderer;
                    final String text7 = text;
                    final float x3 = (float)(width - (this.renderer.getStringWidth(text) + 2));
                    final int n3 = height - 2;
                    k += 10;
                    renderer3.drawString(text7, x3, (float)(n3 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
                }
            }
            if (this.speed.getValue()) {
                final String text2 = grayString + "Speed " + "§f" + Phobos.speedManager.getSpeedKpH() + " km/h";
                final TextManager renderer4 = this.renderer;
                final String text8 = text2;
                final float x4 = (float)(width - (this.renderer.getStringWidth(text2) + 2));
                final int n4 = height - 2;
                k += 10;
                renderer4.drawString(text8, x4, (float)(n4 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
            }
            if (this.time.getValue()) {
                final String text2 = grayString + "Time " + "§f" + new SimpleDateFormat("h:mm a").format(new Date());
                final TextManager renderer5 = this.renderer;
                final String text9 = text2;
                final float x5 = (float)(width - (this.renderer.getStringWidth(text2) + 2));
                final int n5 = height - 2;
                k += 10;
                renderer5.drawString(text9, x5, (float)(n5 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
            }
            if (this.durability.getValue()) {
                final int itemDamage = HUD.mc.player.getHeldItemMainhand().getMaxDamage() - HUD.mc.player.getHeldItemMainhand().getItemDamage();
                if (itemDamage > 0) {
                    final String text3 = grayString + "Durability " + "§a" + itemDamage;
                    final TextManager renderer6 = this.renderer;
                    final String text10 = text3;
                    final float x6 = (float)(width - (this.renderer.getStringWidth(text3) + 2));
                    final int n6 = height - 2;
                    k += 10;
                    renderer6.drawString(text10, x6, (float)(n6 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
                }
            }
            if (this.tps.getValue()) {
                final String text2 = grayString + "TPS " + "§f" + Phobos.serverManager.getTPS();
                final TextManager renderer7 = this.renderer;
                final String text11 = text2;
                final float x7 = (float)(width - (this.renderer.getStringWidth(text2) + 2));
                final int n7 = height - 2;
                k += 10;
                renderer7.drawString(text11, x7, (float)(n7 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
            }
            final String fpsText = grayString + "FPS " + "§f" + Minecraft.getDebugFPS();
            final String text3 = grayString + "Ping " + "§f" + Phobos.serverManager.getPing();
            if (this.renderer.getStringWidth(text3) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    final TextManager renderer8 = this.renderer;
                    final String text12 = text3;
                    final float x8 = (float)(width - (this.renderer.getStringWidth(text3) + 2));
                    final int n8 = height - 2;
                    k += 10;
                    renderer8.drawString(text12, x8, (float)(n8 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
                }
                if (this.fps.getValue()) {
                    final TextManager renderer9 = this.renderer;
                    final String text13 = fpsText;
                    final float x9 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
                    final int n9 = height - 2;
                    k += 10;
                    renderer9.drawString(text13, x9, (float)(n9 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
                }
            }
            else {
                if (this.fps.getValue()) {
                    final TextManager renderer10 = this.renderer;
                    final String text14 = fpsText;
                    final float x10 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
                    final int n10 = height - 2;
                    k += 10;
                    renderer10.drawString(text14, x10, (float)(n10 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
                }
                if (this.ping.getValue()) {
                    final TextManager renderer11 = this.renderer;
                    final String text15 = text3;
                    final float x11 = (float)(width - (this.renderer.getStringWidth(text3) + 2));
                    final int n11 = height - 2;
                    k += 10;
                    renderer11.drawString(text15, x11, (float)(n11 - k), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(height - k)) : this.color, true);
                }
            }
        }
        else {
            if (this.serverBrand.getValue()) {
                final String text2 = grayString + "Server brand " + "§f" + Phobos.serverManager.getServerBrand();
                this.renderer.drawString(text2, (float)(width - (this.renderer.getStringWidth(text2) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
            }
            if (this.potions.getValue()) {
                for (final PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
                    final String text = Phobos.potionManager.getColoredPotionString(effect);
                    this.renderer.drawString(text, (float)(width - (this.renderer.getStringWidth(text) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
                }
            }
            if (this.speed.getValue()) {
                final String text2 = grayString + "Speed " + "§f" + Phobos.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(text2, (float)(width - (this.renderer.getStringWidth(text2) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
            }
            if (this.time.getValue()) {
                final String text2 = grayString + "Time " + "§f" + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(text2, (float)(width - (this.renderer.getStringWidth(text2) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
            }
            if (this.durability.getValue()) {
                final int itemDamage = HUD.mc.player.getHeldItemMainhand().getMaxDamage() - HUD.mc.player.getHeldItemMainhand().getItemDamage();
                if (itemDamage > 0) {
                    final String text3 = grayString + "Durability " + "§a" + itemDamage;
                    this.renderer.drawString(text3, (float)(width - (this.renderer.getStringWidth(text3) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
                }
            }
            if (this.tps.getValue()) {
                final String text2 = grayString + "TPS " + "§f" + Phobos.serverManager.getTPS();
                this.renderer.drawString(text2, (float)(width - (this.renderer.getStringWidth(text2) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
            }
            final String fpsText = grayString + "FPS " + "§f" + Minecraft.getDebugFPS();
            final String text3 = grayString + "Ping " + "§f" + Phobos.serverManager.getPing();
            if (this.renderer.getStringWidth(text3) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    this.renderer.drawString(text3, (float)(width - (this.renderer.getStringWidth(text3) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
                }
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
                }
            }
            else {
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
                }
                if (this.ping.getValue()) {
                    this.renderer.drawString(text3, (float)(width - (this.renderer.getStringWidth(text3) + 2)), (float)(2 + k++ * 10), (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2 + k * 10)) : this.color, true);
                }
            }
        }
        final boolean inHell = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");
        final int posX = (int)HUD.mc.player.posX;
        final int posY = (int)HUD.mc.player.posY;
        final int posZ = (int)HUD.mc.player.posZ;
        final float nether = inHell ? 8.0f : 0.125f;
        final int hposX = (int)(HUD.mc.player.posX * nether);
        final int hposZ = (int)(HUD.mc.player.posZ * nether);
        if (this.renderingUp.getValue()) {
            Phobos.notificationManager.handleNotifications(height - (k + 16));
        }
        else {
            Phobos.notificationManager.handleNotifications(height - (j + 16));
        }
        k = ((HUD.mc.currentScreen instanceof GuiChat) ? 14 : 0);
        final String coordinates = grayString + "XYZ " + "§f" + posX + ", " + posY + ", " + posZ + " " + grayString + "[" + "§f" + hposX + ", " + hposZ + grayString + "]";
        final String text4 = (this.direction.getValue() ? (Phobos.rotationManager.getDirection4D(false) + " ") : "") + (this.coords.getValue() ? coordinates : "") + "";
        final TextManager renderer12 = this.renderer;
        final String text16 = text4;
        final float x12 = 2.0f;
        final int n12 = height;
        k += 10;
        final float y = (float)(n12 - k);
        int color;
        if (this.rolling.getValue() && this.rainbow.getValue()) {
            final Map<Integer, Integer> colorMap = this.colorMap;
            final int n13 = height;
            k += 10;
            color = colorMap.get(n13 - k);
        }
        else {
            color = this.color;
        }
        renderer12.drawString(text16, x12, y, color, true);
        if (this.armor.getValue()) {
            this.renderArmorHUD(this.percent.getValue());
        }
        if (this.totems.getValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue() != Greeter.NONE) {
            this.renderGreeter();
        }
        if (this.lag.getValue() != LagNotify.NONE) {
            this.renderLag();
        }
        if (this.hitMarkers.getValue() && this.hitMarkerTimer > 0) {
            this.drawHitMarkers();
        }
    }
    
    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }
    
    public void renderGreeter() {
        final int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.greeter.getValue()) {
            case TIME: {
                text = text + MathUtil.getTimeOfDay() + HUD.mc.player.getDisplayNameString();
                break;
            }
            case LONG: {
                text = text + "Welcome to Phobos.eu " + HUD.mc.player.getDisplayNameString() + " :^)";
                break;
            }
            case CUSTOM: {
                text += this.spoofGreeter.getValue();
                break;
            }
            default: {
                text = text + "Welcome " + HUD.mc.player.getDisplayNameString();
                break;
            }
        }
        this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(2)) : this.color, true);
    }
    
    public void renderLag() {
        final int width = this.renderer.scaledWidth;
        if (Phobos.serverManager.isServerNotResponding()) {
            final String text = ((this.lag.getValue() == LagNotify.GRAY) ? "§7" : "§c") + "Server not responding: " + MathUtil.round(Phobos.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(20)) : this.color, true);
        }
    }
    
    public void renderArrayList() {
    }
    
    public void renderTotemHUD() {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            final int i = width / 2;
            final int iteration = 0;
            final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            final int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
    
    public void renderArmorHUD(final boolean percent) {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (!percent) {
                continue;
            }
            int dmg = 0;
            final int itemDurability = is.getMaxDamage() - is.getItemDamage();
            final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            if (percent) {
                dmg = 100 - (int)(red * 100.0f);
            }
            else {
                dmg = itemDurability;
            }
            this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float)(y - 11), ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
    
    public void drawHitMarkers() {
        final ScaledResolution resolution = new ScaledResolution(HUD.mc);
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f - 4.0f, resolution.getScaledHeight() / 2.0f - 4.0f, resolution.getScaledWidth() / 2.0f - 8.0f, resolution.getScaledHeight() / 2.0f - 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f + 4.0f, resolution.getScaledHeight() / 2.0f - 4.0f, resolution.getScaledWidth() / 2.0f + 8.0f, resolution.getScaledHeight() / 2.0f - 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f - 4.0f, resolution.getScaledHeight() / 2.0f + 4.0f, resolution.getScaledWidth() / 2.0f - 8.0f, resolution.getScaledHeight() / 2.0f + 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f + 4.0f, resolution.getScaledHeight() / 2.0f + 4.0f, resolution.getScaledWidth() / 2.0f + 8.0f, resolution.getScaledHeight() / 2.0f + 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
    }
    
    public void drawTextRadar(final int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (final Map.Entry<String, Integer> player : this.players.entrySet()) {
                final String text = player.getKey() + " ";
                final int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, (float)y, (this.rolling.getValue() && this.rainbow.getValue()) ? ((int)this.colorMap.get(y)) : this.color, true);
                y += textheight;
            }
        }
    }
    
    static {
        HUD.INSTANCE = new HUD();
        box = new ResourceLocation("textures/gui/container/shulker_box.png");
        totem = new ItemStack(Items.TOTEM_OF_UNDYING);
        codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
        csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
        COD_EVENT = new SoundEvent(HUD.codHitmarker);
        CSGO_EVENT = new SoundEvent(HUD.csgoHitmarker);
    }
    
    public enum Greeter
    {
        NONE, 
        NAME, 
        TIME, 
        LONG, 
        CUSTOM;
    }
    
    public enum LagNotify
    {
        NONE, 
        RED, 
        GRAY;
    }
    
    public enum WaterMark
    {
        NONE, 
        PHOBOS, 
        EARTH;
    }
    
    public enum Sound
    {
        NONE, 
        COD, 
        CSGO;
    }
}
