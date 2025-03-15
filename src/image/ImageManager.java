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

    public boolean saveImage(Image image, String folderPath) {
        if (image == null) {
            System.err.println("No image to save.");
            return false;
        }
        BufferedImage bufferedImage = image.getBufferedImage();
        File dir = new File(folderPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Invalid directory: " + folderPath);
            return false;
        }
        File outputFile = new File(dir, "saved_image.png");
        try {
            ImageIO.write(bufferedImage, "png", outputFile);
            System.out.println("Image saved successfully to " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}