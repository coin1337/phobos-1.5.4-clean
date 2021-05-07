// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.manager;

import java.util.concurrent.TimeUnit;
import me.earth.phobos.features.modules.client.Managers;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledExecutorService;
import me.earth.phobos.util.Timer;
import me.earth.phobos.features.Feature;

public class SafetyManager extends Feature implements Runnable
{
    private final Timer syncTimer;
    private ScheduledExecutorService service;
    private final AtomicBoolean SAFE;
    
    public SafetyManager() {
        this.syncTimer = new Timer();
        this.SAFE = new AtomicBoolean(false);
    }
    
    @Override
    public void run() {
    }
    
    public void onUpdate() {
        this.run();
    }
    
    public String getSafetyString() {
        if (this.SAFE.get()) {
            return "§aSecure";
        }
        return "§cUnsafe";
    }
    
    public boolean isSafe() {
        return this.SAFE.get();
    }
    
    public ScheduledExecutorService getService() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, 0L, Managers.getInstance().safetyCheck.getValue(), TimeUnit.MILLISECONDS);
        return service;
    }
}
