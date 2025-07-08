package funzionali;

import model.Image;
import org.junit.jupiter.api.*;
import service.ImageService;
import service.impl.ImageServiceImpl;
import util.BaseTest;
import util.TestDataBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#8 - Salva Immagine Locale
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#8 - Salva Immagine Locale")
public class UC08_SalvaImmagineTest extends BaseTest {

    private ImageService imageService;
    private Image testImage;
    private static Path testDirectory;

    @Override
    protected void additionalSetup() throws Exception {
        imageService = new ImageServiceImpl();
        testImage = TestDataBuilder.createTestImage(150, 150);

        // Crea directory di test
        testDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "test_save_images");
        Files.createDirectories(testDirectory);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Salvataggio con nome valido")
    void testSalvataggioNomeValido() throws IOException {
        // Act
        boolean risultato = imageService.saveImage(
                testImage,
                testDirectory.toString(),
                "immagine_test"
        );

        // Assert
        assertTrue(risultato);

        Path expectedFile = testDirectory.resolve("immagine_test.png");
        assertTrue(Files.exists(expectedFile), "Il file dovrebbe essere stato creato");
        assertTrue(Files.size(expectedFile) > 0, "Il file non dovrebbe essere vuoto");

        // Cleanup
        Files.deleteIfExists(expectedFile);
    }

    @Test
    @Order(2)
    @DisplayName("Creazione cartella se non esiste")
    void testCreazioneCartella() throws IOException {
        // Arrange
        Path newDirectory = testDirectory.resolve("nuova_cartella");
        assertFalse(Files.exists(newDirectory), "La cartella non dovrebbe esistere inizialmente");

        // Act
        boolean risultato = imageService.saveImage(
                testImage,
                newDirectory.toString(),
                "immagine_in_nuova_cartella"
        );

        // Assert
        assertTrue(risultato);
        assertTrue(Files.exists(newDirectory), "La cartella dovrebbe essere stata creata");

        Path expectedFile = newDirectory.resolve("immagine_in_nuova_cartella.png");
        assertTrue(Files.exists(expectedFile));

        // Cleanup
        Files.deleteIfExists(expectedFile);
        Files.deleteIfExists(newDirectory);
    }

    @Test
    @Order(3)
    @DisplayName("Aggiunta estensione .png automatica")
    void testAggiuntaEstensionePng() throws IOException {
        // Act - Salva senza specificare estensione
        boolean risultato = imageService.saveImage(
                testImage,
                testDirectory.toString(),
                "immagine_senza_estensione"
        );

        // Assert
        assertTrue(risultato);

        Path expectedFile = testDirectory.resolve("immagine_senza_estensione.png");
        assertTrue(Files.exists(expectedFile), "Il file con estensione .png dovrebbe esistere");

        // Verifica che non sia stato creato anche senza estensione
        Path fileNoExt = testDirectory.resolve("immagine_senza_estensione");
        assertFalse(Files.exists(fileNoExt));

        // Cleanup
        Files.deleteIfExists(expectedFile);
    }

    @Test
    @Order(4)
    @DisplayName("Salvataggio con nome che già contiene .png")
    void testSalvataggioConEstensioneEsistente() throws IOException {
        // Act
        boolean risultato = imageService.saveImage(
                testImage,
                testDirectory.toString(),
                "immagine_con_estensione.png"
        );

        // Assert
        assertTrue(risultato);

        Path expectedFile = testDirectory.resolve("immagine_con_estensione.png");
        assertTrue(Files.exists(expectedFile));

        // Verifica che non sia stato creato un file con doppia estensione
        Path doubleExtFile = testDirectory.resolve("immagine_con_estensione.png.png");
        assertFalse(Files.exists(doubleExtFile));

        // Cleanup
        Files.deleteIfExists(expectedFile);
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Salvataggio senza immagine caricata")
    void testSalvataggioSenzaImmagine() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            imageService.saveImage(null, testDirectory.toString(), "test");
        });
    }

    @Test
    @Order(6)
    @DisplayName("Errore I/O durante salvataggio")
    void testErroreIODuranteSalvataggio() {
        // Arrange - Usa un percorso non valido per forzare un errore
        String invalidPath = "/percorso/non/valido/che/non/esiste";

        // Act & Assert
        assertThrows(IOException.class, () -> {
            imageService.saveImage(testImage, invalidPath, "test");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Nome file non valido")
    void testNomeFileNonValido() {
        // Act & Assert - Nome vuoto
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.saveImage(testImage, testDirectory.toString(), "");
        });

        // Act & Assert - Nome null
        assertThrows(NullPointerException.class, () -> {
            imageService.saveImage(testImage, testDirectory.toString(), null);
        });
    }

    @Test
    @Order(8)
    @DisplayName("Percorso cartella non valido")
    void testPercorsoCartellaNonValido() {
        // Act & Assert - Percorso null
        assertThrows(NullPointerException.class, () -> {
            imageService.saveImage(testImage, null, "test");
        });

        // Act & Assert - Percorso vuoto
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.saveImage(testImage, "", "test");
        });
    }

    @AfterEach
    void cleanupFiles() throws IOException {
        // Pulisce tutti i file creati durante i test
        if (Files.exists(testDirectory)) {
            Files.walk(testDirectory)
                    .sorted((a, b) -> -a.compareTo(b)) // Ordine inverso per eliminare prima i file
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignora errori di pulizia
                        }
                    });
        }
    }
}