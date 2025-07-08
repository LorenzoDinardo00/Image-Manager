package strutturali;

import dao.impl.UserDAOImpl;
import exception.AuthenticationException;
import model.Role;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.BaseTest;
import util.TestDataBuilder;
import util.TestDatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test strutturali per UserDAOImpl
 * Testa i singoli metodi della classe in isolamento
 */
@DisplayName("Test Strutturali - UserDAOImpl")
public class UserDAOImplTest extends BaseTest {

    private UserDAOImpl userDAO;
    private Connection testConnection;

    @BeforeEach
    void setUp() throws SQLException {
        testConnection = TestDatabaseConfig.getTestConnection();
        userDAO = new UserDAOImpl() {
            protected Connection getConnection() throws SQLException {
                return testConnection;
            }
        };
    }

    // ========== TEST findByUsernameAndPassword ==========

    @Test
    @DisplayName("findByUsernameAndPassword - Credenziali corrette")
    void testFindByUsernameAndPassword_CredenzialiCorrette() throws SQLException {
        // Arrange
        User utente = TestDataBuilder.createTestUser("testuser", Role.OSSERVATORE);
        userDAO.registerUser(utente);

        // Act
        User trovato = userDAO.findByUsernameAndPassword("testuser", "password123");

        // Assert
        assertNotNull(trovato);
        assertEquals("testuser", trovato.getUsername());
        assertEquals("password123", trovato.getPassword());
        assertEquals(Role.OSSERVATORE, trovato.getRole());
    }

    @Test
    @DisplayName("findByUsernameAndPassword - Password errata")
    void testFindByUsernameAndPassword_PasswordErrata() throws SQLException {
        // Arrange
        User utente = TestDataBuilder.createTestUser("testuser", Role.OSSERVATORE);
        userDAO.registerUser(utente);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            userDAO.findByUsernameAndPassword("testuser", "passwordSbagliata");
        });
    }

    @Test
    @DisplayName("findByUsernameAndPassword - Username non esistente")
    void testFindByUsernameAndPassword_UsernameNonEsistente() {
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            userDAO.findByUsernameAndPassword("utenteInesistente", "password");
        });
    }

    // ========== TEST registerUser ==========

    @Test
    @DisplayName("registerUser - Inserimento nuovo utente")
    void testRegisterUser_InserimentoValido() throws SQLException {
        // Arrange
        User nuovoUtente = TestDataBuilder.createTestUser("nuovo", Role.AUTORE);

        // Act
        boolean risultato = userDAO.registerUser(nuovoUtente);

        // Assert
        assertTrue(risultato);

        // Verifica che sia stato inserito
        User verificato = userDAO.findByUsernameAndPassword("nuovo", "password123");
        assertNotNull(verificato);
        assertEquals(Role.AUTORE, verificato.getRole());
    }

    @Test
    @DisplayName("registerUser - Gestione SQLException con codice 23505")
    void testRegisterUser_UsernameDuplicato() throws SQLException {
        // Arrange
        User utente1 = TestDataBuilder.createTestUser("duplicato", Role.OSSERVATORE);
        userDAO.registerUser(utente1);

        User utente2 = TestDataBuilder.createTestUser("duplicato", Role.AUTORE);

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.registerUser(utente2);
        });

        // H2 potrebbe usare un codice diverso, verifichiamo il messaggio
        assertTrue(exception.getMessage().contains("Username o Email già esistente") ||
                exception.getMessage().contains("Unique"));
    }

    @Test
    @DisplayName("registerUser - Tutti i campi vengono salvati correttamente")
    void testRegisterUser_TuttiICampi() throws SQLException {
        // Arrange
        User utente = TestDataBuilder.createCustomUser(
                "completo",
                "NomeCompleto",
                "CognomeCompleto",
                java.time.LocalDate.of(1985, 6, 15),
                "3331112222",
                "completo@test.com",
                "pwd123",
                Role.AUTORE
        );

        // Act
        userDAO.registerUser(utente);

        // Assert
        User salvato = userDAO.findByUsernameAndPassword("completo", "pwd123");
        assertEquals("NomeCompleto", salvato.getName());
        assertEquals("CognomeCompleto", salvato.getSurname());
        assertEquals("3331112222", salvato.getCellphone());
        assertEquals("completo@test.com", salvato.getEmail());
        assertEquals(java.time.LocalDate.of(1985, 6, 15), salvato.getDateOfBirth());
    }
}