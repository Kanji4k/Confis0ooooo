package client.confis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1293;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_8646;
import net.minecraft.class_9011;

/** New HUD mods: Effect Timer and a fully custom Scoreboard. Both get colors, background and scale. */
public final class ExtraModules {
    private ExtraModules() {}

    private static String roman(int n) {
        String[] r = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return n >= 1 && n <= 10 ? r[n] : String.valueOf(n);
    }

    // ------------------------------------------------------------ effect timer
    public static class EffectTimerModule extends Module {
        private record Row(String name, String time, boolean low) {}

        public EffectTimerModule() {
            super("effects", "Effect Timer", "HUD", 0.010, 0.300);
            defAccent = 0xFFFF5555;
            accentLabel = "Expiring (under 10s) color";
        }

        private List<Row> rows(class_310 mc) {
            List<Row> out = new ArrayList<>();
            if (mc.field_1724 != null) {
                List<class_1293> list = new ArrayList<>(mc.field_1724.method_6026());
                list.sort(Comparator.comparingInt(class_1293::method_5584).reversed());
                for (class_1293 e : list) {
                    String name = e.method_5579().comp_349().method_5560().getString() + " " + roman(e.method_5578() + 1);
                    int secs = Math.max(0, e.method_5584() / 20);
                    String t = e.method_48559() ? "inf" : (secs / 60) + ":" + String.format("%02d", secs % 60);
                    out.add(new Row(name, t, !e.method_48559() && secs < 10));
                }
            }
            if (out.isEmpty() && mc.field_1755 instanceof ConfigScreen) {        // placeholder while editing the HUD
                out.add(new Row("Speed II", "1:30", false));
                out.add(new Row("Regeneration I", "0:08", true));
            }
            return out;
        }

        @Override
        public int width(class_310 mc) {
            int w = 70;
            for (Row r : rows(mc)) w = Math.max(w, mc.field_1772.method_1727(r.name()) + mc.field_1772.method_1727(r.time()) + 18);
            return w;
        }

        @Override public int height(class_310 mc) { return Math.max(1, rows(mc).size()) * 12 + 1; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            List<Row> rows = rows(mc);
            if (rows.isEmpty()) return;
            int w = width(mc);
            boolean blink = (System.currentTimeMillis() / 400) % 2 == 0;
            for (int i = 0; i < rows.size(); i++) {
                Row r = rows.get(i);
                int ry = y + i * 12;
                if (c.showBg) g.method_25294(x, ry, x + w, ry + 11, c.bg);
                g.method_51433(mc.field_1772, r.name(), x + 4, ry + 2, c.text, true);
                int col = r.low() && blink ? c.accent : c.text;
                g.method_51433(mc.field_1772, r.time(), x + w - 4 - mc.field_1772.method_1727(r.time()), ry + 2, col, true);
            }
        }
    }

    // -------------------------------------------------------------- scoreboard
    public static class ScoreboardModule extends Module {
        private record Line(class_2561 name, String score) {}
        private class_2561 title = class_2561.method_43473();

        public ScoreboardModule() {
            super("scoreboard", "Scoreboard", "HUD", 0.850, 0.350);
            defAccent = 0xFFFF5555;
            accentLabel = "Score number color";
        }

        private List<Line> lines(class_310 mc) {
            List<Line> out = new ArrayList<>();
            if (mc.field_1687 != null) {
                class_269 sb = mc.field_1687.method_8428();
                class_266 obj = sb.method_1189(class_8646.field_45157);
                if (obj != null) {
                    title = obj.method_1114();
                    List<class_9011> entries = new ArrayList<>(sb.method_1184(obj));
                    entries.removeIf(e -> e.comp_2127().startsWith("#"));
                    entries.sort(Comparator.comparingInt(class_9011::comp_2128).reversed());
                    for (class_9011 e : entries.subList(0, Math.min(15, entries.size()))) {
                        class_2561 n = class_268.method_1142(sb.method_1164(e.comp_2127()), e.method_55387());
                        out.add(new Line(n, String.valueOf(e.comp_2128())));
                    }
                }
            }
            if (out.isEmpty() && mc.field_1755 instanceof ConfigScreen) {
                title = class_2561.method_43470("Scoreboard");
                out.add(new Line(class_2561.method_43470("Kills"), "12"));
                out.add(new Line(class_2561.method_43470("Coins"), "340"));
            }
            return out;
        }

        @Override
        public int width(class_310 mc) {
            List<Line> l = lines(mc);
            if (l.isEmpty()) return 60;
            int w = mc.field_1772.method_27525(title);
            for (Line r : l) w = Math.max(w, mc.field_1772.method_27525(r.name()) + mc.field_1772.method_1727(r.score()) + 14);
            return w + 8;
        }

        @Override public int height(class_310 mc) { return lines(mc).isEmpty() ? 13 : (lines(mc).size() + 1) * 10 + 4; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            List<Line> l = lines(mc);
            if (l.isEmpty()) return;
            int w = width(mc), h = height(mc);
            if (c.showBg) g.method_25294(x, y, x + w, y + h, c.bg);
            g.method_51439(mc.field_1772, title, x + (w - mc.field_1772.method_27525(title)) / 2, y + 3, c.text, true);
            for (int i = 0; i < l.size(); i++) {
                int ry = y + 13 + i * 10;
                g.method_51439(mc.field_1772, l.get(i).name(), x + 4, ry, c.text, true);
                g.method_51433(mc.field_1772, l.get(i).score(), x + w - 4 - mc.field_1772.method_1727(l.get(i).score()), ry, c.accent, true);
            }
        }
    }

    // ------------------------------------------------------------ reach display
    public static class ReachModule extends Module {
        public ReachModule() { super("reach", "Reach Display", "INFO", 0.010, 0.255); }
        private String text() {
            double r = ReachTracker.last();
            return "Reach: " + (r < 0 ? "--" : String.format("%.2f", r) + " blocks");
        }
        @Override public int width(class_310 mc) { return mc.field_1772.method_1727(text()) + 8; }
        @Override public int height(class_310 mc) { return 13; }
        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (c.showBg) g.method_25294(x, y, x + width(mc), y + 13, c.bg);
            g.method_51433(mc.field_1772, text(), x + 4, y + 3, c.text, true);
        }
    }

    // -------------------------------------------------------------- range display
    /** Shows the distance to whatever you are aiming at and whether it is inside your attack reach. */
    public static class RangeModule extends Module {
        public RangeModule() {
            super("range", "Hit Range", "INFO", 0.010, 0.295);
            defAccent = 0xFF55FF55;
            accentLabel = "In-range color";
        }
        private double dist(class_310 mc) {
            if (mc.field_1724 == null || mc.field_1692 == null) return -1;
            var b = mc.field_1692.method_5829();
            var eye = mc.field_1724.method_33571();
            var p = new net.minecraft.class_243(Math.max(b.field_1323, Math.min(eye.field_1352, b.field_1320)),
                    Math.max(b.field_1322, Math.min(eye.field_1351, b.field_1325)), Math.max(b.field_1321, Math.min(eye.field_1350, b.field_1324)));
            return eye.method_1022(p);
        }
        private String text(class_310 mc) {
            double d = dist(mc);
            double max = mc.field_1724 == null ? 3.0 : mc.field_1724.method_55755();
            return d < 0 ? "Range: -- / " + String.format("%.1f", max) : "Range: " + String.format("%.2f", d) + " / " + String.format("%.1f", max);
        }
        @Override public int width(class_310 mc) { return mc.field_1772.method_1727(text(mc)) + 8; }
        @Override public int height(class_310 mc) { return 13; }
        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            double d = dist(mc);
            boolean in = d >= 0 && mc.field_1724 != null && d <= mc.field_1724.method_55755();
            if (c.showBg) g.method_25294(x, y, x + width(mc), y + 13, c.bg);
            g.method_51433(mc.field_1772, text(mc), x + 4, y + 3, in ? c.accent : c.text, true);
        }
    }
}
