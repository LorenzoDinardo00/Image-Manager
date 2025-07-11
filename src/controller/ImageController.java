package controller;

import model.Image;
import service.ImageService;
import filter.Filter;
import filter.FilterRegistry;
import filter.BrightnessContrastFilter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ImageController {
    private final ImageService imageService;
    private final Scanner scanner;
    private final FilterRegistry filterRegistry;
    private static final String LOCAL_SAVED_IMAGES_FOLDER = "immagini_salvate_localmente";


    public ImageController(ImageService imageService, Scanner scanner) {
        this.imageService = imageService;
        this.scanner = scanner;
        this.filterRegistry = new FilterRegistry();
    }

    public Image loadImage() {
        System.out.print("Inserisci il path dell'immagine da caricare: ");
        String path = scanner.nextLine();
        try {
            Image loadedImage = imageService.loadImage(path);
            System.out.println("Immagine caricata con successo! Dimensioni: " + loadedImage.getWidth() + "x" + loadedImage.getHeight());
            return loadedImage;
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento dell'immagine: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Path non valido: " + e.getMessage());
        }
        return null;
    }

    public void saveLoadedImage(Image loadedImg) {
        if (loadedImg == null || loadedImg.getBufferedImage() == null) {
            System.out.println("Nessuna immagine valida caricata in memoria.");
            return;
        }

        String fixedLocalSavePath = Paths.get(System.getProperty("user.dir"), LOCAL_SAVED_IMAGES_FOLDER).toString();

        System.out.print("Inserisci il nome dell'immagine da salvare (senza estensione, sarà .png): ");
        String imageNameSave = scanner.nextLine();

        try {
            if (imageService.saveImage(loadedImg, fixedLocalSavePath, imageNameSave)) {
            } else {
                System.out.println("Errore nel salvataggio locale dell'immagine (ImageService ha restituito false).");
            }
        } catch (IOException e) {
            System.err.println("Errore I/O durante il salvataggio dell'immagine: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Errore nei parametri di salvataggio: " + e.getMessage());
        }
    }

    public Image modifyLoadedImage(Image loadedImg) {
        if (loadedImg == null || loadedImg.getBufferedImage() == null) {
            System.out.println("Nessuna immagine valida caricata. Carica prima un'immagine.");
            return loadedImg;
        }

        System.out.println("Filtri disponibili:");
        List<Filter> availableFilters = filterRegistry.getAvailableFilters();
        for (int i = 0; i < availableFilters.size(); i++) {
            System.out.println((i + 1) + ". " + availableFilters.get(i).getClass().getSimpleName());
        }
        System.out.println((availableFilters.size() + 1) + ". Regola Luminosità/Contrasto");
        System.out.print("Seleziona un filtro (numero corrispondente, 0 per annullare): ");

        if (!scanner.hasNextInt()) {
            System.out.println("Input non valido. Inserisci un numero.");
            scanner.next();
            return loadedImg;
        }
        int filtroSceltoIdxInput = scanner.nextInt();
        scanner.nextLine();

        BufferedImage originalBI = loadedImg.getBufferedImage();
        BufferedImage filteredBI = null;

        try {
            if (filtroSceltoIdxInput == 0) {
                System.out.println("Modifica annullata.");
            } else if (filtroSceltoIdxInput > 0 && filtroSceltoIdxInput <= availableFilters.size()) {
                Filter selectedFilter = availableFilters.get(filtroSceltoIdxInput - 1);
                filteredBI = imageService.applyFilter(originalBI, selectedFilter);
                System.out.println("Filtro " + selectedFilter.getClass().getSimpleName() + " applicato!");
            } else if (filtroSceltoIdxInput == availableFilters.size() + 1) {
                System.out.print("Inserisci valore luminosità (es. 10, -20): ");
                int brightness = scanner.nextInt();
                System.out.print("Inserisci valore contrasto (es. 1.0 per nessun cambiamento, 1.5 per aumentare): ");
                double contrast = scanner.nextDouble();
                scanner.nextLine();
                Filter bcFilter = new BrightnessContrastFilter(brightness, contrast);
                filteredBI = imageService.applyFilter(originalBI, bcFilter);
                System.out.println("Filtro Luminosità/Contrasto applicato!");
            } else {
                System.out.println("Filtro non valido.");
            }

            if (filteredBI != null) {
                Image modifiedImage = new Image(filteredBI);
                System.out.print("Vuoi salvare l'immagine modificata (nella cartella '" + LOCAL_SAVED_IMAGES_FOLDER + "')? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    saveLoadedImage(modifiedImage);
                }
                return modifiedImage;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Errore nell'applicazione del filtro: " + e.getMessage());
        }
        return loadedImg;
    }
}