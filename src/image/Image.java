package image;

import java.awt.image.BufferedImage;

public class Image {
    private BufferedImage bufferedImage;

    public Image(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    // Getter e setter per bufferedImage, larghezza, altezza, etc.
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getWidth() {
        return bufferedImage.getWidth();
    }

    public int getHeight() {
        return bufferedImage.getHeight();
    }


}