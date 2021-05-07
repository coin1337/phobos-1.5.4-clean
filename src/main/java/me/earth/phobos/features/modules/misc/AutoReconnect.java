// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.util.MathUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import me.earth.phobos.util.Timer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraftforge.client.event.GuiOpenEvent;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.multiplayer.ServerData;
import me.earth.phobos.features.modules.Module;

public class AutoReconnect extends Module
{
    private static ServerData serverData;
    private static AutoReconnect INSTANCE;
    private final Setting<Integer> delay;
    
    public AutoReconnect() {
        super("AutoReconnect", "Reconnects you if you disconnect.", Category.MISC, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 5));
        this.setInstance();
    }
    
    private void setInstance() {
        AutoReconnect.INSTANCE = this;
    }
    
    public static AutoReconnect getInstance() {
        if (AutoReconnect.INSTANCE == null) {
            AutoReconnect.INSTANCE = new AutoReconnect();
        }
        return AutoReconnect.INSTANCE;
    }
    
    @SubscribeEvent
    public void sendPacket(final GuiOpenEvent event) {
        if (event.getGui() instanceof GuiDisconnected) {
            this.updateLastConnectedServer();
            if (AutoLog.getInstance().isOff()) {
                final GuiDisconnected disconnected = (GuiDisconnected)event.getGui();
                event.setGui((GuiScreen)new GuiDisconnectedHook(disconnected));
            }
        }
    }
    
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        this.updateLastConnectedServer();
    }
    
    public void updateLastConnectedServer() {
        final ServerData data = AutoReconnect.mc.getCurrentServerData();
        if (data != null) {
            AutoReconnect.serverData = data;
        }
    }
    
    static {
        AutoReconnect.INSTANCE = new AutoReconnect();
    }
    
    private class GuiDisconnectedHook extends GuiDisconnected
    {
        private final Timer timer;
        
        public GuiDisconnectedHook(final GuiDisconnected disconnected) {
            super(null,null,null);//(disconnected.parentScreen, disconnected.reason, disconnected.message);
            (this.timer = new Timer()).reset();
        }
        
        public void updateScreen() {
            if (this.timer.passedS(AutoReconnect.this.delay.getValue())) {
                //this.mc.displayGuiScreen((GuiScreen)new GuiConnecting(this.parentScreen, this.mc, (AutoReconnect.serverData == null) ? this.mc.currentServerData : AutoReconnect.serverData));
            }
        }
        
        public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            final String s = "Reconnecting in " + MathUtil.round((AutoReconnect.this.delay.getValue() * 1000 - this.timer.getPassedTimeMs()) / 1000.0, 1);
            AutoReconnect.this.renderer.drawString(s, (float)(this.width / 2 - AutoReconnect.this.renderer.getStringWidth(s) / 2), (float)(this.height - 16), 16777215, true);
        }
    }
}
