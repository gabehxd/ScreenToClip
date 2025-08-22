package livingroom.computer.screentoclip.neoforge;

import livingroom.computer.screentoclip.HandleImage;
import livingroom.computer.screentoclip.ScreenToClip;
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
