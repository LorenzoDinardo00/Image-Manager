package service.impl;

import service.ImageService;
import model.Image;
import filter.Filter; // Assicurati che sia nel package corretto

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ImageServiceImpl implements ImageService {

    @Override
    public Image loadImage(String path) throws IOException, IllegalArgumentException {
        Objects.requireNonNull(path, "Il percorso del file non può essere nullo.");
        if (path.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso del file non può essere vuoto.");
        }
        try {
            File inputFile = new File(path);
            if (!inputFile.exists() || !inputFile.isFile()) {
                throw new IOException("File non trovato o non è un file valido: " + path);
            }
            BufferedImage bufferedImage = ImageIO.read(inputFile);
            if (bufferedImage == null) {
                throw new IOException("Impossibile leggere l'immagine dal percorso (formato non supportato o file corrotto): " + path);
            }
            return new Image(bufferedImage);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean saveImage(Image image, String folderPath, String imageName) throws IOException, IllegalArgumentException {
        Objects.requireNonNull(image, "L'oggetto Image non può essere nullo."); //
        Objects.requireNonNull(image.getBufferedImage(), "La BufferedImage nell'oggetto Image non può essere nulla.");
        Objects.requireNonNull(folderPath, "Il percorso della cartella non può essere nullo.");
        Objects.requireNonNull(imageName, "Il nome dell'immagine non può essere nullo.");
        if (folderPath.trim().isEmpty() || imageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Percorso cartella e nome immagine non possono essere vuoti.");
        }

        BufferedImage bufferedImage = image.getBufferedImage();
        Path dirPath = Paths.get(folderPath);

        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new IOException("Impossibile creare la directory di destinazione: " + folderPath, e);
            }
        } else if (!Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("Il percorso specificato non è una directory: " + folderPath);
        }

        String finalImageName = imageName.toLowerCase().endsWith(".png") ? imageName : imageName + ".png";
        File outputFile = new File(dirPath.toFile(), finalImageName);

        try {
            boolean success = ImageIO.write(bufferedImage, "png", outputFile);
            if (success) {
                System.out.println("Immagine salvata correttamente al seguente percorso " + outputFile.getAbsolutePath()); //
            } else {           
                System.err.println("ImageIO.write ha restituito false senza eccezioni per: " + outputFile.getAbsolutePath());
            }
            return success;
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public BufferedImage applyFilter(BufferedImage inputImage, Filter filter) throws IllegalArgumentException {
        Objects.requireNonNull(inputImage, "L'immagine di input non può essere nulla.");
        Objects.requireNonNull(filter, "Il filtro non può essere nullo.");
        return filter.apply(inputImage);
    }
}