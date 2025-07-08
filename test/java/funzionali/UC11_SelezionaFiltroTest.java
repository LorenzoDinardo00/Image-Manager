package funzionali;

import filter.*;
import model.Image;
import org.junit.jupiter.api.*;
import service.ImageService;
import service.impl.ImageServiceImpl;
import util.BaseTest;
import util.TestDataBuilder;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#11 - Seleziona Filtro
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#11 - Seleziona Filtro")
public class UC11_SelezionaFiltroTest extends BaseTest {

    private ImageService imageService;
    private FilterRegistry filterRegistry;
    private Image testImage;

    @Override
    protected void additionalSetup() throws Exception {
        imageService = new ImageServiceImpl();
        filterRegistry = new FilterRegistry();
        testImage = TestDataBuilder.createTestImage(200, 200);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Selezione di ogni filtro disponibile")
    void testSelezioneOgniFiltroDisponibile() {
        // Arrange
        List<Filter> filtriDisponibili = filterRegistry.getAvailableFilters();
        BufferedImage original = testImage.getBufferedImage();

        // Act & Assert - Testa ogni filtro
        assertFalse(filtriDisponibili.isEmpty(), "Dovrebbero esserci filtri disponibili");

        for (Filter filtro : filtriDisponibili) {
            BufferedImage risultato = imageService.applyFilter(original, filtro);

            assertNotNull(risultato, "Il filtro " + filtro.getClass().getSimpleName() +
                    " dovrebbe produrre un risultato");
            assertEquals(original.getWidth(), risultato.getWidth());
            assertEquals(original.getHeight(), risultato.getHeight());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Visualizzazione anteprima")
    void testVisualizzazioneAnteprima() {
        // Arrange
        Filter grayScaleFilter = new GrayScaleFilter();
        BufferedImage original = testImage.getBufferedImage();

        // Act - Simula la visualizzazione dell'anteprima
        BufferedImage anteprima = imageService.applyFilter(original, grayScaleFilter);

        // Assert
        assertNotNull(anteprima);
        // L'anteprima dovrebbe essere diversa dall'originale
        boolean isDifferent = false;
        for (int x = 0; x < Math.min(10, original.getWidth()); x++) {
            for (int y = 0; y < Math.min(10, original.getHeight()); y++) {
                if (original.getRGB(x, y) != anteprima.getRGB(x, y)) {
                    isDifferent = true;
                    break;
                }
            }
        }
        assertTrue(isDifferent, "L'anteprima dovrebbe mostrare l'effetto del filtro");
    }

    @Test
    @Order(3)
    @DisplayName("Annullamento operazione")
    void testAnnullamentoOperazione() {
        // Arrange
        BufferedImage original = testImage.getBufferedImage();
        BufferedImage copiaOriginale = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                original.getType()
        );
        copiaOriginale.getGraphics().drawImage(original, 0, 0, null);

        // Act - Simula selezione e poi annullamento
        Filter filtro = new InvertFilter();
        // L'utente vede l'anteprima ma annulla
        BufferedImage anteprima = imageService.applyFilter(original, filtro);

        // Assert - L'immagine originale non dovrebbe essere modificata
        for (int x = 0; x < original.getWidth(); x += 10) {
            for (int y = 0; y < original.getHeight(); y += 10) {
                assertEquals(copiaOriginale.getRGB(x, y), original.getRGB(x, y),
                        "L'immagine originale non dovrebbe essere modificata dopo annullamento");
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Verifica presenza filtri standard")
    void testPresenzaFiltriStandard() {
        // Arrange
        List<Filter> filtri = filterRegistry.getAvailableFilters();

        // Assert - Verifica che ci siano i filtri base
        assertTrue(filtri.stream().anyMatch(f -> f instanceof GrayScaleFilter),
                "Dovrebbe esserci il filtro GrayScale");
        assertTrue(filtri.stream().anyMatch(f -> f instanceof InvertFilter),
                "Dovrebbe esserci il filtro Invert");
        assertTrue(filtri.stream().anyMatch(f -> f instanceof BlurFilter),
                "Dovrebbe esserci il filtro Blur");
        assertTrue(filtri.stream().anyMatch(f -> f instanceof SharpenFilter),
                "Dovrebbe esserci il filtro Sharpen");
        assertTrue(filtri.stream().anyMatch(f -> f instanceof SepiaFilter),
                "Dovrebbe esserci il filtro Sepia");
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Selezione indice filtro non valido")
    void testSelezioneIndiceFiltroNonValido() {
        // Arrange
        List<Filter> filtri = filterRegistry.getAvailableFilters();
        int indiceNonValido = filtri.size() + 10;

        // Act & Assert - Simula selezione di indice non valido
        assertThrows(IndexOutOfBoundsException.class, () -> {
            filtri.get(indiceNonValido);
        });
    }

    @Test
    @Order(6)
    @DisplayName("Applicazione filtro su immagine null")
    void testApplicazioneFiltroSuNull() {
        // Arrange
        Filter filtro = new GrayScaleFilter();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            imageService.applyFilter(null, filtro);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Registry senza filtri")
    void testRegistrySenzaFiltri() {
        // Arrange - Crea un registry vuoto
        FilterRegistry emptyRegistry = new FilterRegistry() {
            @Override
            public List<Filter> getAvailableFilters() {
                return List.of(); // Lista vuota
            }
        };

        // Act
        List<Filter> filtri = emptyRegistry.getAvailableFilters();

        // Assert
        assertTrue(filtri.isEmpty());
    }
}