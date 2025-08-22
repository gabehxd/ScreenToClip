/*
MIT License

Copyright (c) 2018 comp500

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package livingroom.computer.screentoclip;

import com.mojang.blaze3d.platform.NativeImage;
import livingroom.computer.screentoclip.mixin.NativeImagePointerAccessor;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.*;
import java.nio.ByteBuffer;

public class HandleImage {

    public static void init() {
        if (!Minecraft.ON_OSX) {
            // Test that the mixin was run properly
            // Ensure AWT is loaded by forcing loadLibraries() to be called, will cause a HeadlessException if someone else already loaded AWT
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard();
            } catch (HeadlessException e) {
                ScreenToClip.LOGGER.warn("java.awt.headless property was not set properly!");
            }
        }
    }


    public static void handleScreenshotAWT(NativeImage img) {
        if (Minecraft.ON_OSX) {
            return;
        }

        // Only allow RGBA
        if (img.format() != NativeImage.Format.RGBA) {
            ScreenToClip.LOGGER.warn("Failed to capture screenshot: wrong format");
            return;
        }

        // IntellIJ doesn't like this
        //noinspection ConstantConditions
        long imagePointer = ((NativeImagePointerAccessor) (Object) img).getPointer();
        ByteBuffer buf = MemoryUtil.memByteBufferSafe(imagePointer, img.getWidth() * img.getHeight() * 4);
        if (buf == null) {
            throw new RuntimeException("Invalid image");
        }

        handleScreenshotAWT(buf, img.getWidth(), img.getHeight(), 4);
    }

    private static void handleScreenshotAWT(ByteBuffer byteBuffer, int width, int height, int components) {
        if (Minecraft.ON_OSX) {
            return;
        }

        byte[] array;
        if (byteBuffer.hasArray()) {
            array = byteBuffer.array();
        } else {
            // can't use .array() as the buffer is not array-backed
            array = new byte[height * width * components];
            byteBuffer.get(array);
        }

        doCopy(array, width, height, components);
    }

    private static void doCopy(byte[] imageData, int width, int height, int components) {
        new Thread(() -> {
            DataBufferByte buf = new DataBufferByte(imageData, imageData.length);
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            // Ignore the alpha channel, due to JDK-8204187
            int[] nBits = {8, 8, 8};
            int[] bOffs = {0, 1, 2}; // is this efficient, no transformation is being done?
            ColorModel cm = new ComponentColorModel(cs, nBits, false, false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE);
            BufferedImage bufImg = new BufferedImage(cm, Raster.createInterleavedRaster(buf,
                    width, height,
                    width * components, components,
                    bOffs, null), false, null);

            Transferable trans = getTransferableImage(bufImg);
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents(trans, null);
        }, "Screenshot to Clipboard Copy").start();
    }

    private static Transferable getTransferableImage(final BufferedImage bufferedImage) {
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            @Override
            public @NotNull Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (DataFlavor.imageFlavor.equals(flavor)) {
                    return bufferedImage;
                }
                throw new UnsupportedFlavorException(flavor);
            }
        };
    }
}
