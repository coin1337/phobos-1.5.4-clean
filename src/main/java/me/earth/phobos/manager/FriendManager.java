// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.manager;

import me.earth.phobos.util.PlayerUtil;
import java.util.Iterator;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import me.earth.phobos.features.Feature;

public class FriendManager extends Feature
{
    private final Map<String, UUID> friends;
    
    public FriendManager() {
        super("Friends");
        this.friends = new HashMap<String, UUID>();
    }
    
    public boolean isFriend(final String name) {
        return this.friends.get(name) != null;
    }
    
    public boolean isFriend(final EntityPlayer player) {
        return this.isFriend(player.getName());
    }
    
    public void addFriend(final String name) {
        final Friend friend = this.getFriendByName(name);
        if (friend != null) {
            this.friends.put(friend.getUsername(), friend.getUuid());
        }
    }
    
    public void removeFriend(final String name) {
        this.friends.remove(name);
    }
    
    public void onLoad() {
        this.friends.clear();
        this.clearSettings();
    }
    
    public void saveFriends() {
        this.clearSettings();
        for (final Map.Entry<String, UUID> entry : this.friends.entrySet()) {
            this.register(new Setting(entry.getValue().toString(), entry.getKey()));
        }
    }
    
    public Map<String, UUID> getFriends() {
        return this.friends;
    }
    
    public Friend getFriendByName(final String input) {
        final UUID uuid = PlayerUtil.getUUIDFromName(input);
        if (uuid != null) {
            return new Friend(input, uuid);
        }
        return null;
    }
    
    public void addFriend(final Friend friend) {
        this.friends.put(friend.getUsername(), friend.getUuid());
    }
    
    public static class Friend
    {
        private final String username;
        private final UUID uuid;
        
        public Friend(final String username, final UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }
        
        public String getUsername() {
            return this.username;
        }
        
        public UUID getUuid() {
            return this.uuid;
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof Friend && ((Friend)other).getUsername().equals(this.username) && ((Friend)other).getUuid().equals(this.uuid);
        }
        
        @Override
        public int hashCode() {
            return this.username.hashCode() + this.uuid.hashCode();
        }
    }
}
