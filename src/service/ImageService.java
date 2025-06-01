package service;

import model.Image;
import filter.Filter;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageService {
    Image loadImage(String path) throws IOException, IllegalArgumentException;
    boolean saveImage(Image image, String folderPath, String imageName) throws IOException, IllegalArgumentException;
    BufferedImage applyFilter(BufferedImage inputImage, Filter filter) throws IllegalArgumentException;

}