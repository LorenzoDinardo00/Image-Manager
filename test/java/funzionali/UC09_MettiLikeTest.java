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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#9 - Metti Like
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#9 - Metti Like")
public class UC09_MettiLikeTest extends BaseTest {

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
        testPost = TestDataBuilder.createTestPost("autore1", "Post per test like");
        postService.createPost(testPost, autore);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Like da OSSERVATORE")
    void testLikeDaOsservatore() throws SQLException {
        // Arrange
        int likesIniziali = testPost.getLikesCount();

        // Act
        boolean risultato = postService.addLikeToPost(testPost.getPostId(), osservatore);

        // Assert
        assertTrue(risultato);

        // Verifica che il like sia stato incrementato nel database
        Post postAggiornato = postService.getPostById(testPost.getPostId());
        assertEquals(likesIniziali + 1, postAggiornato.getLikesCount());
    }

    @Test
    @Order(2)
    @DisplayName("Like da AUTORE")
    void testLikeDaAutore() throws SQLException {
        // Arrange
        Post altroPosts = TestDataBuilder.createTestPost("autore1", "Altro post");
        postService.createPost(altroPosts, autore);

        // Act
        boolean risultato = postService.addLikeToPost(altroPosts.getPostId(), autore);

        // Assert
        assertTrue(risultato);

        Post postAggiornato = postService.getPostById(altroPosts.getPostId());
        assertEquals(1, postAggiornato.getLikesCount());
    }

    @Test
    @Order(3)
    @DisplayName("Incremento contatore like")
    void testIncrementoContatoreLike() throws SQLException {
        // Arrange
        Post post = TestDataBuilder.createTestPost("autore1", "Post multipli like");
        postService.createPost(post, autore);
        assertEquals(0, post.getLikesCount());

        // Act - Aggiungi multipli like
        postService.addLikeToPost(post.getPostId(), osservatore);
        postService.addLikeToPost(post.getPostId(), autore);
        postService.addLikeToPost(post.getPostId(), null); // Like anonimo

        // Assert
        Post postAggiornato = postService.getPostById(post.getPostId());
        assertEquals(3, postAggiornato.getLikesCount());
    }

    @Test
    @Order(4)
    @DisplayName("Aggiornamento visualizzazione")
    void testAggiornamentoVisualizzazione() throws SQLException {
        // Arrange
        Post post = TestDataBuilder.createTestPost("autore1", "Post test aggiornamento");
        postService.createPost(post, autore);

        // Act
        postService.addLikeToPost(post.getPostId(), osservatore);

        // Assert - Verifica che il post nella lista sia aggiornato
        var posts = postService.getAllPosts();
        Post postNellaLista = posts.stream()
                .filter(p -> p.getPostId() == post.getPostId())
                .findFirst()
                .orElse(null);

        assertNotNull(postNellaLista);
        assertEquals(1, postNellaLista.getLikesCount());
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Like a post non esistente")
    void testLikePostNonEsistente() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.addLikeToPost(9999, osservatore); // ID non esistente
        });

        assertTrue(exception.getMessage().contains("Post non trovato"));
    }

    @Test
    @Order(6)
    @DisplayName("Errore database durante like")
    void testErroreDatabaseDuranteLike() throws SQLException {
        // Arrange
        int postId = testPost.getPostId();
        testConnection.close();

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            postService.addLikeToPost(postId, osservatore);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Like con ID post non valido")
    void testLikeIdPostNonValido() {
        // Act & Assert - ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            postService.addLikeToPost(-1, osservatore);
        });

        // Act & Assert - ID zero
        assertThrows(IllegalArgumentException.class, () -> {
            postService.addLikeToPost(0, osservatore);
        });
    }
}