package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static byte[] bufferedImageToBytes(BufferedImage bufferedImage, String format) {
        if (bufferedImage == null) {
            System.err.println("BufferedImage non valido per la conversione in byte.");
            return null;
        }
        if (format == null || format.trim().isEmpty()) {
            System.err.println("Formato immagine non specificato per la conversione in byte.");
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Errore durante la conversione dell'immagine in byte array (formato: " + format + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return ""; // Nessuna estensione trovata o nome file nullo
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}