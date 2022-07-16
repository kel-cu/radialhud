package anticope.radialhud;


import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class RadialHud implements ClientModInitializer {
    public static KeyBinding openHudKey;

    @Override
    public void onInitializeClient() {
        openHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.radialhud.open",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "category.radialhud.cat"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var mc = MinecraftClient.getInstance();
            if (openHudKey.isPressed() && mc.currentScreen == null) {
                mc.setScreen(new RadialScreen());
            }
        });
    }
    
}
