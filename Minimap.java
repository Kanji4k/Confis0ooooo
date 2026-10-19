package client.confis;

import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2902;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3620;
import net.minecraft.class_638;

public final class Minimap {
    private static final int N = 40;      // cells per side
    private static final int CELL = 2;    // pixels per cell
    public static final int SIZE = N * CELL;
    private static final int UNKNOWN = 0xFF101010;

    private static final int[] colors = new int[N * N];
    private static long lastUpdate;
    private static final class_2338.class_2339 POS = new class_2338.class_2339();

    private Minimap() {}

    /** Draws the map with its top-left corner at (x0, y0). Total size: SIZE+4 wide, SIZE+16 tall. */
    public static void render(class_332 g, class_310 mc, Config.ModuleCfg c, int x0, int y0) {
        if (mc.field_1687 == null || mc.field_1724 == null) return;
        Config cfg = Config.get();
        long now = System.currentTimeMillis();
        int zoom = Math.max(1, Math.min(4, cfg.mapZoom));
        if (now - lastUpdate > 250) {
            update(mc.field_1687, mc.field_1724.method_31477(), mc.field_1724.method_31478(), mc.field_1724.method_31479(), zoom);
            lastUpdate = now;
        }

        if (c.showBg) g.method_25294(x0, y0, x0 + SIZE + 4, y0 + SIZE + 4, c.bg);
        int x = x0 + 2, y = y0 + 2;

        for (int j = 0; j < N; j++) {
            int i = 0;
            while (i < N) {
                int col = colors[j * N + i];
                int k = i + 1;
                while (k < N && colors[j * N + k] == col) k++;
                g.method_25294(x + i * CELL, y + j * CELL, x + k * CELL, y + (j + 1) * CELL, col);
                i = k;
            }
        }

        String dim = mc.field_1687.method_27983().toString();
        double px = mc.field_1724.method_23317(), pz = mc.field_1724.method_23321();
        for (Waypoint w : cfg.waypointList) {
            if (!dim.equals(w.dim)) continue;
            double cx = (w.x + 0.5 - px) / zoom, cz = (w.z + 0.5 - pz) / zoom;
            cx = Math.max(-N / 2 + 1, Math.min(N / 2 - 1, cx));
            cz = Math.max(-N / 2 + 1, Math.min(N / 2 - 1, cz));
            int sx = x + SIZE / 2 + (int) (cx * CELL);
            int sy = y + SIZE / 2 + (int) (cz * CELL);
            g.method_25294(sx - 2, sy - 2, sx + 3, sy + 3, 0xFF000000);
            g.method_25294(sx - 1, sy - 1, sx + 2, sy + 2, 0xFF000000 | w.color);
        }

        int mx = x + SIZE / 2, my = y + SIZE / 2;
        g.method_25294(mx - 1, my - 1, mx + 2, my + 2, 0xFFFFFFFF);
        double yaw = Math.toRadians(mc.field_1724.method_36454());
        int tx = mx + (int) Math.round(-Math.sin(yaw) * 5);
        int ty = my + (int) Math.round(Math.cos(yaw) * 5);
        g.method_25294(tx, ty, tx + 2, ty + 2, c.accent);

        g.method_51433(mc.field_1772, "N", x + SIZE / 2 - 2, y + 1, c.text, true);
        String coords = mc.field_1724.method_31477() + ", " + mc.field_1724.method_31478() + ", " + mc.field_1724.method_31479();
        g.method_51433(mc.field_1772, coords, x0 + SIZE + 4 - mc.field_1772.method_1727(coords), y0 + SIZE + 6, c.text, true);
    }

    private static void update(class_638 level, int px, int py, int pz, int zoom) {
        boolean ceiling = level.method_8597().comp_643();
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                int wx = px + (i - N / 2) * zoom;
                int wz = pz + (j - N / 2) * zoom;
                colors[j * N + i] = sample(level, wx, wz, py, zoom, ceiling);
            }
        }
    }

    private static int sample(class_638 level, int wx, int wz, int py, int zoom, boolean ceiling) {
        if (!level.method_8393(wx >> 4, wz >> 4)) return UNKNOWN;
        int y = surfaceY(level, wx, wz, py, ceiling);
        if (y == Integer.MIN_VALUE) return UNKNOWN;

        POS.method_10103(wx, y, wz);
        class_2680 state = level.method_8320(POS);
        class_3620 mc = state.method_26205(level, POS);
        int rgb = (mc == class_3620.field_16008) ? 0x101010 : mc.field_16011;

        double shade = 1.0;
        if (level.method_8393(wx >> 4, (wz - zoom) >> 4)) {
            int yn = surfaceY(level, wx, wz - zoom, py, ceiling);
            if (yn != Integer.MIN_VALUE) {
                if (y > yn) shade = 1.18;
                else if (y < yn) shade = 0.78;
            }
        }
        int r = Math.min(255, (int) (((rgb >> 16) & 0xFF) * shade));
        int gr = Math.min(255, (int) (((rgb >> 8) & 0xFF) * shade));
        int b = Math.min(255, (int) ((rgb & 0xFF) * shade));
        return 0xFF000000 | (r << 16) | (gr << 8) | b;
    }

    private static int surfaceY(class_638 level, int x, int z, int py, boolean ceiling) {
        if (!ceiling) {
            int y = level.method_8624(class_2902.class_2903.field_13202, x, z) - 1;
            return y <= level.method_31607() ? Integer.MIN_VALUE : y;
        }
        class_2338.class_2339 p = new class_2338.class_2339();
        for (int y = py + 2; y > py - 48 && y > level.method_31607(); y--) {
            p.method_10103(x, y, z);
            if (!level.method_8320(p).method_26215()) {
                p.method_10103(x, y + 1, z);
                if (level.method_8320(p).method_26215()) return y;
            }
        }
        return Integer.MIN_VALUE;
    }
}
