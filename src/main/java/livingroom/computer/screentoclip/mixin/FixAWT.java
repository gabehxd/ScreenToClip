package livingroom.computer.screentoclip.mixin;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class FixAWT {
    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void awtFix(String[] strings, CallbackInfo ci) {
        //this must be set to false in order to access the clipboard
        System.setProperty("java.awt.headless", "false");
    }
}
