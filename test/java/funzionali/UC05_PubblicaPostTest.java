package funzionali;

import dao.PostDao;
import dao.CommentDao;
import dao.impl.PostDAOImpl;
import dao.impl.CommentDAOImpl;
import model.Image;
import model.Post;
import model.Role;
import model.User;
import org.junit.jupiter.api.*;
import service.PostService;
import service.impl.PostServiceImpl;
import util.BaseTest;
import util.TestDataBuilder;
import util.TestDatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#5 - Pubblica Post
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#5 - Pubblica Post")
public class UC05_PubblicaPostTest extends BaseTest {

    private PostService postService;
    private Connection testConnection;
    private User autoreTest;
    private User osservatoreTest;

    @Override
    protected void additionalSetup() throws Exception {
        testConnection = TestDatabaseConfig.getTestConnection();

        PostDao postDao = new PostDAOImpl() {
            protected Connection getConnection() throws SQLException {
                return testConnection;
            }
        };

        CommentDao commentDao = new CommentDAOImpl() {
            protected Connection getConnection() throws SQLException {
                return testConnection;
            }
        };

        postService = new PostServiceImpl(postDao, commentDao);

        // Pre-inserisce utenti
        testConnection.createStatement().execute(
                "INSERT INTO users (username, name, surname, dateofbirth, email, password, role) " +
                        "VALUES ('autoretest', 'Autore', 'Test', '1990-01-01', 'autore@test.com', 'pass', 'autore')," +
                        "('osservatoretest', 'Osservatore', 'Test', '1991-01-01', 'oss@test.com', 'pass', 'osservatore')"
        );

        autoreTest = TestDataBuilder.createTestUser("autoretest", Role.AUTORE);
        osservatoreTest = TestDataBuilder.createTestUser("osservatoretest", Role.OSSERVATORE);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Pubblicazione post con immagine caricata e descrizione")
    void testPubblicazionePostCompleto() throws SQLException {
        // Arrange
        byte[] imageData = TestDataBuilder.createTestImageData();
        Post nuovoPost = new Post(
                "autoretest",
                imageData,
                imageData.length,
                "png",
                "Questo è un post di test con descrizione"
        );

        // Act
        boolean risultato = postService.createPost(nuovoPost, autoreTest);

        // Assert
        assertTrue(risultato);
        assertNotNull(nuovoPost.getPostId());
        assertTrue(nuovoPost.getPostId() > 0);

        // Verifica che il post sia nel database
        Post postSalvato = postService.getPostById(nuovoPost.getPostId());
        assertNotNull(postSalvato);
        assertEquals("Questo è un post di test con descrizione", postSalvato.getDescription());
        assertEquals("png", postSalvato.getImageFormat());
    }

    @Test
    @Order(2)
    @DisplayName("Pubblicazione post con descrizione vuota")
    void testPubblicazionePostSenzaDescrizione() throws SQLException {
        // Arrange
        byte[] imageData = TestDataBuilder.createTestImageData();
        Post nuovoPost = new Post(
                "autoretest",
                imageData,
                imageData.length,
                "png",
                ""  // descrizione vuota
        );

        // Act
        boolean risultato = postService.createPost(nuovoPost, autoreTest);

        // Assert
        assertTrue(risultato);
        Post postSalvato = postService.getPostById(nuovoPost.getPostId());
        assertEquals("", postSalvato.getDescription());
    }

    @Test
    @Order(3)
    @DisplayName("Verifica generazione ID post")
    void testGenerazioneIdPost() throws SQLException {
        // Arrange
        Post post1 = TestDataBuilder.createTestPost("autoretest", "Primo post");
        Post post2 = TestDataBuilder.createTestPost("autoretest", "Secondo post");

        // Act
        postService.createPost(post1, autoreTest);
        postService.createPost(post2, autoreTest);

        // Assert
        assertTrue(post1.getPostId() > 0);
        assertTrue(post2.getPostId() > 0);
        assertNotEquals(post1.getPostId(), post2.getPostId());
        assertTrue(post2.getPostId() > post1.getPostId());
    }

    @Test
    @Order(4)
    @DisplayName("Verifica salvataggio dati binari immagine")
    void testSalvataggioDatiBinari() throws SQLException {
        // Arrange
        byte[] imageData = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Post post = new Post(
                "autoretest",
                imageData,
                imageData.length,
                "png",
                "Test dati binari"
        );

        // Act
        postService.createPost(post, autoreTest);

        // Assert
        Post postSalvato = postService.getPostById(post.getPostId());
        assertArrayEquals(imageData, postSalvato.getImageData());
        assertEquals(10, postSalvato.getImageSize());
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Pubblicazione senza immagine caricata")
    void testPubblicazioneSenzaImmagine() {
        // Arrange
        Post postSenzaImmagine = new Post(
                "autoretest",
                null,  // nessuna immagine
                0,
                "png",
                "Post senza immagine"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(postSenzaImmagine, autoreTest);
        });
    }

    @Test
    @Order(6)
    @DisplayName("Pubblicazione senza essere loggato")
    void testPubblicazioneSenzaLogin() {
        // Arrange
        Post post = TestDataBuilder.createTestPost("autoretest", "Test");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(post, null);  // utente null
        });
    }

    @Test
    @Order(7)
    @DisplayName("Pubblicazione come OSSERVATORE (non permesso)")
    void testPubblicazioneComeOsservatore() {
        // Arrange
        Post post = TestDataBuilder.createTestPost("osservatoretest", "Test osservatore");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(post, osservatoreTest);
        });

        assertTrue(exception.getMessage().contains("AUTORE"));
    }

    @Test
    @Order(8)
    @DisplayName("Errore conversione immagine")
    void testErroreConversioneImmagine() {
        // Arrange
        Post postConDatiInvalidi = new Post(
                "autoretest",
                new byte[0],  // array vuoto
                0,
                "png",
                "Test"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(postConDatiInvalidi, autoreTest);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Errore database durante pubblicazione")
    void testErroreDatabaseDurantePubblicazione() throws SQLException {
        // Arrange
        Post post = TestDataBuilder.createTestPost("autoretest", "Test");
        testConnection.close();

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            postService.createPost(post, autoreTest);
        });
    }
}