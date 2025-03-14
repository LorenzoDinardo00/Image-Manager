package image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageManager {
    public Image loadImage(String path) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            return new Image(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}