// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos;

import org.apache.logging.log4j.LogManager;
import me.earth.phobos.features.modules.misc.RPC;
import org.lwjgl.opengl.Display;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import me.earth.phobos.manager.SafetyManager;
import me.earth.phobos.manager.NotificationManager;
import me.earth.phobos.manager.HoleManager;
import me.earth.phobos.manager.TotemPopManager;
import me.earth.phobos.manager.ReloadManager;
import me.earth.phobos.manager.PacketManager;
import me.earth.phobos.manager.TimerManager;
import me.earth.phobos.manager.InventoryManager;
import me.earth.phobos.manager.PotionManager;
import me.earth.phobos.manager.ServerManager;
import me.earth.phobos.manager.ColorManager;
import me.earth.phobos.manager.TextManager;
import me.earth.phobos.manager.FriendManager;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.manager.ConfigManager;
import me.earth.phobos.manager.EventManager;
import me.earth.phobos.manager.CommandManager;
import me.earth.phobos.manager.RotationManager;
import me.earth.phobos.manager.PositionManager;
import me.earth.phobos.manager.SpeedManager;
import me.earth.phobos.manager.ModuleManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "earthhack", name = "3arthh4ck", version = "1.5.4")
public class Phobos
{
    public static final String MODID = "earthhack";
    public static final String MODNAME = "3arthh4ck";
    public static final String MODVER = "1.5.4";
    public static final String NAME_UNICODE = "3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
    public static final String PHOBOS_UNICODE = "\u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
    public static final String CHAT_SUFFIX = " \u23d0 3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
    public static final String PHOBOS_SUFFIX = " \u23d0 \u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
    public static final Logger LOGGER;
    public static final boolean SERVER = false;
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    private static boolean unloaded;
    @Mod.Instance
    public static Phobos INSTANCE;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        Phobos.LOGGER.info("ohare is cute!!!");
        Phobos.LOGGER.info("faggot above - 3vt");
        Phobos.LOGGER.info("megyn wins again");
        Phobos.LOGGER.info("gtfo my logs - 3arth");
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        Display.setTitle("3arthh4ck - v.1.5.4");
        load();
    }
    
    public static void load() {
        Phobos.LOGGER.info("\n\nLoading 3arthh4ck 1.5.4");
        Phobos.unloaded = false;
        if (Phobos.reloadManager != null) {
            Phobos.reloadManager.unload();
            Phobos.reloadManager = null;
        }
        Phobos.totemPopManager = new TotemPopManager();
        Phobos.timerManager = new TimerManager();
        Phobos.packetManager = new PacketManager();
        Phobos.serverManager = new ServerManager();
        Phobos.colorManager = new ColorManager();
        Phobos.textManager = new TextManager();
        Phobos.moduleManager = new ModuleManager();
        Phobos.speedManager = new SpeedManager();
        Phobos.rotationManager = new RotationManager();
        Phobos.positionManager = new PositionManager();
        Phobos.commandManager = new CommandManager();
        Phobos.eventManager = new EventManager();
        Phobos.configManager = new ConfigManager();
        Phobos.fileManager = new FileManager();
        Phobos.friendManager = new FriendManager();
        Phobos.potionManager = new PotionManager();
        Phobos.inventoryManager = new InventoryManager();
        Phobos.holeManager = new HoleManager();
        Phobos.notificationManager = new NotificationManager();
        Phobos.safetyManager = new SafetyManager();
        Phobos.LOGGER.info("Initialized Managers");
        Phobos.moduleManager.init();
        Phobos.LOGGER.info("Modules loaded.");
        Phobos.configManager.init();
        Phobos.eventManager.init();
        Phobos.LOGGER.info("EventManager loaded.");
        Phobos.textManager.init(true);
        Phobos.moduleManager.onLoad();
        Phobos.totemPopManager.init();
        Phobos.timerManager.init();
        if (Phobos.moduleManager.getModuleByClass(RPC.class).isEnabled()) {
            DiscordPresence.start();
        }
        Phobos.LOGGER.info("3arthh4ck initialized!\n");
    }
    
    public static void unload(final boolean unload) {
        Phobos.LOGGER.info("\n\nUnloading 3arthh4ck 1.5.4");
        if (unload) {
            (Phobos.reloadManager = new ReloadManager()).init((Phobos.commandManager != null) ? Phobos.commandManager.getPrefix() : ".");
        }
        onUnload();
        Phobos.eventManager = null;
        Phobos.holeManager = null;
        Phobos.timerManager = null;
        Phobos.moduleManager = null;
        Phobos.totemPopManager = null;
        Phobos.serverManager = null;
        Phobos.colorManager = null;
        Phobos.textManager = null;
        Phobos.speedManager = null;
        Phobos.rotationManager = null;
        Phobos.positionManager = null;
        Phobos.commandManager = null;
        Phobos.configManager = null;
        Phobos.fileManager = null;
        Phobos.friendManager = null;
        Phobos.potionManager = null;
        Phobos.inventoryManager = null;
        Phobos.notificationManager = null;
        Phobos.safetyManager = null;
        Phobos.LOGGER.info("3arthh4ck unloaded!\n");
    }
    
    public static void reload() {
        unload(false);
        load();
    }
    
    public static void onUnload() {
        if (!Phobos.unloaded) {
            Phobos.eventManager.onUnload();
            Phobos.moduleManager.onUnload();
            Phobos.configManager.saveConfig(Phobos.configManager.config.replaceFirst("phobos/", ""));
            Phobos.moduleManager.onUnloadPost();
            Phobos.timerManager.unload();
            Phobos.unloaded = true;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger("3arthh4ck");
        Phobos.unloaded = false;
    }
}
