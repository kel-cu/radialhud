package anticope.radialhud;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.option.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.MinecraftClient;
import java.lang.RuntimeException;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;

@Mod("radialhud")
public class RadialHud {
    public static final KeyBinding openHudKey = new KeyBinding(
        "key.radialhud.open",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_ALT,
        "category.radialhud.cat"
    );

    static {
        KeyMappingRegistry.register(openHudKey);
    }

    public RadialHud() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER)
            throw new RuntimeException("Client-side only.");
        MinecraftForge.EVENT_BUS.register(RadialHud.class);
    }


    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key e) {
        var mc = MinecraftClient.getInstance();
        if (openHudKey.isPressed() && mc.currentScreen == null) {
            mc.setScreen(new RadialScreen(openHudKey));
        }
    }
}
