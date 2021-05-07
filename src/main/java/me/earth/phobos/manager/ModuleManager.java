// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.manager;

import java.util.concurrent.TimeUnit;
import me.earth.phobos.util.Util;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import me.earth.phobos.features.gui.PhobosGui;
import org.lwjgl.input.Keyboard;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.Render2DEvent;
import java.util.function.Consumer;
import net.minecraftforge.common.MinecraftForge;
import java.util.Arrays;
import java.util.Iterator;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.Capes;
import me.earth.phobos.features.modules.client.StreamerMode;
import me.earth.phobos.features.modules.client.Components;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.FontMod;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.modules.client.Notifications;
import me.earth.phobos.features.modules.render.Trails;
import me.earth.phobos.features.modules.render.VoidESP;
import me.earth.phobos.features.modules.render.HandColor;
import me.earth.phobos.features.modules.render.OffscreenESP;
import me.earth.phobos.features.modules.render.Ranges;
import me.earth.phobos.features.modules.render.PortalESP;
import me.earth.phobos.features.modules.render.XRay;
import me.earth.phobos.features.modules.render.LogoutSpots;
import me.earth.phobos.features.modules.render.Tracer;
import me.earth.phobos.features.modules.render.Trajectories;
import me.earth.phobos.features.modules.render.BlockHighlight;
import me.earth.phobos.features.modules.render.HoleESP;
import me.earth.phobos.features.modules.render.ESP;
import me.earth.phobos.features.modules.render.Skeleton;
import me.earth.phobos.features.modules.render.Chams;
import me.earth.phobos.features.modules.render.CameraClip;
import me.earth.phobos.features.modules.render.Nametags;
import me.earth.phobos.features.modules.render.Fullbright;
import me.earth.phobos.features.modules.render.SmallShield;
import me.earth.phobos.features.modules.render.NoRender;
import me.earth.phobos.features.modules.render.StorageESP;
import me.earth.phobos.features.modules.player.Yaw;
import me.earth.phobos.features.modules.player.TrueDurability;
import me.earth.phobos.features.modules.player.MCP;
import me.earth.phobos.features.modules.player.TpsSync;
import me.earth.phobos.features.modules.player.EchestBP;
import me.earth.phobos.features.modules.player.Scaffold;
import me.earth.phobos.features.modules.player.Jesus;
import me.earth.phobos.features.modules.player.NoHunger;
import me.earth.phobos.features.modules.player.Replenish;
import me.earth.phobos.features.modules.player.XCarry;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.modules.player.MultiTask;
import me.earth.phobos.features.modules.player.Blink;
import me.earth.phobos.features.modules.movement.SafeWalk;
import me.earth.phobos.features.modules.player.Speedmine;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.modules.player.FastPlace;
import me.earth.phobos.features.modules.player.TimerSpeed;
import me.earth.phobos.features.modules.player.FakePlayer;
import me.earth.phobos.features.modules.player.LiquidInteract;
import me.earth.phobos.features.modules.player.Reach;
import me.earth.phobos.features.modules.movement.TestPhase;
import me.earth.phobos.features.modules.movement.AutoWalk;
import me.earth.phobos.features.modules.movement.IceSpeed;
import me.earth.phobos.features.modules.movement.NoFall;
import me.earth.phobos.features.modules.movement.HoleTP;
import me.earth.phobos.features.modules.movement.NoSlowDown;
import me.earth.phobos.features.modules.movement.ElytraFlight;
import me.earth.phobos.features.modules.movement.Flight;
import me.earth.phobos.features.modules.movement.TPSpeed;
import me.earth.phobos.features.modules.movement.Static;
import me.earth.phobos.features.modules.movement.Phase;
import me.earth.phobos.features.modules.movement.AntiLevitate;
import me.earth.phobos.features.modules.movement.Sprint;
import me.earth.phobos.features.modules.movement.Step;
import me.earth.phobos.features.modules.movement.Speed;
import me.earth.phobos.features.modules.movement.Velocity;
import me.earth.phobos.features.modules.movement.Strafe;
import me.earth.phobos.features.modules.misc.Bypass;
import me.earth.phobos.features.modules.misc.Translator;
import me.earth.phobos.features.modules.misc.RPC;
import me.earth.phobos.features.modules.misc.Logger;
import me.earth.phobos.features.modules.misc.AntiPackets;
import me.earth.phobos.features.modules.misc.Tracker;
import me.earth.phobos.features.modules.misc.NoAFK;
import me.earth.phobos.features.modules.misc.AutoReconnect;
import me.earth.phobos.features.modules.misc.Nuker;
import me.earth.phobos.features.modules.misc.MobOwner;
import me.earth.phobos.features.modules.misc.ExtraTab;
import me.earth.phobos.features.modules.misc.AntiVanish;
import me.earth.phobos.features.modules.misc.Spammer;
import me.earth.phobos.features.modules.misc.Exploits;
import me.earth.phobos.features.modules.misc.KitDelete;
import me.earth.phobos.features.modules.misc.AutoLog;
import me.earth.phobos.features.modules.misc.NoSoundLag;
import me.earth.phobos.features.modules.misc.PingSpoof;
import me.earth.phobos.features.modules.misc.MCF;
import me.earth.phobos.features.modules.misc.NoRotate;
import me.earth.phobos.features.modules.misc.AutoRespawn;
import me.earth.phobos.features.modules.misc.NoHandShake;
import me.earth.phobos.features.modules.misc.BuildHeight;
import me.earth.phobos.features.modules.misc.BetterPortals;
import me.earth.phobos.features.modules.misc.ChatModifier;
import me.earth.phobos.features.modules.combat.Crasher;
import me.earth.phobos.features.modules.combat.ArmorMessage;
import me.earth.phobos.features.modules.combat.BedBomb;
import me.earth.phobos.features.modules.combat.AntiTrap;
import me.earth.phobos.features.modules.combat.AutoArmor;
import me.earth.phobos.features.modules.combat.Webaura;
import me.earth.phobos.features.modules.combat.Selftrap;
import me.earth.phobos.features.modules.combat.HoleFiller;
import me.earth.phobos.features.modules.combat.Killaura;
import me.earth.phobos.features.modules.combat.BowSpam;
import me.earth.phobos.features.modules.combat.Criticals;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.modules.combat.AutoTrap;
import me.earth.phobos.features.modules.combat.Surround;
import me.earth.phobos.features.modules.combat.Offhand;
import java.util.List;
import me.earth.phobos.features.modules.Module;
import java.util.ArrayList;
import me.earth.phobos.features.Feature;

public class ModuleManager extends Feature
{
    public ArrayList<Module> modules;
    public List<Module> sortedModules;
    public List<Module> alphabeticallySortedModules;
    public Animation animationThread;
    
    public ModuleManager() {
        this.modules = new ArrayList<Module>();
        this.sortedModules = new ArrayList<Module>();
        this.alphabeticallySortedModules = new ArrayList<Module>();
    }
    
    public void init() {
        this.modules.add(new Offhand());
        this.modules.add(new Surround());
        this.modules.add(new AutoTrap());
        this.modules.add(new AutoCrystal());
        this.modules.add(new Criticals());
        this.modules.add(new BowSpam());
        this.modules.add(new Killaura());
        this.modules.add(new HoleFiller());
        this.modules.add(new Selftrap());
        this.modules.add(new Webaura());
        this.modules.add(new AutoArmor());
        this.modules.add(new AntiTrap());
        this.modules.add(new BedBomb());
        this.modules.add(new ArmorMessage());
        this.modules.add(new Crasher());
        //this.modules.add(new Auto32k());
        this.modules.add(new ChatModifier());
        this.modules.add(new BetterPortals());
        this.modules.add(new BuildHeight());
        this.modules.add(new NoHandShake());
        this.modules.add(new AutoRespawn());
        this.modules.add(new NoRotate());
        this.modules.add(new MCF());
        this.modules.add(new PingSpoof());
        this.modules.add(new NoSoundLag());
        this.modules.add(new AutoLog());
        this.modules.add(new KitDelete());
        this.modules.add(new Exploits());
        this.modules.add(new Spammer());
        this.modules.add(new AntiVanish());
        this.modules.add(new ExtraTab());
        this.modules.add(new MobOwner());
        this.modules.add(new Nuker());
        this.modules.add(new AutoReconnect());
        this.modules.add(new NoAFK());
        this.modules.add(new Tracker());
        this.modules.add(new AntiPackets());
        this.modules.add(new Logger());
        this.modules.add(new RPC());
        this.modules.add(new Translator());
        this.modules.add(new Bypass());
        this.modules.add(new Strafe());
        this.modules.add(new Velocity());
        this.modules.add(new Speed());
        this.modules.add(new Step());
        this.modules.add(new Sprint());
        this.modules.add(new AntiLevitate());
        this.modules.add(new Phase());
        this.modules.add(new Static());
        this.modules.add(new TPSpeed());
        this.modules.add(new Flight());
        this.modules.add(new ElytraFlight());
        this.modules.add(new NoSlowDown());
        this.modules.add(new HoleTP());
        this.modules.add(new NoFall());
        this.modules.add(new IceSpeed());
        this.modules.add(new AutoWalk());
        this.modules.add(new TestPhase());
        this.modules.add(new Reach());
        this.modules.add(new LiquidInteract());
        this.modules.add(new FakePlayer());
        this.modules.add(new TimerSpeed());
        this.modules.add(new FastPlace());
        this.modules.add(new Freecam());
        this.modules.add(new Speedmine());
        this.modules.add(new SafeWalk());
        this.modules.add(new Blink());
        this.modules.add(new MultiTask());
        this.modules.add(new BlockTweaks());
        this.modules.add(new XCarry());
        this.modules.add(new Replenish());
        this.modules.add(new NoHunger());
        this.modules.add(new Jesus());
        this.modules.add(new Scaffold());
        this.modules.add(new EchestBP());
        this.modules.add(new TpsSync());
        this.modules.add(new MCP());
        this.modules.add(new TrueDurability());
        this.modules.add(new Yaw());
        this.modules.add(new StorageESP());
        this.modules.add(new NoRender());
        this.modules.add(new SmallShield());
        this.modules.add(new Fullbright());
        this.modules.add(new Nametags());
        this.modules.add(new CameraClip());
        this.modules.add(new Chams());
        this.modules.add(new Skeleton());
        this.modules.add(new ESP());
        this.modules.add(new HoleESP());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Trajectories());
        this.modules.add(new Tracer());
        this.modules.add(new LogoutSpots());
        this.modules.add(new XRay());
        this.modules.add(new PortalESP());
        this.modules.add(new Ranges());
        this.modules.add(new OffscreenESP());
        this.modules.add(new HandColor());
        this.modules.add(new VoidESP());
        this.modules.add(new Trails());
        this.modules.add(new Notifications());
        this.modules.add(new HUD());
        this.modules.add(new ToolTips());
        this.modules.add(new FontMod());
        this.modules.add(new ClickGui());
        this.modules.add(new Managers());
        this.modules.add(new Components());
        this.modules.add(new StreamerMode());
        this.modules.add(new Capes());
        this.modules.add(new Colors());
        (this.animationThread = new Animation()).start();
    }
    
    public Module getModuleByName(final String name) {
        for (final Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    
    public <T extends Module> T getModuleByClass(final Class<T> clazz) {
        for (final Module module : this.modules) {
            if (clazz.isInstance(module)) {
                return (T)module;
            }
        }
        return null;
    }
    
    public void enableModule(final Class clazz) {
        final Module module = this.getModuleByClass((Class<Module>)clazz);
        if (module != null) {
            module.enable();
        }
    }
    
    public void disableModule(final Class clazz) {
        final Module module = this.getModuleByClass((Class<Module>)clazz);
        if (module != null) {
            module.disable();
        }
    }
    
    public void enableModule(final String name) {
        final Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }
    
    public void disableModule(final String name) {
        final Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }
    
    public boolean isModuleEnabled(final String name) {
        final Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }
    
    public boolean isModuleEnabled(final Class clazz) {
        final Module module = this.getModuleByClass((Class<Module>)clazz);
        return module != null && module.isOn();
    }
    
    public Module getModuleByDisplayName(final String displayName) {
        for (final Module module : this.modules) {
            if (module.getDisplayName().equalsIgnoreCase(displayName)) {
                return module;
            }
        }
        return null;
    }
    
    public ArrayList<Module> getEnabledModules() {
        final ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (final Module module : this.modules) {
            if (module.isEnabled() || module.isSliding()) {
                enabledModules.add(module);
            }
        }
        return enabledModules;
    }
    
    public ArrayList<Module> getModulesByCategory(final Module.Category category) {
        final ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
            	modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }
    
    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }
    
    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(MinecraftForge.EVENT_BUS::register);
        this.modules.forEach(Module::onLoad);
    }
    
    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }
    
    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }
    
    public void onRender2D(final Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }
    
    public void onRender3D(final Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }
    
    public void sortModules(final boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }
    
    public void alphabeticallySortModules() {
        this.alphabeticallySortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(Module::getDisplayName)).collect(Collectors.toList());
    }
    
    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }
    
    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }
    
    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }
    
    public void onUnloadPost() {
        for (final Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }
    
    public void onKeyPressed(final int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof PhobosGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
    
    private class Animation extends Thread
    {
        ScheduledExecutorService service;
        public Module module;
        public float offset;
        public float vOffset;
        
        public Animation() {
            super("Animation");
            this.service = Executors.newSingleThreadScheduledExecutor();
        }
        
        @Override
        public void run() {
            for (final Module module : ModuleManager.this.sortedModules) {
                final String text = module.getDisplayName() + "§7" + ((module.getDisplayInfo() != null) ? (" [§f" + module.getDisplayInfo() + "§7" + "]") : "");
                module.offset = ModuleManager.this.renderer.getStringWidth(text) / (float)HUD.getInstance().animationHorizontalTime.getValue();
                module.vOffset = ModuleManager.this.renderer.getFontHeight() / (float)HUD.getInstance().animationVerticalTime.getValue();
                if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                    if (module.arrayListOffset <= module.offset || Util.mc.world == null) {
                        continue;
                    }
                    final Module module2 = module;
                    module2.arrayListOffset -= module.offset;
                    module.sliding = true;
                }
                else {
                    if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) {
                        continue;
                    }
                    if (module.arrayListOffset < ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                        final Module module3 = module;
                        module3.arrayListOffset += module.offset;
                        module.sliding = true;
                    }
                    else {
                        module.sliding = false;
                    }
                }
            }
        }
        
        @Override
        public void start() {
            System.out.println("Starting animation thread.");
            this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
        }
    }
}
