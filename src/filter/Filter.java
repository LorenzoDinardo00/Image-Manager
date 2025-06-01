package filter;

import java.awt.image.BufferedImage;

public interface Filter {
    BufferedImage apply(BufferedImage inputImage);
}