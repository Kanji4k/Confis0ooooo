package client.confis;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import net.minecraft.class_640;
import net.minecraft.class_7532;

/** Contacts on the left (online players + people you talked to), conversation on the right. */
public class MessengerScreen extends ConfisScreen {
    private static String selected;
    private final class_342 input;
    private final KeyPoll enter = KeyPoll.enter();
    private int page;
    private List<String> contacts = new ArrayList<>();

    public MessengerScreen(class_437 parent, String preselect) {
        super("Messenger", parent);
        if (preselect != null) selected = preselect;
        input = new class_342(mc.field_1772, 0, 0, 100, 14, class_2561.method_43470("Message"));
        input.method_1880(200);
        input.method_47404(class_2561.method_43470("Type a message..."));
    }

    @Override protected int panelW() { return 460; }
    @Override protected int panelH() { return 270; }

    private void refreshContacts() {
        Set<String> names = new LinkedHashSet<>(MessengerLog.CHATS.keySet());
        if (mc.method_1562() != null)
            for (class_640 p : mc.method_1562().method_2880()) {
                String n = p.method_2966().name();
                if (mc.field_1724 == null || !n.equals(mc.field_1724.method_7334().name())) names.add(n);
            }
        contacts = new ArrayList<>(names);
    }

    private int perPage() { return Math.max(1, (ph - 60) / 18); }

    @Override
    protected void build() {
        refreshContacts();
        int per = perPage();
        int pages = Math.max(1, (contacts.size() + per - 1) / per);
        page = Math.min(page, pages - 1);
        for (int n = 0; n < per && page * per + n < contacts.size(); n++) {
            String name = contacts.get(page * per + n);
            btn("", px + 8, py + 32 + n * 18, 130, 16, () -> {
                selected = name;
                MessengerLog.UNREAD.remove(name);
                markDirty();
            });
        }
        btn("<", px + 8, py + ph - 20, 18, 14, () -> { page = Math.max(0, page - 1); markDirty(); });
        btn(">", px + 120, py + ph - 20, 18, 14, () -> { page = Math.min(pages - 1, page + 1); markDirty(); });

        input.method_46421(px + 146); input.method_46419(py + ph - 22); input.method_25358(pw - 146 - 62);
        method_37063(input);
        method_48265(input);
        btn("SEND", px + pw - 52, py + ph - 23, 44, 16, this::send);
    }

    private void send() {
        if (selected == null) return;
        MessengerLog.send(selected, input.method_1882());
        input.method_1852("");
    }

    @Override
    public void method_25393() {
        super.method_25393();
        if (enter.pressed() && input.method_25370()) send();
    }

    @Override
    protected void draw(class_332 g, int mx, int my) {
        int per = perPage();
        for (int n = 0; n < per && page * per + n < contacts.size(); n++) {
            String name = contacts.get(page * per + n);
            int y = py + 32 + n * 18;
            class_640 info = findInfo(name);
            if (info != null) class_7532.method_52722(g, info.method_52810(), px + 10, y + 2, 12);
            else g.method_25294(px + 10, y + 2, px + 22, y + 14, 0xFF444444);
            int unread = MessengerLog.UNREAD.getOrDefault(name, 0);
            boolean sel = name.equals(selected);
            g.method_51433(field_22793, name + (unread > 0 ? " (" + unread + ")" : ""), px + 26, y + 4,
                    sel ? accent() : unread > 0 ? 0xFFFFFF55 : 0xFFFFFFFF, true);
        }
        g.method_25294(px + 142, py + 28, px + 143, py + ph - 26, 0xFF3A3A3A);

        int x0 = px + 148, w = pw - 156;
        if (selected == null) {
            g.method_51433(field_22793, "Pick a player on the left.", x0, py + 34, 0xFF7A7A7A, true);
            return;
        }
        g.method_51433(field_22793, "Chat with " + selected, x0, py + 30, accent(), true);
        List<class_5481> lines = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        for (MessengerLog.Entry e : MessengerLog.CHATS.getOrDefault(selected, List.of()))
            for (class_5481 l : field_22793.method_1728(class_2561.method_43470((e.mine() ? "You: " : selected + ": ") + e.text()), w)) {
                lines.add(l);
                cols.add(e.mine() ? 0xFFB0B0B0 : 0xFFFFFFFF);
            }
        int top = py + 44, rows = Math.max(1, (ph - 44 - 30) / 10);
        for (int i = 0; i < rows && i < lines.size(); i++) {
            int idx = Math.max(0, lines.size() - rows) + i;
            if (idx >= lines.size()) break;
            g.method_51430(field_22793, lines.get(idx), x0, top + i * 10, cols.get(idx), true);
        }
    }

    private class_640 findInfo(String name) {
        if (mc.method_1562() == null) return null;
        for (class_640 p : mc.method_1562().method_2880()) if (p.method_2966().name().equals(name)) return p;
        return null;
    }
}
