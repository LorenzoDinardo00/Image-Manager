package model;
import java.awt.image.BufferedImage;

public class Image {
    private BufferedImage bufferedImage;

    public Image(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public int getWidth() {
        return bufferedImage != null ? bufferedImage.getWidth() : 0;
    }

    public int getHeight() {
        return bufferedImage != null ? bufferedImage.getHeight() : 0;
    }
}