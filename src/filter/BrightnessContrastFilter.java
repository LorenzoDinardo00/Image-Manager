package filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Filtro per regolare la luminosità e il contrasto dell'immagine.
 */
public class BrightnessContrastFilter implements Filter {
    private int brightness;
    private double contrast;

    public BrightnessContrastFilter(int brightness, double contrast) {
        this.brightness = brightness;
        this.contrast = contrast;
    }

    @Override
    public BufferedImage apply(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(inputImage.getRGB(x, y));
                int red = adjust(c.getRed());
                int green = adjust(c.getGreen());
                int blue = adjust(c.getBlue());
                Color adjusted = new Color(red, green, blue);
                outputImage.setRGB(x, y, adjusted.getRGB());
            }
        }

        return outputImage;
    }

    private int adjust(int value) {
        // Applica il contrasto intorno a 128 e poi aggiunge la luminosità
        int newValue = (int)(contrast * (value - 128) + 128 + brightness);
        return Math.min(255, Math.max(0, newValue));
    }
}