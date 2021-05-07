// 
// Decompiled by Procyon v0.5.36
// 

package me.earth.phobos.features.modules.client;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import java.util.Map;
import me.earth.phobos.features.modules.Module;

public class Capes extends Module
{
    public static Map<String, String[]> UUIDs;
    public static final ResourceLocation THREEVT_CAPE;
    public static final ResourceLocation ZBOB_CAPE;
    private static Capes instance;
    
    public Capes() {
        super("Capes", "Renders the client's capes", Category.CLIENT, false, false, false);
        Capes.UUIDs.put("Megyn", new String[] { "a5e36d37-5fbe-4481-b5be-1f06baee1f1c", "7de842e8-af08-49ed-9d0c-4071e2a99f00", "8ca55379-c872-4299-987d-d20962badd11", "e6e8bf7e-0b23-4d2e-b2ae-c40c5ff4eecc" });
        Capes.UUIDs.put("zb0b", new String[] { "0aa3b04f-786a-49c8-bea9-025ee0dd1e85" });
        Capes.UUIDs.put("3vt", new String[] { "19bf3f1f-fe06-4c86-bea5-3dad5df89714", "b0836db9-2472-4ba6-a1b7-92c605f5e80d" });
        Capes.UUIDs.put("oHare", new String[] { "453e38dd-f4a9-481f-8ebd-8339e89e5445" });
        Capes.instance = this;
    }
    
    public static Capes getInstance() {
        if (Capes.instance == null) {
            Capes.instance = new Capes();
        }
        return Capes.instance;
    }
    
    public static ResourceLocation getCapeResource(final AbstractClientPlayer player) {
        for (final String name : Capes.UUIDs.keySet()) {
            for (final String uuid : Capes.UUIDs.get(name)) {
                if (name.equalsIgnoreCase("3vt") && player.getUniqueID().equals(UUID.fromString(formatUUID(uuid)))) {
                    return Capes.THREEVT_CAPE;
                }
                if (name.equalsIgnoreCase("Megyn") && formatUUID(player.getUniqueID().toString()).equals(formatUUID(uuid))) {
                    return Capes.ZBOB_CAPE;
                }
            }
        }
        return Capes.ZBOB_CAPE;
    }
    
    public static boolean hasCape(final UUID uuid) {
        final Iterator<String> iterator = Capes.UUIDs.keySet().iterator();
        if (iterator.hasNext()) {
            final String name = iterator.next();
            return Arrays.asList((String[])Capes.UUIDs.get(name)).contains(uuid.toString());
        }
        return false;
    }
    
    public static String formatUUID(final String input) {
        return input.replace("-", "").toLowerCase();
    }
    
    static {
        Capes.UUIDs = new HashMap<String, String[]>();
        THREEVT_CAPE = new ResourceLocation("textures/3vt2.png");
        ZBOB_CAPE = new ResourceLocation("textures/zb0b.png");
    }
}
