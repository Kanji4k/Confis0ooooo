package client.confis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;

public final class Modules {
    public static final List<Module> ALL = new ArrayList<>();

    // 1 to 16 Modules definition
    public static Module inventoryHub;
    public static Module totemCount;
    public static Module effectIcons;
    public static Module nameBadgeC;
    public static Module saturation;
    public static Module zoom;
    public static Module itemGuide;
    public static Module tierTagger;
    public static Module shulkerBoxTooltip;
    public static Module crosshairIndicator;
    public static Module customGlint;
    public static Module bactro;
    public static Module freecam;
    public static Module skin3d;
    public static Module potionCount;

    static {
        // --- Standard HUD Info Displays ---
        ALL.add(new TextModule("fps", "FPS", "HUD", 0.05f, 0.05f, mc -> "FPS: " + Hud.fps));
        ALL.add(new TextModule("ping", "Ping", "HUD", 0.05f, 0.08f, mc -> {
            int ping = -1;
            if (mc.field_1724 != null && mc.method_1562() != null) {
                var info = mc.method_1562().method_2871(mc.field_1724.getUuid());
                if (info != null) ping = info.method_2959();
            }
            return "Ping: " + (ping < 0 ? "--" : ping + " ms");
        }));
        ALL.add(new TextModule("tps", "Server TPS", "HUD", 0.05f, 0.11f, mc -> {
            double t = TpsTracker.get();
            return String.format("TPS: %.1f", t);
        }));
        ALL.add(new TextModule("cps", "CPS", "HUD", 0.05f, 0.14f, mc -> "CPS: " + Cps.left() + " | " + Cps.right()));

        // --- 1 to 16 Features Integrated ---
        ALL.add(totemCount = new TextModule("totem_count", "Totem Count", "HUD", 0.05f, 0.17f, mc -> "Totems: " + getTotemCount(mc)));
        ALL.add(potionCount = new TextModule("potion_count", "Potion Count", "HUD", 0.05f, 0.20f, mc -> "Potions: " + getPotionCount(mc)));
        ALL.add(saturation = new TextModule("saturation", "Saturation", "HUD", 0.05f, 0.23f, mc -> {
            if (mc.field_1724 != null) {
                return String.format("Sat: %.1f", mc.field_1724.getHungerManager().getSaturationLevel());
            }
            return "Sat: 0.0";
        }));

        ALL.add(inventoryHub = new Module("inventory_hub", "Inventory Hub", "HUD", 0.05f, 0.26f));
        ALL.add(effectIcons = new Module("effect_icons", "Effect Timer Icons", "HUD", 0.0f, 0.0f));
        ALL.add(nameBadgeC = new Module("name_badge_c", "Name Badge C", "HUD", 0.0f, 0.0f));
        ALL.add(zoom = new Module("zoom", "Zoom", "HUD", 0.0f, 0.0f));
        ALL.add(itemGuide = new Module("item_guide", "Item Guide", "HUD", 0.0f, 0.0f));
        ALL.add(tierTagger = new Module("tier_tagger", "Tier Tagger", "HUD", 0.0f, 0.0f));
        ALL.add(shulkerBoxTooltip = new Module("shulker_box_tooltip", "Shulker Box Tooltip", "HUD", 0.0f, 0.0f));
        ALL.add(crosshairIndicator = new Module("crosshair_indicator", "Crosshair Indicator", "HUD", 0.0f, 0.0f));
        ALL.add(customGlint = new Module("custom_glint", "Custom Glint", "HUD", 0.0f, 0.0f));
        ALL.add(bactro = new Module("bactro", "Bactro / Flight", "HUD", 0.0f, 0.0f));
        ALL.add(freecam = new Module("freecam", "Freecam", "HUD", 0.0f, 0.0f));
        ALL.add(skin3d = new Module("3d_skin", "3D Skin", "HUD", 0.0f, 0.0f));

        // --- Other Custom Modules ---
        ALL.add(new ArmorModule());
        ALL.add(new KeystrokesModule());
        ALL.add(new MinimapModule());
        ALL.add(new WaypointsModule());
        ALL.add(new ExtraModules.HitboxModule());
        ALL.add(new ExtraModules.ItemScaleModule());
        ALL.add(new ExtraModules.DropScaleModule());
        ALL.add(new ExtraModules.EffectTimerModule());
        ALL.add(new ExtraModules.ScoreboardModule());
        ALL.add(new ExtraModules.ReachModule());
        ALL.add(new ExtraModules.RangeModule());
    }

    private static int getTotemCount(class_310 mc) {
        if (mc.field_1724 == null) return 0;
        int count = 0;
        for (class_1799 item : mc.field_1724.getInventory().main) {
            if (item.method_7848().toString().contains("totem_of_undying")) {
                count += item.method_7947();
            }
        }
        if (mc.field_1724.getOffHandStack().method_7848().toString().contains("totem_of_undying")) {
            count += mc.field_1724.getOffHandStack().method_7947();
        }
        return count;
    }

    private static int getPotionCount(class_310 mc) {
        if (mc.field_1724 == null) return 0;
        int count = 0;
        for (class_1799 item : mc.field_1724.getInventory().main) {
            if (item.method_7848().toString().contains("potion")) {
                count += item.method_7947();
            }
        }
        return count;
    }

    private Modules() {}

    public static Module byId(String id) {
        for (Module m : ALL) if (m.id.equals(id)) return m;
        return null;
    }

    // --- Base Text Module Renderer ---
    public static class TextModule extends Module {
        private final Function<class_310, String> text;

        public TextModule(String id, String name, String cat, double x, double y, Function<class_310, String> text) {
            super(id, name, cat, x, y);
            this.text = text;
        }

        @Override public int width(class_310 mc) { return mc.field_1772.method_1727(text.apply(mc)) + 4; }
        @Override public int height(class_310 mc) { return 12; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            String s = text.apply(mc);
            int w = mc.field_1772.method_1727(s) + 4;
            if (c.drawBg) g.method_25294(x, y, x + w, y + 12, c.bg);
            g.method_27535(mc.field_1772, s, x + 2, y + 2, c.text, true);
        }
    }

    // --- Armor Module Renderer ---
    public static static class ArmorModule extends Module {
        private static final class_1304[] SLOTS = {class_1304.field_6174, class_1304.field_6172, class_1304.field_6166, class_1304.field_6173};

        ArmorModule() { super("armor", "Armor Status", "HUD", 0.05f, 0.90f); }

        @Override public int width(class_310 mc) { return 18; }
        @Override public int height(class_310 mc) { return SLOTS.length * 19 - 2; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (mc.field_1724 == null) return;
            int row = 0;
            for (class_1304 slot : SLOTS) {
                int ry = y + row * 19;
                row++;
                class_1799 stack = mc.field_1724.method_6118(slot);
                if (stack.method_7960()) continue;
                if (c.drawBg) g.method_25294(x, ry, x + 18, ry + 17, c.bg);
                g.method_51427(stack, x + 1, ry);
            }
        }
    }

    // --- Keystrokes Module Renderer ---
    public static class KeystrokesModule extends Module {
        public KeystrokesModule() {
            super("keystrokes", "Keystrokes", "HUD", 0.85f, 0.85f);
            defBg = 0x88000000;
            defAccent = 0xffffffff;
            accentLabel = "Pressed key color";
        }

        @Override public int width(class_310 mc) { return 78; }
        @Override public int height(class_310 mc) { return 82; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            // Draws WASD + Mouse Keystrokes
        }
    }

    // --- Minimap Module Renderer ---
    public static class MinimapModule extends Module {
        public MinimapModule() {
            super("minimap", "Minimap", "HUD", 0.88f, 0.05f);
            defBg = 0x88000000;
            defAccent = 0xffff0000;
            accentLabel = "Facing marker color";
        }

        @Override public int width(class_310 mc) { return Minimap.SIZE + 4; }
        @Override public int height(class_310 mc) { return Minimap.SIZE + 14; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            Minimap.render(g, mc, x, y);
        }
    }

    // --- Waypoints Module ---
    public static class WaypointsModule extends Module {
        public WaypointsModule() { super("waypoints", "Waypoints", "HUD", 0, 0); defBg = 0x00000000; }
        @Override public boolean movable() { return false; }
        @Override public int width(class_310 mc) { return 0; }
        @Override public int height(class_310 mc) { return 0; }
        @Override public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {}
    }
}
