package funzionali;

import dao.PostDao;
import dao.CommentDao;
import dao.impl.PostDAOImpl;
import dao.impl.CommentDAOImpl;
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
 * Test funzionali per UC#4 - Visualizza Elenco Post
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#4 - Visualizza Elenco Post")
public class UC04_VisualizzaPostTest extends BaseTest {

    private PostService postService;
    private Connection testConnection;

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

        // Pre-inserisce alcuni utenti nel database
        TestDatabaseConfig.getTestConnection().createStatement().execute(
                "INSERT INTO users (username, name, surname, dateofbirth, email, password, role) " +
                        "VALUES ('autore1', 'Nome', 'Cognome', '1990-01-01', 'autore1@test.com', 'pass', 'autore')," +
                        "('autore2', 'Nome2', 'Cognome2', '1991-01-01', 'autore2@test.com', 'pass', 'autore')"
        );
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Visualizzazione con post presenti nel sistema")
    void testVisualizzazioneConPost() throws SQLException {
        // Arrange - Crea alcuni post
        Post post1 = TestDataBuilder.createTestPost("autore1", "Primo post");
        Post post2 = TestDataBuilder.createTestPost("autore2", "Secondo post");

        User autore = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
        postService.createPost(post1, autore);

        autore.setUsername("autore2");
        postService.createPost(post2, autore);

        // Act
        List<Post> posts = postService.getAllPosts();

        // Assert
        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertTrue(posts.stream().anyMatch(p -> p.getDescription().equals("Primo post")));
        assertTrue(posts.stream().anyMatch(p -> p.getDescription().equals("Secondo post")));
    }

    @Test
    @Order(2)
    @DisplayName("Visualizzazione come VISITATORE")
    void testVisualizzazioneVisitatore() throws SQLException {
        // Arrange
        Post post = TestDataBuilder.createTestPost("autore1", "Post visibile a tutti");
        User autore = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
        postService.createPost(post, autore);

        // Act - Visitatore non loggato visualizza i post
        List<Post> posts = postService.getAllPosts();

        // Assert
        assertFalse(posts.isEmpty());
        assertEquals(1, posts.size());
    }

    @Test
    @Order(3)
    @DisplayName("Visualizzazione come OSSERVATORE")
    void testVisualizzazioneOsservatore() throws SQLException {
        // Arrange
        Post post = TestDataBuilder.createTestPost("autore1", "Post per test osservatore");
        User autore = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
        postService.createPost(post, autore);

        // Act
        List<Post> posts = postService.getAllPosts();

        // Assert
        assertFalse(posts.isEmpty());
        // Un osservatore può vedere tutti i post
        assertEquals("Post per test osservatore", posts.get(0).getDescription());
    }

    @Test
    @Order(4)
    @DisplayName("Visualizzazione come AUTORE")
    void testVisualizzazioneAutore() throws SQLException {
        // Arrange
        Post post1 = TestDataBuilder.createTestPost("autore1", "Mio post");
        Post post2 = TestDataBuilder.createTestPost("autore2", "Post di altro autore");

        User autore1 = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
        User autore2 = TestDataBuilder.createTestUser("autore2", Role.AUTORE);

        postService.createPost(post1, autore1);
        autore2.setUsername("autore2");
        postService.createPost(post2, autore2);

        // Act
        List<Post> posts = postService.getAllPosts();

        // Assert
        assertEquals(2, posts.size());
        // Un autore può vedere sia i propri post che quelli degli altri
    }

    @Test
    @Order(5)
    @DisplayName("Ordinamento post per data (più recenti prima)")
    void testOrdinamentoPerData() throws SQLException, InterruptedException {
        // Arrange
        Post post1 = TestDataBuilder.createTestPost("autore1", "Post vecchio");
        User autore = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
        postService.createPost(post1, autore);

        Thread.sleep(100); // Piccola pausa per garantire timestamp diversi

        Post post2 = TestDataBuilder.createTestPost("autore1", "Post nuovo");
        postService.createPost(post2, autore);

        // Act
        List<Post> posts = postService.getAllPosts();

        // Assert
        assertEquals(2, posts.size());
        assertEquals("Post nuovo", posts.get(0).getDescription());
        assertEquals("Post vecchio", posts.get(1).getDescription());
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(6)
    @DisplayName("Visualizzazione con database vuoto")
    void testVisualizzazioneDatabaseVuoto() throws SQLException {
        // Act
        List<Post> posts = postService.getAllPosts();

        // Assert
        assertNotNull(posts);
        assertTrue(posts.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Gestione errore database durante visualizzazione")
    void testErroreDatabaseDuranteVisualizzazione() throws SQLException {
        // Arrange - Chiudiamo la connessione per simulare un errore
        testConnection.close();

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            postService.getAllPosts();
        });
    }
}