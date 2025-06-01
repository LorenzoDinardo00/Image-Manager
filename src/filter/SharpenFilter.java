package filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Filtro per accentuare i contorni e rendere l'immagine più nitida.
 */
public class SharpenFilter implements Filter {

    @Override
    public BufferedImage apply(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        // Definizione di un kernel per lo sharpen
        int[][] kernel = {
                { 0, -1, 0 },
                { -1, 5, -1 },
                { 0, -1, 0 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumRed = 0, sumGreen = 0, sumBlue = 0;

                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        Color c = new Color(inputImage.getRGB(x + i, y + j));
                        int weight = kernel[j + 1][i + 1];
                        sumRed += c.getRed() * weight;
                        sumGreen += c.getGreen() * weight;
                        sumBlue += c.getBlue() * weight;
                    }
                }

                int newRed = Math.min(255, Math.max(0, sumRed));
                int newGreen = Math.min(255, Math.max(0, sumGreen));
                int newBlue = Math.min(255, Math.max(0, sumBlue));

                Color sharpened = new Color(newRed, newGreen, newBlue);
                outputImage.setRGB(x, y, sharpened.getRGB());
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
