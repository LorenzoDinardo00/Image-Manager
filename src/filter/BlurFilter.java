package filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Filtro per sfocare un'immagine tramite box blur 3x3.
 */
public class BlurFilter implements Filter {

    @Override
    public BufferedImage apply(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int imageType = inputImage.getType();
        if (imageType == BufferedImage.TYPE_CUSTOM || imageType == 0) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }
        BufferedImage outputImage = new BufferedImage(width, height, imageType);
        // Applichiamo un semplice box blur 3x3
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumRed = 0, sumGreen = 0, sumBlue = 0;

                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        Color c = new Color(inputImage.getRGB(x + i, y + j));
                        sumRed += c.getRed();
                        sumGreen += c.getGreen();
                        sumBlue += c.getBlue();
                    }
                }

                int avgRed = sumRed / 9;
                int avgGreen = sumGreen / 9;
                int avgBlue = sumBlue / 9;

                Color blurred = new Color(avgRed, avgGreen, avgBlue);
                outputImage.setRGB(x, y, blurred.getRGB());
            }
        }

        // Copia i bordi senza modifiche
        for (int x = 0; x < width; x++) {
            outputImage.setRGB(x, 0, inputImage.getRGB(x, 0));
            outputImage.setRGB(x, height - 1, inputImage.getRGB(x, height - 1));
        }
        for (int y = 0; y < height; y++) {
            outputImage.setRGB(0, y, inputImage.getRGB(0, y));
            outputImage.setRGB(width - 1, y, inputImage.getRGB(width - 1, y));
        }

        return outputImage;
    }
}