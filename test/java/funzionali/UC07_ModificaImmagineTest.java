package funzionali;

import filter.*;
import model.Image;
import org.junit.jupiter.api.*;
import service.ImageService;
import service.impl.ImageServiceImpl;
import util.BaseTest;
import util.TestDataBuilder;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#7 - Modifica Immagine
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#7 - Modifica Immagine")
public class UC07_ModificaImmagineTest extends BaseTest {

    private ImageService imageService;
    private Image testImage;
    private FilterRegistry filterRegistry;

    @Override
    protected void additionalSetup() throws Exception {
        imageService = new ImageServiceImpl();
        filterRegistry = new FilterRegistry();

        // Crea un'immagine di test colorata per vedere gli effetti dei filtri
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                // Crea un gradiente di colori
                int r = (x * 255) / 100;
                int g = (y * 255) / 100;
                int b = 128;
                bufferedImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        testImage = new Image(bufferedImage);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Applicazione filtro GrayScale")
    void testApplicazioneGrayScale() {
        // Arrange
        Filter grayScaleFilter = new GrayScaleFilter();
        BufferedImage original = testImage.getBufferedImage();

        // Act
        BufferedImage risultato = imageService.applyFilter(original, grayScaleFilter);

        // Assert
        assertNotNull(risultato);
        assertEquals(original.getWidth(), risultato.getWidth());
        assertEquals(original.getHeight(), risultato.getHeight());

        // Verifica che l'immagine sia effettivamente in scala di grigi
        Color pixelColor = new Color(risultato.getRGB(50, 50));
        assertEquals(pixelColor.getRed(), pixelColor.getGreen());
        assertEquals(pixelColor.getGreen(), pixelColor.getBlue());
    }

    @Test
    @Order(2)
    @DisplayName("Applicazione filtro Invert")
    void testApplicazioneInvert() {
        // Arrange
        Filter invertFilter = new InvertFilter();
        BufferedImage original = testImage.getBufferedImage();

        // Act
        BufferedImage risultato = imageService.applyFilter(original, invertFilter);

        // Assert
        assertNotNull(risultato);

        // Verifica che i colori siano invertiti
        Color originalColor = new Color(original.getRGB(50, 50));
        Color invertedColor = new Color(risultato.getRGB(50, 50));

        assertEquals(255 - originalColor.getRed(), invertedColor.getRed());
        assertEquals(255 - originalColor.getGreen(), invertedColor.getGreen());
        assertEquals(255 - originalColor.getBlue(), invertedColor.getBlue());
    }

    @Test
    @Order(3)
    @DisplayName("Applicazione filtro Blur")
    void testApplicazioneBlur() {
        // Arrange
        Filter blurFilter = new BlurFilter();
        BufferedImage original = testImage.getBufferedImage();

        // Act
        BufferedImage risultato = imageService.applyFilter(original, blurFilter);

        // Assert
        assertNotNull(risultato);
        assertEquals(original.getWidth(), risultato.getWidth());
        assertEquals(original.getHeight(), risultato.getHeight());

        // Il blur dovrebbe creare una versione sfocata (test visivo difficile da automatizzare)
        // Verifichiamo almeno che non sia identica all'originale nei pixel interni
        boolean isDifferent = false;
        for (int x = 10; x < 90; x += 10) {
            for (int y = 10; y < 90; y += 10) {
                if (original.getRGB(x, y) != risultato.getRGB(x, y)) {
                    isDifferent = true;
                    break;
                }
            }
        }
        assertTrue(isDifferent, "Il filtro blur dovrebbe modificare l'immagine");
    }

    @Test
    @Order(4)
    @DisplayName("Applicazione filtro Sharpen")
    void testApplicazioneSharpen() {
        // Arrange
        Filter sharpenFilter = new SharpenFilter();
        BufferedImage original = testImage.getBufferedImage();

        // Act
        BufferedImage risultato = imageService.applyFilter(original, sharpenFilter);

        // Assert
        assertNotNull(risultato);
        assertEquals(original.getWidth(), risultato.getWidth());
        assertEquals(original.getHeight(), risultato.getHeight());
    }

    @Test
    @Order(5)
    @DisplayName("Applicazione filtro Sepia")
    void testApplicazioneSepia() {
        // Arrange
        Filter sepiaFilter = new SepiaFilter();
        BufferedImage original = testImage.getBufferedImage();

        // Act
        BufferedImage risultato = imageService.applyFilter(original, sepiaFilter);

        // Assert
        assertNotNull(risultato);

        // Verifica che l'immagine abbia toni seppia (più rosso/marrone)
        Color sepiaColor = new Color(risultato.getRGB(50, 50));
        assertTrue(sepiaColor.getRed() >= sepiaColor.getBlue(),
                "Il filtro seppia dovrebbe aumentare i toni rossi");
    }

    @Test
    @Order(6)
    @DisplayName("Applicazione Luminosità/Contrasto")
    void testApplicazioneLuminositaContrasto() {
        // Arrange
        Filter brightnessContrastFilter = new BrightnessContrastFilter(50, 1.5);
        BufferedImage original = testImage.getBufferedImage();

        // Act
        BufferedImage risultato = imageService.applyFilter(original, brightnessContrastFilter);

        // Assert
        assertNotNull(risultato);

        // Verifica che l'immagine sia più luminosa
        Color originalColor = new Color(original.getRGB(50, 50));
        Color modifiedColor = new Color(risultato.getRGB(50, 50));

        // Almeno uno dei canali dovrebbe essere più luminoso
        assertTrue(modifiedColor.getRed() > originalColor.getRed() ||
                        modifiedColor.getGreen() > originalColor.getGreen() ||
                        modifiedColor.getBlue() > originalColor.getBlue(),
                "L'immagine dovrebbe essere più luminosa");
    }

    @Test
    @Order(7)
    @DisplayName("Salvataggio immagine modificata")
    void testSalvataggioImmagineModificata() throws Exception {
        // Arrange
        Filter filter = new GrayScaleFilter();
        BufferedImage original = testImage.getBufferedImage();
        BufferedImage modificata = imageService.applyFilter(original, filter);
        Image immagineModificata = new Image(modificata);

        String testDir = System.getProperty("java.io.tmpdir");

        // Act
        boolean salvata = imageService.saveImage(immagineModificata, testDir, "test_modificata");

        // Assert
        assertTrue(salvata);
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(8)
    @DisplayName("Modifica senza immagine caricata")
    void testModificaSenzaImmagine() {
        // Arrange
        Filter filter = new GrayScaleFilter();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            imageService.applyFilter(null, filter);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Selezione filtro non valido")
    void testFiltroNonValido() {
        // Arrange
        BufferedImage original = testImage.getBufferedImage();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            imageService.applyFilter(original, null);
        });
    }

    @Test
    @Order(10)
    @DisplayName("Errore durante applicazione filtro")
    void testErroreApplicazioneFiltro() {
        // Arrange - Crea un filtro che lancia eccezione
        Filter filtroErrore = new Filter() {
            @Override
            public BufferedImage apply(BufferedImage inputImage) {
                throw new RuntimeException("Errore simulato nel filtro");
            }
        };

        BufferedImage original = testImage.getBufferedImage();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            imageService.applyFilter(original, filtroErrore);
        });
    }
}