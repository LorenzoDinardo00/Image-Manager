package filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Filtro per invertire i colori di un'immagine.
 */
public class InvertFilter implements Filter {

    @Override
    public BufferedImage apply(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(inputImage.getRGB(x, y));
                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();
                int blue = 255 - color.getBlue();
                Color inverted = new Color(red, green, blue);
                outputImage.setRGB(x, y, inverted.getRGB());
            }
        }

        return outputImage;
    }
}