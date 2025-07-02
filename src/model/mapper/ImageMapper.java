package model.mapper;

import model.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Mapper per la conversione di Image da/verso vari formati.
 * Anche se Image non è persistito direttamente nel DB, questo mapper
 * può essere utile per conversioni da/verso byte array e altri formati.
 */
public class ImageMapper {

    /**
     * Converte un array di byte in un oggetto Image.
     * Utile quando si recuperano dati immagine dal database (es. da Post).
     *
     * @param imageData array di byte contenente i dati dell'immagine
     * @return un oggetto Image contenente la BufferedImage
     * @throws IOException se si verifica un errore nella lettura dell'immagine
     */
    public Image fromByteArray(byte[] imageData) throws IOException {
        if (imageData == null || imageData.length == 0) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            BufferedImage bufferedImage = ImageIO.read(bais);
            if (bufferedImage == null) {
                throw new IOException("Impossibile decodificare i dati dell'immagine");
            }
            return new Image(bufferedImage);
        }
    }

    /**
     * Converte un oggetto Image in un array di byte.
     * Utile quando si deve salvare un'immagine nel database.
     *
     * @param image l'oggetto Image da convertire
     * @param format il formato di output (es. "png", "jpg")
     * @return array di byte contenente i dati dell'immagine
     * @throws IOException se si verifica un errore nella scrittura dell'immagine
     */
    public byte[] toByteArray(Image image, String format) throws IOException {
        if (image == null || image.getBufferedImage() == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image.getBufferedImage(), format, baos);
            if (!success) {
                throw new IOException("Formato immagine non supportato: " + format);
            }
            return baos.toByteArray();
        }
    }

    /**
     * Crea un oggetto Image dalle dimensioni specificate.
     * Utile per test o per creare immagini placeholder.
     *
     * @param width larghezza dell'immagine
     * @param height altezza dell'immagine
     * @param imageType tipo di BufferedImage (es. BufferedImage.TYPE_INT_ARGB)
     * @return un nuovo oggetto Image vuoto con le dimensioni specificate
     */
    public Image createEmptyImage(int width, int height, int imageType) {
        BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
        return new Image(bufferedImage);
    }

    /**
     * Verifica se un'immagine è valida per essere salvata.
     *
     * @param image l'immagine da validare
     * @return true se l'immagine è valida, false altrimenti
     */
    public boolean isValid(Image image) {
        return image != null &&
                image.getBufferedImage() != null &&
                image.getWidth() > 0 &&
                image.getHeight() > 0;
    }
}
