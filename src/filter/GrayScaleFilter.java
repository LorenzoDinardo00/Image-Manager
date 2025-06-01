package filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Filtro per convertire un'immagine in scala di grigi.
 */
public class GrayScaleFilter implements Filter {

    @Override
    public BufferedImage apply(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(inputImage.getRGB(x, y));
                int gray = (int)(color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                Color grayColor = new Color(gray, gray, gray);
                outputImage.setRGB(x, y, grayColor.getRGB());
            }
        }

        return outputImage;
    }
}