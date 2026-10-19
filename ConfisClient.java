package client.confis;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.class_2960;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ConfisClient implements ClientModInitializer {
    public static final String MOD_ID = "confis0oooooo";
    public static final String NAME = "Confis0oooooo";

    public static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        Config.load();

        // Register Right Shift keybinding
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.confis.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.confis.general"
        ));

        // Tick event to open via Right Shift
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                while (menuKey.wasPressed()) {
                    client.setScreen(new ConfisScreen());
                }
            }
        });

        // Fallback: Type /confis in chat to open the menu on any server
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("confis").executes(context -> {
                net.minecraft.client.MinecraftClient.getInstance().execute(() -> {
                    net.minecraft.client.MinecraftClient.getInstance().setScreen(new ConfisScreen());
                });
                return Command.SINGLE_SUCCESS;
            }));
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                class_2960.method_60655(MOD_ID, "hud"),
                (graphics, deltaTracker) -> Hud.render(graphics)
        );

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> TpsTracker.reset());
        MessengerLog.register();
        ReachTracker.register();
        HitboxRenderer.register();

        HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD,
                original -> (graphics, deltaTracker) -> {
                    if (!Modules.byId("scoreboard").cfg().enabled) original.render(graphics, deltaTracker);
                });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());
    }
}
