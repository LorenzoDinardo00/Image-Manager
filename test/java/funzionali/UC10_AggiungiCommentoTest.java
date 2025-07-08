package funzionali;

import dao.PostDao;
import dao.CommentDao;
import dao.impl.PostDAOImpl;
import dao.impl.CommentDAOImpl;
import model.Comment;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#10 - Aggiungi Commento
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#10 - Aggiungi Commento")
public class UC10_AggiungiCommentoTest extends BaseTest {

    private PostService postService;
    private Connection testConnection;
    private User osservatore;
    private User autore;
    private Post testPost;

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
                        "VALUES ('osservatore1', 'Oss', 'Uno', '1990-01-01', 'oss1@test.com', 'pass', 'osservatore')," +
                        "('autore1', 'Aut', 'Uno', '1991-01-01', 'aut1@test.com', 'pass', 'autore')"
        );

        osservatore = TestDataBuilder.createTestUser("osservatore1", Role.OSSERVATORE);
        autore = TestDataBuilder.createTestUser("autore1", Role.AUTORE);

        // Crea un post di test
        testPost = TestDataBuilder.createTestPost("autore1", "Post per test commenti");
        postService.createPost(testPost, autore);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Commento da OSSERVATORE")
    void testCommentoDaOsservatore() throws SQLException {
        // Arrange
        Comment commento = new Comment(
                testPost.getPostId(),
                osservatore.getUsername(),
                "Questo è un commento da osservatore"
        );

        // Act
        boolean risultato = postService.addCommentToPost(commento, osservatore);

        // Assert
        assertTrue(risultato);

        List<Comment> commenti = postService.getCommentsForPost(testPost.getPostId());
        assertEquals(1, commenti.size());
        assertEquals("Questo è un commento da osservatore", commenti.get(0).getCommentText());
        assertEquals("osservatore1", commenti.get(0).getCommenterUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Commento da AUTORE")
    void testCommentoDaAutore() throws SQLException {
        // Arrange
        Comment commento = new Comment(
                testPost.getPostId(),
                autore.getUsername(),
                "L'autore commenta il proprio post"
        );

        // Act
        boolean risultato = postService.addCommentToPost(commento, autore);

        // Assert
        assertTrue(risultato);

        List<Comment> commenti = postService.getCommentsForPost(testPost.getPostId());
        assertTrue(commenti.stream()
                .anyMatch(c -> c.getCommentText().equals("L'autore commenta il proprio post")));
    }

    @Test
    @Order(3)
    @DisplayName("Commento con testo lungo")
    void testCommentoTestoLungo() throws SQLException {
        // Arrange
        String testoLungo = "Questo è un commento molto lungo che serve per testare " +
                "se il sistema gestisce correttamente i commenti con molto testo. " +
                "Potrebbe essere una recensione dettagliata o una discussione approfondita " +
                "su un argomento specifico. Il sistema dovrebbe gestirlo senza problemi.";

        Comment commento = new Comment(
                testPost.getPostId(),
                osservatore.getUsername(),
                testoLungo
        );

        // Act
        boolean risultato = postService.addCommentToPost(commento, osservatore);

        // Assert
        assertTrue(risultato);

        List<Comment> commenti = postService.getCommentsForPost(testPost.getPostId());
        Comment commentoSalvato = commenti.stream()
                .filter(c -> c.getCommentText().equals(testoLungo))
                .findFirst()
                .orElse(null);

        assertNotNull(commentoSalvato);
        assertEquals(testoLungo, commentoSalvato.getCommentText());
    }

    @Test
    @Order(4)
    @DisplayName("Visualizzazione timestamp commento")
    void testTimestampCommento() throws SQLException {
        // Arrange
        Comment commento = new Comment(
                testPost.getPostId(),
                autore.getUsername(),
                "Commento con timestamp"
        );

        // Act
        postService.addCommentToPost(commento, autore);

        // Assert
        List<Comment> commenti = postService.getCommentsForPost(testPost.getPostId());
        Comment commentoConTimestamp = commenti.stream()
                .filter(c -> c.getCommentText().equals("Commento con timestamp"))
                .findFirst()
                .orElse(null);

        assertNotNull(commentoConTimestamp);
        assertNotNull(commentoConTimestamp.getCommentedAt());
        assertTrue(commentoConTimestamp.getCommentId() > 0);
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Commento senza essere loggato")
    void testCommentoSenzaLogin() {
        // Arrange
        Comment commento = new Comment(
                testPost.getPostId(),
                "utente_non_loggato",
                "Tentativo di commento"
        );

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            postService.addCommentToPost(commento, null);
        });
    }

    @Test
    @Order(6)
    @DisplayName("Commento con testo vuoto")
    void testCommentoTestoVuoto() {
        // Arrange
        Comment commentoVuoto = new Comment(
                testPost.getPostId(),
                osservatore.getUsername(),
                ""
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.addCommentToPost(commentoVuoto, osservatore);
        });

        assertTrue(exception.getMessage().contains("testo del commento non può essere vuoto"));
    }

    @Test
    @Order(7)
    @DisplayName("Commento a post non esistente")
    void testCommentoPostNonEsistente() {
        // Arrange
        Comment commento = new Comment(
                9999, // ID non esistente
                osservatore.getUsername(),
                "Commento a post inesistente"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.addCommentToPost(commento, osservatore);
        });

        assertTrue(exception.getMessage().contains("Post non trovato"));
    }

    @Test
    @Order(8)
    @DisplayName("Errore database durante inserimento")
    void testErroreDatabaseInserimento() throws SQLException {
        // Arrange
        Comment commento = new Comment(
                testPost.getPostId(),
                autore.getUsername(),
                "Test errore DB"
        );

        testConnection.close();

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            postService.addCommentToPost(commento, autore);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Commento con username diverso dall'utente loggato")
    void testCommentoUsernameDiverso() {
        // Arrange
        Comment commento = new Comment(
                testPost.getPostId(),
                "altro_utente", // Username diverso
                "Tentativo di impersonare altro utente"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.addCommentToPost(commento, osservatore);
        });

        assertTrue(exception.getMessage().contains("autore del commento deve corrispondere"));
    }
}