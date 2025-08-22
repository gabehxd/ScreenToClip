package computer.livingroom.screentoclip.neoforge;

import computer.livingroom.screentoclip.HandleImage;
import computer.livingroom.screentoclip.ScreenToClip;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenshotEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ScreenToClip.MOD_ID)
public final class ScreenToClipNeoForge {
    public ScreenToClipNeoForge() {
        // Run our common setup.
        NeoForge.EVENT_BUS.addListener(false, ScreenshotEvent.class, event -> HandleImage.handleScreenshotAWT(event.getImage()));
        ScreenToClip.init();
    }
}
