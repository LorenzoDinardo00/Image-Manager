import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * La classe ImageAcquisition si occupa di caricare immagini da file e di trasformarle in array
 * di pixel per essere successivamente elaborate in operazioni di computer vision.
 * Le funzionalità offerte sono:
 * - Caricamento dell'immagine da un file (assumendo formato JPG).
 * - Conversione dell'immagine in un array bidimensionale di interi (RGB intero per pixel).
 * - (Opzionale) Conversione dell'immagine in un array tridimensionale per separare i canali RGB.
 */
public class ImageAcquisition {

    /**
     * Carica un'immagine dal file system.
     *
     * @param filePath il percorso del file immagine in formato JPG
     * @return l'immagine caricata come BufferedImage
     * @throws IOException se il file non può essere letto o non rappresenta un'immagine valida
     */
    public BufferedImage loadImage(String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        if (image == null) {
            throw new IOException("Impossibile caricare l'immagine da: " + filePath);
        }
        return image;
    }

    /**
     * Converte una BufferedImage in un array bidimensionale di pixel.
     * Ogni elemento dell'array rappresenta il valore RGB del pixel (codificato in un intero).
     *
     * @param image la BufferedImage da convertire
     * @return un array bidimensionale di pixel
     */
    public int[][] toPixelArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] pixelArray = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelArray[y][x] = image.getRGB(x, y);
            }
        }
        return pixelArray;
    }

    /**
     * Metodo di utilità che carica un'immagine da file e la converte in un array bidimensionale.
     *
     * @param filePath il percorso del file immagine in formato JPG
     * @return un array bidimensionale contenente i valori RGB di ciascun pixel
     * @throws IOException se il file non può essere letto o l'immagine non è valida
     */
    public int[][] acquireImageAsArray(String filePath) throws IOException {
        BufferedImage image = loadImage(filePath);
        return toPixelArray(image);
    }

    /**
     * Converte una BufferedImage in un array tridimensionale che separa i canali RGB.
     * L'array risultante ha dimensioni [altezza][larghezza][3] dove:
     * - [][0] contiene il canale rosso,
     * - [][1] contiene il canale verde,
     * - [][2] contiene il canale blu.
     *
     * @param image la BufferedImage da convertire
     * @return un array tridimensionale contenente i valori dei canali RGB per ogni pixel
     */
    public int[][][] toRGBArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] rgbArray = new int[height][width][3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                // Estrae i componenti R, G, B
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                rgbArray[y][x][0] = red;
                rgbArray[y][x][1] = green;
                rgbArray[y][x][2] = blue;
            }
        }
        return rgbArray;
    }
}

