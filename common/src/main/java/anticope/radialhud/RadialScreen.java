package anticope.radialhud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;

public class RadialScreen extends Screen {

    private int crosshairX, crosshairY, focusedSlot, prevFocusedSlot = -1;
    private float yaw, pitch;
    private final KeyBinding key;
    private final static Identifier WIDGETS_TEXTURE = Identifier.of("textures/gui/sprites/hud/hotbar_selection.png");

    public RadialScreen(KeyBinding key) {
        super(Text.translatable("screen.radialhud.name"));
        this.key = key;
    }
    
    private void cursorMode(int mode) {
        double x = (double) (this.client.getWindow().getWidth() / 2);
        double y = (double) (this.client.getWindow().getHeight() / 2);
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), mode, x, y);
    }

    @Override
    protected void init() {
        super.init();
        cursorMode(GLFW.GLFW_CURSOR_DISABLED);
        yaw = client.player.getYaw();
        pitch = client.player.getPitch();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!key.isPressed())
            close();

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        if (focusedSlot == prevFocusedSlot) return;

        var diff = Math.abs(focusedSlot - prevFocusedSlot);
        if (diff >= PlayerInventory.getHotbarSize() - 1) diff = 1;
        var pitch = 1F + (float)diff/(float)PlayerInventory.getHotbarSize();

        client.world.playSoundFromEntity(client.player, client.player, SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.2F, pitch);
        prevFocusedSlot = focusedSlot;
    }

    @Override
    public void close() {
        cursorMode(GLFW.GLFW_CURSOR_NORMAL);
        if (focusedSlot > -1) {
            client.player.getInventory().selectedSlot = focusedSlot;
        }
        super.close();
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        matrices.drawTexture(RenderLayer::getGuiTextured, Identifier.of("textures/gui/sprites/hud/crosshair.png"), crosshairX - 8, crosshairY - 8, 0, 0, 15, 15, 15, 15);

        drawItems(matrices, (int) (Math.min(height, width) / 2 * 0.5), mouseX, mouseY);
        matrices.getMatrices().scale(2f, 2f, 1f);

        int scale = client.options.getGuiScale().getValue();
        Vector2 mouse = new Vector2(mouseX, mouseY);
        Vector2 center = new Vector2(width / 2, height / 2);
        mouse.subtract(center);
        mouse.normalize();
        Vector2 cross = mouse;

        if (scale == 0)
            scale = 4;

        if (Math.hypot(width / 2 - mouseX, height / 2 - mouseY) < 1f / scale * 200f)
            mouse.multiply((float) Math.hypot(width / 2 - mouseX, height / 2 - mouseY));
        else
            mouse.multiply(1f / scale * 200f);

        crosshairX = (int) mouse.x + width / 2;
        crosshairY = (int) mouse.y + height / 2;

        client.player.setYaw(yaw + cross.x / 3);
        client.player.setPitch(MathHelper.clamp(pitch + cross.y / 3, -90f, 90f));
    }

//    private void drawTexture(DrawContext matrices, Identifier id, int x, int y, int u, int v, int w, int h, boolean invert) {
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
//        RenderSystem.setShaderTexture(0, id);
//        RenderSystem.enableBlend();
//        if (invert)
//            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
//                    GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE,
//                    GlStateManager.DstFactor.ZERO);
//        drawTexture(matrices, x, y, u, v, w, h);
//    }

    private void drawItems(DrawContext matrix, int radius, int mouseX, int mouseY) {
        double lowestDistance = Double.MAX_VALUE;

        var inventory = client.player.getInventory();

        for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++) {
            double s = (double) slot / PlayerInventory.getHotbarSize() * 2 * Math.PI;
            int x = (int) Math.round(radius * Math.cos(s) + width / 2) - 8;
            int y = (int) Math.round(radius * Math.sin(s) + height / 2) - 8;
            
            var stack = inventory.getStack(slot);
            matrix.drawItem(stack, x, y);

            if (Math.hypot(x - mouseX, y - mouseY) < lowestDistance) {
                lowestDistance = Math.hypot(x - mouseX, y - mouseY);
                focusedSlot = slot;
            }
        }

        double s = (double) focusedSlot / PlayerInventory.getHotbarSize() * 2 * Math.PI;
        int x = (int) Math.round(radius * Math.cos(s) + width / 2) - 8 - 4;
        int y = (int) Math.round(radius * Math.sin(s) + height / 2) - 8 - 4;

        matrix.drawTexture(RenderLayer::getGuiTextured, WIDGETS_TEXTURE, x, y, 0, 0, 16 + 8, 16 + 8, 16 + 8, 16 + 8);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
