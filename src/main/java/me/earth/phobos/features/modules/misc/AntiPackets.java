// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import java.util.Iterator;
import me.earth.phobos.features.command.Command;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketAnimation;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class AntiPackets extends Module
{
    private Setting<Mode> mode;
    private Setting<Integer> page;
    private Setting<Integer> pages;
    private Setting<Boolean> AdvancementInfo;
    private Setting<Boolean> Animation;
    private Setting<Boolean> BlockAction;
    private Setting<Boolean> BlockBreakAnim;
    private Setting<Boolean> BlockChange;
    private Setting<Boolean> Camera;
    private Setting<Boolean> ChangeGameState;
    private Setting<Boolean> Chat;
    private Setting<Boolean> ChunkData;
    private Setting<Boolean> CloseWindow;
    private Setting<Boolean> CollectItem;
    private Setting<Boolean> CombatEvent;
    private Setting<Boolean> ConfirmTransaction;
    private Setting<Boolean> Cooldown;
    private Setting<Boolean> CustomPayload;
    private Setting<Boolean> CustomSound;
    private Setting<Boolean> DestroyEntities;
    private Setting<Boolean> Disconnect;
    private Setting<Boolean> DisplayObjective;
    private Setting<Boolean> Effect;
    private Setting<Boolean> Entity;
    private Setting<Boolean> EntityAttach;
    private Setting<Boolean> EntityEffect;
    private Setting<Boolean> EntityEquipment;
    private Setting<Boolean> EntityHeadLook;
    private Setting<Boolean> EntityMetadata;
    private Setting<Boolean> EntityProperties;
    private Setting<Boolean> EntityStatus;
    private Setting<Boolean> EntityTeleport;
    private Setting<Boolean> EntityVelocity;
    private Setting<Boolean> Explosion;
    private Setting<Boolean> HeldItemChange;
    private Setting<Boolean> JoinGame;
    private Setting<Boolean> KeepAlive;
    private Setting<Boolean> Maps;
    private Setting<Boolean> MoveVehicle;
    private Setting<Boolean> MultiBlockChange;
    private Setting<Boolean> OpenWindow;
    private Setting<Boolean> Particles;
    private Setting<Boolean> PlaceGhostRecipe;
    private Setting<Boolean> PlayerAbilities;
    private Setting<Boolean> PlayerListHeaderFooter;
    private Setting<Boolean> PlayerListItem;
    private Setting<Boolean> PlayerPosLook;
    private Setting<Boolean> RecipeBook;
    private Setting<Boolean> RemoveEntityEffect;
    private Setting<Boolean> ResourcePackSend;
    private Setting<Boolean> Respawn;
    private Setting<Boolean> ScoreboardObjective;
    private Setting<Boolean> SelectAdvancementsTab;
    private Setting<Boolean> ServerDifficulty;
    private Setting<Boolean> SetExperience;
    private Setting<Boolean> SetPassengers;
    private Setting<Boolean> SetSlot;
    private Setting<Boolean> SignEditorOpen;
    private Setting<Boolean> SoundEffect;
    private Setting<Boolean> SpawnExperienceOrb;
    private Setting<Boolean> SpawnGlobalEntity;
    private Setting<Boolean> SpawnMob;
    private Setting<Boolean> SpawnObject;
    private Setting<Boolean> SpawnPainting;
    private Setting<Boolean> SpawnPlayer;
    private Setting<Boolean> SpawnPosition;
    private Setting<Boolean> Statistics;
    private Setting<Boolean> TabComplete;
    private Setting<Boolean> Teams;
    private Setting<Boolean> TimeUpdate;
    private Setting<Boolean> Title;
    private Setting<Boolean> UnloadChunk;
    private Setting<Boolean> UpdateBossInfo;
    private Setting<Boolean> UpdateHealth;
    private Setting<Boolean> UpdateScore;
    private Setting<Boolean> UpdateTileEntity;
    private Setting<Boolean> UseBed;
    private Setting<Boolean> WindowItems;
    private Setting<Boolean> WindowProperty;
    private Setting<Boolean> WorldBorder;
    private Setting<Boolean> Animations;
    private Setting<Boolean> ChatMessage;
    private Setting<Boolean> ClickWindow;
    private Setting<Boolean> ClientSettings;
    private Setting<Boolean> ClientStatus;
    private Setting<Boolean> CloseWindows;
    private Setting<Boolean> ConfirmTeleport;
    private Setting<Boolean> ConfirmTransactions;
    private Setting<Boolean> CreativeInventoryAction;
    private Setting<Boolean> CustomPayloads;
    private Setting<Boolean> EnchantItem;
    private Setting<Boolean> EntityAction;
    private Setting<Boolean> HeldItemChanges;
    private Setting<Boolean> Input;
    private Setting<Boolean> KeepAlives;
    private Setting<Boolean> PlaceRecipe;
    private Setting<Boolean> Player;
    private Setting<Boolean> PlayerAbility;
    private Setting<Boolean> PlayerDigging;
    private Setting<Boolean> PlayerTryUseItem;
    private Setting<Boolean> PlayerTryUseItemOnBlock;
    private Setting<Boolean> RecipeInfo;
    private Setting<Boolean> ResourcePackStatus;
    private Setting<Boolean> SeenAdvancements;
    private Setting<Boolean> PlayerPackets;
    private Setting<Boolean> Spectate;
    private Setting<Boolean> SteerBoat;
    private Setting<Boolean> TabCompletion;
    private Setting<Boolean> UpdateSign;
    private Setting<Boolean> UseEntity;
    private Setting<Boolean> VehicleMove;
    private int hudAmount;
    
    public AntiPackets() {
        super("AntiPackets", "Blocks Packets", Category.MISC, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Packets", Mode.CLIENT));
        this.page = (Setting<Integer>)this.register(new Setting("SPackets", 1, 1, 10, v -> this.mode.getValue() == Mode.SERVER));
        this.pages = (Setting<Integer>)this.register(new Setting("CPackets", 1, 1, 4, v -> this.mode.getValue() == Mode.CLIENT));
        this.AdvancementInfo = (Setting<Boolean>)this.register(new Setting("AdvancementInfo", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.Animation = (Setting<Boolean>)this.register(new Setting("Animation", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.BlockAction = (Setting<Boolean>)this.register(new Setting("BlockAction", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.BlockBreakAnim = (Setting<Boolean>)this.register(new Setting("BlockBreakAnim", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.BlockChange = (Setting<Boolean>)this.register(new Setting("BlockChange", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.Camera = (Setting<Boolean>)this.register(new Setting("Camera", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.ChangeGameState = (Setting<Boolean>)this.register(new Setting("ChangeGameState", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.Chat = (Setting<Boolean>)this.register(new Setting("Chat", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 1));
        this.ChunkData = (Setting<Boolean>)this.register(new Setting("ChunkData", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.CloseWindow = (Setting<Boolean>)this.register(new Setting("CloseWindow", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.CollectItem = (Setting<Boolean>)this.register(new Setting("CollectItem", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.CombatEvent = (Setting<Boolean>)this.register(new Setting("Combatevent", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.ConfirmTransaction = (Setting<Boolean>)this.register(new Setting("ConfirmTransaction", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.Cooldown = (Setting<Boolean>)this.register(new Setting("Cooldown", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.CustomPayload = (Setting<Boolean>)this.register(new Setting("CustomPayload", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.CustomSound = (Setting<Boolean>)this.register(new Setting("CustomSound", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 2));
        this.DestroyEntities = (Setting<Boolean>)this.register(new Setting("DestroyEntities", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.Disconnect = (Setting<Boolean>)this.register(new Setting("Disconnect", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.DisplayObjective = (Setting<Boolean>)this.register(new Setting("DisplayObjective", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.Effect = (Setting<Boolean>)this.register(new Setting("Effect", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.Entity = (Setting<Boolean>)this.register(new Setting("Entity", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.EntityAttach = (Setting<Boolean>)this.register(new Setting("EntityAttach", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.EntityEffect = (Setting<Boolean>)this.register(new Setting("EntityEffect", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.EntityEquipment = (Setting<Boolean>)this.register(new Setting("EntityEquipment", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 3));
        this.EntityHeadLook = (Setting<Boolean>)this.register(new Setting("EntityHeadLook", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.EntityMetadata = (Setting<Boolean>)this.register(new Setting("EntityMetadata", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.EntityProperties = (Setting<Boolean>)this.register(new Setting("EntityProperties", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.EntityStatus = (Setting<Boolean>)this.register(new Setting("EntityStatus", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.EntityTeleport = (Setting<Boolean>)this.register(new Setting("EntityTeleport", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.EntityVelocity = (Setting<Boolean>)this.register(new Setting("EntityVelocity", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.Explosion = (Setting<Boolean>)this.register(new Setting("Explosion", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.HeldItemChange = (Setting<Boolean>)this.register(new Setting("HeldItemChange", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 4));
        this.JoinGame = (Setting<Boolean>)this.register(new Setting("JoinGame", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.KeepAlive = (Setting<Boolean>)this.register(new Setting("KeepAlive", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.Maps = (Setting<Boolean>)this.register(new Setting("Maps", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.MoveVehicle = (Setting<Boolean>)this.register(new Setting("MoveVehicle", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.MultiBlockChange = (Setting<Boolean>)this.register(new Setting("MultiBlockChange", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.OpenWindow = (Setting<Boolean>)this.register(new Setting("OpenWindow", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.Particles = (Setting<Boolean>)this.register(new Setting("Particles", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.PlaceGhostRecipe = (Setting<Boolean>)this.register(new Setting("PlaceGhostRecipe", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 5));
        this.PlayerAbilities = (Setting<Boolean>)this.register(new Setting("PlayerAbilities", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.PlayerListHeaderFooter = (Setting<Boolean>)this.register(new Setting("PlayerListHeaderFooter", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.PlayerListItem = (Setting<Boolean>)this.register(new Setting("PlayerListItem", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.PlayerPosLook = (Setting<Boolean>)this.register(new Setting("PlayerPosLook", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.RecipeBook = (Setting<Boolean>)this.register(new Setting("RecipeBook", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.RemoveEntityEffect = (Setting<Boolean>)this.register(new Setting("RemoveEntityEffect", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.ResourcePackSend = (Setting<Boolean>)this.register(new Setting("ResourcePackSend", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.Respawn = (Setting<Boolean>)this.register(new Setting("Respawn", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 6));
        this.ScoreboardObjective = (Setting<Boolean>)this.register(new Setting("ScoreboardObjective", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SelectAdvancementsTab = (Setting<Boolean>)this.register(new Setting("SelectAdvancementsTab", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.ServerDifficulty = (Setting<Boolean>)this.register(new Setting("ServerDifficulty", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SetExperience = (Setting<Boolean>)this.register(new Setting("SetExperience", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SetPassengers = (Setting<Boolean>)this.register(new Setting("SetPassengers", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SetSlot = (Setting<Boolean>)this.register(new Setting("SetSlot", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SignEditorOpen = (Setting<Boolean>)this.register(new Setting("SignEditorOpen", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SoundEffect = (Setting<Boolean>)this.register(new Setting("SoundEffect", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 7));
        this.SpawnExperienceOrb = (Setting<Boolean>)this.register(new Setting("SpawnExperienceOrb", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.SpawnGlobalEntity = (Setting<Boolean>)this.register(new Setting("SpawnGlobalEntity", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.SpawnMob = (Setting<Boolean>)this.register(new Setting("SpawnMob", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.SpawnObject = (Setting<Boolean>)this.register(new Setting("SpawnObject", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.SpawnPainting = (Setting<Boolean>)this.register(new Setting("SpawnPainting", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.SpawnPlayer = (Setting<Boolean>)this.register(new Setting("SpawnPlayer", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.SpawnPosition = (Setting<Boolean>)this.register(new Setting("SpawnPosition", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.Statistics = (Setting<Boolean>)this.register(new Setting("Statistics", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 8));
        this.TabComplete = (Setting<Boolean>)this.register(new Setting("TabComplete", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.Teams = (Setting<Boolean>)this.register(new Setting("Teams", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.TimeUpdate = (Setting<Boolean>)this.register(new Setting("TimeUpdate", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.Title = (Setting<Boolean>)this.register(new Setting("Title", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.UnloadChunk = (Setting<Boolean>)this.register(new Setting("UnloadChunk", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.UpdateBossInfo = (Setting<Boolean>)this.register(new Setting("UpdateBossInfo", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.UpdateHealth = (Setting<Boolean>)this.register(new Setting("UpdateHealth", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.UpdateScore = (Setting<Boolean>)this.register(new Setting("UpdateScore", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 9));
        this.UpdateTileEntity = (Setting<Boolean>)this.register(new Setting("UpdateTileEntity", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 10));
        this.UseBed = (Setting<Boolean>)this.register(new Setting("UseBed", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 10));
        this.WindowItems = (Setting<Boolean>)this.register(new Setting("WindowItems", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 10));
        this.WindowProperty = (Setting<Boolean>)this.register(new Setting("WindowProperty", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 10));
        this.WorldBorder = (Setting<Boolean>)this.register(new Setting("WorldBorder", false, v -> this.mode.getValue() == Mode.SERVER && this.page.getValue() == 10));
        this.Animations = (Setting<Boolean>)this.register(new Setting("Animations", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.ChatMessage = (Setting<Boolean>)this.register(new Setting("ChatMessage", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.ClickWindow = (Setting<Boolean>)this.register(new Setting("ClickWindow", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.ClientSettings = (Setting<Boolean>)this.register(new Setting("ClientSettings", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.ClientStatus = (Setting<Boolean>)this.register(new Setting("ClientStatus", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.CloseWindows = (Setting<Boolean>)this.register(new Setting("CloseWindows", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.ConfirmTeleport = (Setting<Boolean>)this.register(new Setting("ConfirmTeleport", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.ConfirmTransactions = (Setting<Boolean>)this.register(new Setting("ConfirmTransactions", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 1));
        this.CreativeInventoryAction = (Setting<Boolean>)this.register(new Setting("CreativeInventoryAction", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.CustomPayloads = (Setting<Boolean>)this.register(new Setting("CustomPayloads", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.EnchantItem = (Setting<Boolean>)this.register(new Setting("EnchantItem", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.EntityAction = (Setting<Boolean>)this.register(new Setting("EntityAction", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.HeldItemChanges = (Setting<Boolean>)this.register(new Setting("HeldItemChanges", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.Input = (Setting<Boolean>)this.register(new Setting("Input", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.KeepAlives = (Setting<Boolean>)this.register(new Setting("KeepAlives", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.PlaceRecipe = (Setting<Boolean>)this.register(new Setting("PlaceRecipe", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 2));
        this.Player = (Setting<Boolean>)this.register(new Setting("Player", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.PlayerAbility = (Setting<Boolean>)this.register(new Setting("PlayerAbility", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.PlayerDigging = (Setting<Boolean>)this.register(new Setting("PlayerDigging", false, v -> this.mode.getValue() == Mode.CLIENT && this.page.getValue() == 3));
        this.PlayerTryUseItem = (Setting<Boolean>)this.register(new Setting("PlayerTryUseItem", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.PlayerTryUseItemOnBlock = (Setting<Boolean>)this.register(new Setting("TryUseItemOnBlock", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.RecipeInfo = (Setting<Boolean>)this.register(new Setting("RecipeInfo", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.ResourcePackStatus = (Setting<Boolean>)this.register(new Setting("ResourcePackStatus", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.SeenAdvancements = (Setting<Boolean>)this.register(new Setting("SeenAdvancements", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 3));
        this.PlayerPackets = (Setting<Boolean>)this.register(new Setting("PlayerPackets", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.Spectate = (Setting<Boolean>)this.register(new Setting("Spectate", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.SteerBoat = (Setting<Boolean>)this.register(new Setting("SteerBoat", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.TabCompletion = (Setting<Boolean>)this.register(new Setting("TabCompletion", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.UpdateSign = (Setting<Boolean>)this.register(new Setting("UpdateSign", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.UseEntity = (Setting<Boolean>)this.register(new Setting("UseEntity", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.VehicleMove = (Setting<Boolean>)this.register(new Setting("VehicleMove", false, v -> this.mode.getValue() == Mode.CLIENT && this.pages.getValue() == 4));
        this.hudAmount = 0;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!this.isEnabled()) {
            return;
        }
        if (event.getPacket() instanceof CPacketAnimation && this.Animations.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketChatMessage && this.ChatMessage.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketClickWindow && this.ClickWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketClientSettings && this.ClientSettings.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketClientStatus && this.ClientStatus.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCloseWindow && this.CloseWindows.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport && this.ConfirmTeleport.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTransaction && this.ConfirmTransactions.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCreativeInventoryAction && this.CreativeInventoryAction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCustomPayload && this.CustomPayloads.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketEnchantItem && this.EnchantItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketEntityAction && this.EntityAction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketHeldItemChange && this.HeldItemChanges.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketInput && this.Input.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketKeepAlive && this.KeepAlives.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlaceRecipe && this.PlaceRecipe.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer && this.Player.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerAbilities && this.PlayerAbility.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerDigging && this.PlayerDigging.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && this.PlayerTryUseItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && this.PlayerTryUseItemOnBlock.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketRecipeInfo && this.RecipeInfo.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketResourcePackStatus && this.ResourcePackStatus.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketSeenAdvancements && this.SeenAdvancements.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketSpectate && this.Spectate.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketSteerBoat && this.SteerBoat.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketTabComplete && this.TabCompletion.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketUpdateSign && this.UpdateSign.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketUseEntity && this.UseEntity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketVehicleMove && this.VehicleMove.getValue()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (!this.isEnabled()) {
            return;
        }
        if (event.getPacket() instanceof SPacketAdvancementInfo && this.AdvancementInfo.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketAnimation && this.Animation.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketBlockAction && this.BlockAction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketBlockBreakAnim && this.BlockBreakAnim.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketBlockChange && this.BlockChange.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCamera && this.Camera.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChangeGameState && this.ChangeGameState.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChat && this.Chat.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChunkData && this.ChunkData.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCloseWindow && this.CloseWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCollectItem && this.CollectItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCombatEvent && this.CombatEvent.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketConfirmTransaction && this.ConfirmTransaction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCooldown && this.Cooldown.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCustomPayload && this.CustomPayload.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCustomSound && this.CustomSound.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketDestroyEntities && this.DestroyEntities.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketDisconnect && this.Disconnect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChunkData && this.ChunkData.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCloseWindow && this.CloseWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCollectItem && this.CollectItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketDisplayObjective && this.DisplayObjective.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEffect && this.Effect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntity && this.Entity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityAttach && this.EntityAttach.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityEffect && this.EntityEffect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityEquipment && this.EntityEquipment.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityHeadLook && this.EntityHeadLook.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityMetadata && this.EntityMetadata.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityProperties && this.EntityProperties.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityStatus && this.EntityStatus.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityTeleport && this.EntityTeleport.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityVelocity && this.EntityVelocity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketExplosion && this.Explosion.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketHeldItemChange && this.HeldItemChange.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketJoinGame && this.JoinGame.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketKeepAlive && this.KeepAlive.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketMaps && this.Maps.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketMoveVehicle && this.MoveVehicle.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketMultiBlockChange && this.MultiBlockChange.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketOpenWindow && this.OpenWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketParticles && this.Particles.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlaceGhostRecipe && this.PlaceGhostRecipe.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerAbilities && this.PlayerAbilities.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerListHeaderFooter && this.PlayerListHeaderFooter.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerListItem && this.PlayerListItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && this.PlayerPosLook.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketRecipeBook && this.RecipeBook.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketRemoveEntityEffect && this.RemoveEntityEffect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketResourcePackSend && this.ResourcePackSend.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketRespawn && this.Respawn.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketScoreboardObjective && this.ScoreboardObjective.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSelectAdvancementsTab && this.SelectAdvancementsTab.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketServerDifficulty && this.ServerDifficulty.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSetExperience && this.SetExperience.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSetPassengers && this.SetPassengers.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSetSlot && this.SetSlot.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSignEditorOpen && this.SignEditorOpen.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && this.SoundEffect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnExperienceOrb && this.SpawnExperienceOrb.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnGlobalEntity && this.SpawnGlobalEntity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnMob && this.SpawnMob.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnObject && this.SpawnObject.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnPainting && this.SpawnPainting.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnPlayer && this.SpawnPlayer.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnPosition && this.SpawnPosition.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketStatistics && this.Statistics.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTabComplete && this.TabComplete.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTeams && this.Teams.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTimeUpdate && this.TimeUpdate.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTitle && this.Title.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUnloadChunk && this.UnloadChunk.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateBossInfo && this.UpdateBossInfo.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateHealth && this.UpdateHealth.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateScore && this.UpdateScore.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateTileEntity && this.UpdateTileEntity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUseBed && this.UseBed.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketWindowItems && this.WindowItems.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketWindowProperty && this.WindowProperty.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketWorldBorder && this.WorldBorder.getValue()) {
            event.setCanceled(true);
        }
    }
    
    @Override
    public void onEnable() {
        final String standart = "§aAntiPackets On!§f Cancelled Packets: ";
        final StringBuilder text = new StringBuilder(standart);
        if (!this.settings.isEmpty()) {
            for (final Setting setting : this.settings) {
                if (setting.getValue() instanceof Boolean && (Boolean)setting.getValue() && !setting.getName().equalsIgnoreCase("Enabled")) {
                    if (setting.getName().equalsIgnoreCase("drawn")) {
                        continue;
                    }
                    final String name = setting.getName();
                    text.append(name).append(", ");
                }
            }
        }
        if (text.toString().equals(standart)) {
            Command.sendMessage("§aAntiPackets On!§f Currently not cancelling any Packets.");
        }
        else {
            final String output = this.removeLastChar(this.removeLastChar(text.toString()));
            Command.sendMessage(output);
        }
    }
    
    @Override
    public void onUpdate() {
        int amount = 0;
        if (!this.settings.isEmpty()) {
            for (final Setting setting : this.settings) {
                if (setting.getValue() instanceof Boolean && (Boolean)setting.getValue() && !setting.getName().equalsIgnoreCase("Enabled")) {
                    if (setting.getName().equalsIgnoreCase("drawn")) {
                        continue;
                    }
                    ++amount;
                }
            }
        }
        this.hudAmount = amount;
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.hudAmount == 0) {
            return "";
        }
        return this.hudAmount + "";
    }
    
    public String removeLastChar(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
    
    public enum Mode
    {
        CLIENT, 
        SERVER;
    }
}
