package funzionali;

import model.Image;
import org.junit.jupiter.api.*;
import service.ImageService;
import service.impl.ImageServiceImpl;
import util.BaseTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#6 - Carica Immagine
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#6 - Carica Immagine")
public class UC06_CaricaImmagineTest extends BaseTest {

    private ImageService imageService;
    private static final String TEST_IMAGES_DIR = "test/resources/test-images/";
    private static Path testPngPath;
    private static Path testJpgPath;
    private static Path testTxtPath;

    @BeforeAll
    static void createTestFiles() throws IOException {
        // Crea la directory se non esiste
        Files.createDirectories(Paths.get(TEST_IMAGES_DIR));

        // Crea immagine PNG di test
        testPngPath = Paths.get(TEST_IMAGES_DIR, "test.png");
        BufferedImage pngImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = pngImage.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        ImageIO.write(pngImage, "png", testPngPath.toFile());

        // Crea immagine JPG di test
        testJpgPath = Paths.get(TEST_IMAGES_DIR, "test.jpg");
        BufferedImage jpgImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        g = jpgImage.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        ImageIO.write(jpgImage, "jpg", testJpgPath.toFile());

        // Crea file di testo (non immagine)
        testTxtPath = Paths.get(TEST_IMAGES_DIR, "notanimage.txt");
        Files.write(testTxtPath, "Questo non è un'immagine".getBytes());
    }

    @Override
    protected void additionalSetup() throws Exception {
        imageService = new ImageServiceImpl();
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Caricamento immagine PNG valida")
    void testCaricamentoPngValido() throws IOException {
        // Act
        Image immagineCaricata = imageService.loadImage(testPngPath.toString());

        // Assert
        assertNotNull(immagineCaricata);
        assertNotNull(immagineCaricata.getBufferedImage());
        assertEquals(100, immagineCaricata.getWidth());
        assertEquals(100, immagineCaricata.getHeight());
    }

    @Test
    @Order(2)
    @DisplayName("Caricamento immagine JPG valida")
    void testCaricamentoJpgValido() throws IOException {
        // Act
        Image immagineCaricata = imageService.loadImage(testJpgPath.toString());

        // Assert
        assertNotNull(immagineCaricata);
        assertNotNull(immagineCaricata.getBufferedImage());
        assertEquals(200, immagineCaricata.getWidth());
        assertEquals(200, immagineCaricata.getHeight());
    }

    @Test
    @Order(3)
    @DisplayName("Caricamento con sovrascrittura immagine esistente (con conferma)")
    void testCaricamentoSovrascrittura() throws IOException {
        // Arrange - Prima carica un'immagine
        Image primaImmagine = imageService.loadImage(testPngPath.toString());
        assertNotNull(primaImmagine);

        // Act - Carica una seconda immagine (simulando la conferma dell'utente)
        Image secondaImmagine = imageService.loadImage(testJpgPath.toString());

        // Assert
        assertNotNull(secondaImmagine);
        assertNotEquals(primaImmagine.getWidth(), secondaImmagine.getWidth());
        assertEquals(200, secondaImmagine.getWidth());
    }

    @Test
    @Order(4)
    @DisplayName("Verifica dimensioni immagine caricate")
    void testVerificaDimensioni() throws IOException {
        // Arrange - Crea un'immagine con dimensioni specifiche
        Path customImagePath = Paths.get(TEST_IMAGES_DIR, "custom.png");
        BufferedImage customImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(customImage, "png", customImagePath.toFile());

        // Act
        Image immagineCaricata = imageService.loadImage(customImagePath.toString());

        // Assert
        assertEquals(640, immagineCaricata.getWidth());
        assertEquals(480, immagineCaricata.getHeight());

        // Cleanup
        Files.deleteIfExists(customImagePath);
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Caricamento file non esistente")
    void testCaricamentoFileNonEsistente() {
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            imageService.loadImage("percorso/inesistente/immagine.png");
        });

        assertTrue(exception.getMessage().contains("File non trovato"));
    }

    @Test
    @Order(6)
    @DisplayName("Caricamento file non immagine")
    void testCaricamentoFileNonImmagine() {
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            imageService.loadImage(testTxtPath.toString());
        });

        assertTrue(exception.getMessage().contains("Impossibile leggere l'immagine"));
    }

    @Test
    @Order(7)
    @DisplayName("Caricamento con path non valido")
    void testCaricamentoPathNonValido() {
        // Act & Assert - Path nullo
        assertThrows(NullPointerException.class, () -> {
            imageService.loadImage(null);
        });
    }

    @Test
    @Order(8)
    @DisplayName("Caricamento formato non supportato")
    void testCaricamentoFormatoNonSupportato() throws IOException {
        // Arrange - Crea un file con estensione non supportata
        Path unsupportedPath = Paths.get(TEST_IMAGES_DIR, "test.xyz");
        Files.write(unsupportedPath, new byte[]{1, 2, 3, 4});

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            imageService.loadImage(unsupportedPath.toString());
        });

        assertTrue(exception.getMessage().contains("Impossibile leggere"));

        // Cleanup
        Files.deleteIfExists(unsupportedPath);
    }

    @AfterAll
    static void cleanupTestFiles() throws IOException {
        // Pulisce i file di test
        Files.deleteIfExists(testPngPath);
        Files.deleteIfExists(testJpgPath);
        Files.deleteIfExists(testTxtPath);
    }
}