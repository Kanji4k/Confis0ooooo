package client.confis;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1569;
import net.minecraft.class_310;

/**
 * Decides which entities get a hitbox outline. The outline itself is drawn by the game's own
 * hitbox renderer (the F3+B one); HitboxDispatcherMixin switches it on just for the types enabled here.
 */
public final class HitboxRenderer {
    public static final String[] KEYS = {"crystal", "exp", "friendly", "hostile", "firework", "pearl", "windcharge"};
    public static final String[] LABELS = {"Crystal", "XP orb", "Friendly mob", "Hostile mob", "Firework", "Ender pearl", "Wind charge"};

    private HitboxRenderer() {}

    /** Kept so ConfisClient does not need to change. Nothing to register any more. */
    public static void register() {}

    public static boolean on(String key) { return Config.get().hitboxes.getOrDefault(key, false); }

    private static int typeOf(class_1297 e) {
        class_1299<?> t = e.method_5864();
        if (t == class_1299.field_6110) return 0;
        if (t == class_1299.field_6044) return 1;
        if (t == class_1299.field_6133) return 4;
        if (t == class_1299.field_6082) return 5;
        if (t == class_1299.field_47243 || t == class_1299.field_49075) return 6;
        if (e instanceof class_1569) return 3;
        if (e instanceof class_1308) return 2;
        return -1;
    }

    /** True if this entity should show its hitbox right now. */
    public static boolean wants(class_1297 e) {
        if (!Config.get().hitboxMaster) return false;
        if (e == class_310.method_1551().field_1724) return false;
        int t = typeOf(e);
        return t >= 0 && on(KEYS[t]);
    }
}
