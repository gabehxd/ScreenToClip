package computer.livingroom.screentoclip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenToClip {
    public static final String MOD_ID = "screentoclip";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        HandleImage.init();
        LOGGER.info("Loaded ScreenToClip");
    }
}
