// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.util.FileUtil;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.StringUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;

public class Spammer extends Module
{
    public Setting<Mode> mode;
    public Setting<PwordMode> type;
    public Setting<DelayType> delayType;
    public Setting<Integer> delay;
    public Setting<Integer> delayDS;
    public Setting<Integer> delayMS;
    public Setting<String> msgTarget;
    public Setting<Boolean> greentext;
    public Setting<Boolean> random;
    public Setting<Boolean> loadFile;
    private final Timer timer;
    private final List<String> sendPlayers;
    private static final String fileName = "phobos/util/Spammer.txt";
    private static final String defaultMessage = "gg";
    private static final List<String> spamMessages;
    private static final Random rnd;
    
    public Spammer() {
        super("Spammer", "Spams stuff.", Category.MISC, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.PWORD));
        this.type = (Setting<PwordMode>)this.register(new Setting("Pword", PwordMode.CHAT, v -> this.mode.getValue() == Mode.PWORD));
        this.delayType = (Setting<DelayType>)this.register(new Setting("DelayType", DelayType.S));
        this.delay = (Setting<Integer>)this.register(new Setting("DelayS", 10, 1, 20, v -> this.delayType.getValue() == DelayType.S));
        this.delayDS = (Setting<Integer>)this.register(new Setting("DelayDS", 10, 1, 500, v -> this.delayType.getValue() == DelayType.DS));
        this.delayMS = (Setting<Integer>)this.register(new Setting("DelayDS", 10, 1, 1000, v -> this.delayType.getValue() == DelayType.MS));
        this.msgTarget = (Setting<String>)this.register(new Setting("MsgTarget", "Target...", v -> this.mode.getValue() == Mode.PWORD && this.type.getValue() == PwordMode.MSG));
        this.greentext = (Setting<Boolean>)this.register(new Setting("Greentext", false, v -> this.mode.getValue() == Mode.FILE));
        this.random = (Setting<Boolean>)this.register(new Setting("Random", false, v -> this.mode.getValue() == Mode.FILE));
        this.loadFile = (Setting<Boolean>)this.register(new Setting("LoadFile", false, v -> this.mode.getValue() == Mode.FILE));
        this.timer = new Timer();
        this.sendPlayers = new ArrayList<String>();
    }
    
    @Override
    public void onLoad() {
        this.readSpamFile();
        this.disable();
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        this.readSpamFile();
    }
    
    @Override
    public void onLogin() {
        this.disable();
    }
    
    @Override
    public void onLogout() {
        this.disable();
    }
    
    @Override
    public void onDisable() {
        Spammer.spamMessages.clear();
        this.timer.reset();
    }
    
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        if (this.loadFile.getValue()) {
            this.readSpamFile();
            this.loadFile.setValue(false);
        }
        switch (this.delayType.getValue()) {
            case MS: {
                if (!this.timer.passedMs(this.delayMS.getValue())) {
                    return;
                }
                break;
            }
            case S: {
                if (!this.timer.passedS(this.delay.getValue())) {
                    return;
                }
                break;
            }
            case DS: {
                if (!this.timer.passedDs(this.delayDS.getValue())) {
                    return;
                }
                break;
            }
        }
        if (this.mode.getValue() == Mode.PWORD) {
            String msg = "  \u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\n \u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\n \u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\n \u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\n \u2588\u2592\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\u2592\u2588\n \u2588\u2592\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\n \u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592";
            switch (this.type.getValue()) {
                case MSG: {
                    msg = "/msg " + this.msgTarget.getValue() + msg;
                    break;
                }
                case EVERYONE: {
                    String target = null;
                    if (Spammer.mc.getConnection() == null || Spammer.mc.getConnection().getPlayerInfoMap() == null) {
                        return;
                    }
                    for (final NetworkPlayerInfo info : Spammer.mc.getConnection().getPlayerInfoMap()) {
                        if (info != null && info.getDisplayName() != null) {
                            try {
                                final String str = info.getDisplayName().getFormattedText();
                                final String name = StringUtils.stripControlCodes(str);
                                if (name.equals(Spammer.mc.player.getName()) || this.sendPlayers.contains(name)) {
                                    continue;
                                }
                                target = name;
                                this.sendPlayers.add(name);
                                break;
                            }
                            catch (Exception ex) {}
                        }
                    }
                    if (target == null) {
                        this.sendPlayers.clear();
                        return;
                    }
                    msg = "/msg " + target + msg;
                    break;
                }
            }
            Spammer.mc.player.sendChatMessage(msg);
        }
        else if (Spammer.spamMessages.size() > 0) {
            String messageOut;
            if (this.random.getValue()) {
                final int index = Spammer.rnd.nextInt(Spammer.spamMessages.size());
                messageOut = Spammer.spamMessages.get(index);
                Spammer.spamMessages.remove(index);
            }
            else {
                messageOut = Spammer.spamMessages.get(0);
                Spammer.spamMessages.remove(0);
            }
            Spammer.spamMessages.add(messageOut);
            if (this.greentext.getValue()) {
                messageOut = "> " + messageOut;
            }
            Spammer.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(messageOut.replaceAll("§", "")));
        }
        this.timer.reset();
    }
    
    private void readSpamFile() {
        final List<String> fileInput = FileUtil.readTextFileAllLines("phobos/util/Spammer.txt");
        final Iterator<String> i = fileInput.iterator();
        Spammer.spamMessages.clear();
        while (i.hasNext()) {
            final String s = i.next();
            if (!s.replaceAll("\\s", "").isEmpty()) {
                Spammer.spamMessages.add(s);
            }
        }
        if (Spammer.spamMessages.size() == 0) {
            Spammer.spamMessages.add("gg");
        }
    }
    
    static {
        spamMessages = new ArrayList<String>();
        rnd = new Random();
    }
    
    public enum Mode
    {
        FILE, 
        PWORD;
    }
    
    public enum PwordMode
    {
        MSG, 
        EVERYONE, 
        CHAT;
    }
    
    public enum DelayType
    {
        MS, 
        DS, 
        S;
    }
}
