package funzionali;

import dao.UserDao;
import dao.impl.UserDAOImpl;
import exception.AuthenticationException;
import model.Role;
import model.User;
import org.junit.jupiter.api.*;
import service.AuthService;
import service.impl.AuthServiceImpl;
import util.BaseTest;
import util.TestDataBuilder;
import util.TestDatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#2 - Login
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#2 - Login Utente")
public class UC02_LoginTest extends BaseTest {

    private AuthService authService;
    private Connection testConnection;

    @Override
    protected void additionalSetup() throws Exception {
        testConnection = TestDatabaseConfig.getTestConnection();

        UserDao userDao = new UserDAOImpl() {
            protected Connection getConnection() throws SQLException {
                return testConnection;
            }
        };

        authService = new AuthServiceImpl(userDao);

        // Pre-registra alcuni utenti per i test
        User osservatore = TestDataBuilder.createTestUser("osservatore1", Role.OSSERVATORE);
        User autore = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
        authService.register(osservatore);
        authService.register(autore);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Login con credenziali corrette (OSSERVATORE)")
    void testLoginOsservatoreValido() throws SQLException, AuthenticationException {
        // Act
        User loggedUser = authService.login("osservatore1", "password123");

        // Assert
        assertNotNull(loggedUser);
        assertEquals("osservatore1", loggedUser.getUsername());
        assertEquals(Role.OSSERVATORE, loggedUser.getRole());
        assertNotNull(loggedUser.getName());
        assertNotNull(loggedUser.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("Login con credenziali corrette (AUTORE)")
    void testLoginAutoreValido() throws SQLException, AuthenticationException {
        // Act
        User loggedUser = authService.login("autore1", "password123");

        // Assert
        assertNotNull(loggedUser);
        assertEquals("autore1", loggedUser.getUsername());
        assertEquals(Role.AUTORE, loggedUser.getRole());
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(3)
    @DisplayName("Login con username non esistente")
    void testLoginUsernameNonEsistente() {
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login("utenteInesistente", "password123");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Login con password errata")
    void testLoginPasswordErrata() {
        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login("osservatore1", "passwordSbagliata");
        });

        assertTrue(exception.getMessage().contains("Credenziali non valide"));
    }

    @Test
    @Order(5)
    @DisplayName("Login con username vuoto")
    void testLoginUsernameVuoto() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("", "password123");
        });
    }

    @Test
    @Order(6)
    @DisplayName("Login con password vuota")
    void testLoginPasswordVuota() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("osservatore1", "");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Login con errore di connessione al database (simulato)")
    void testLoginErroreDatabase() throws SQLException {
        // Arrange - Chiudiamo la connessione per simulare un errore
        testConnection.close();

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            authService.login("osservatore1", "password123");
        });
    }
}