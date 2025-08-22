package computer.livingroom.screentoclip.fabric.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import computer.livingroom.screentoclip.HandleImage;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
    @Inject(method = "method_22691", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;close()V"))
    private static void getScreenshot(NativeImage nativeImage, File file, Consumer<Component> consumer, CallbackInfo ci) {
        HandleImage.handleScreenshotAWT(nativeImage);
    }
}
