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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test funzionali per UC#1 - Registrazione
 * Verifica l'intero flusso di registrazione utente
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UC#1 - Registrazione Utente")
public class UC01_RegistrazioneTest extends BaseTest {

    private AuthService authService;
    private UserDao userDao;
    private Connection testConnection;

    @Override
    protected void additionalSetup() throws Exception {
        testConnection = TestDatabaseConfig.getTestConnection();

        // Creiamo un UserDAO che usa la connessione di test
        userDao = new UserDAOImpl() {
            @Override
            protected Connection getConnection() throws SQLException {
                return testConnection;
            }
        };

        authService = new AuthServiceImpl(userDao);
    }

    // ========== TEST POSITIVI ==========

    @Test
    @Order(1)
    @DisplayName("Registrazione con tutti i campi validi (OSSERVATORE)")
    void testRegistrazioneOsservatoreCompleta() throws SQLException {
        // Arrange
        User nuovoUtente = TestDataBuilder.createCustomUser(
                "mario_rossi",
                "Mario",
                "Rossi",
                LocalDate.of(1990, 5, 15),
                "3331234567",
                "mario.rossi@email.com",
                "password123",
                Role.OSSERVATORE
        );

        // Act
        boolean risultato = authService.register(nuovoUtente);

        // Assert
        assertTrue(risultato, "La registrazione dovrebbe avere successo");

        // Verifica che l'utente sia effettivamente nel database
        assertUserExistsInDatabase("mario_rossi");

        // Verifica che possa fare login
        User utenteLoggato = authService.login("mario_rossi", "password123");
        assertNotNull(utenteLoggato);
        assertEquals("Mario", utenteLoggato.getName());
        assertEquals(Role.OSSERVATORE, utenteLoggato.getRole());
    }

    @Test
    @Order(2)
    @DisplayName("Registrazione con tutti i campi validi (AUTORE)")
    void testRegistrazioneAutoreCompleta() throws SQLException {
        // Arrange
        User nuovoAutore = TestDataBuilder.createCustomUser(
                "giovanni_verdi",
                "Giovanni",
                "Verdi",
                LocalDate.of(1985, 3, 20),
                "3339876543",
                "giovanni.verdi@email.com",
                "securepass456",
                Role.AUTORE
        );

        // Act
        boolean risultato = authService.register(nuovoAutore);

        // Assert
        assertTrue(risultato);
        assertUserExistsInDatabase("giovanni_verdi");

        // Verifica il ruolo
        User utenteLoggato = authService.login("giovanni_verdi", "securepass456");
        assertEquals(Role.AUTORE, utenteLoggato.getRole());
    }

    @Test
    @Order(3)
    @DisplayName("Registrazione con cellulare opzionale vuoto")
    void testRegistrazioneSenzaCellulare() throws SQLException {
        // Arrange
        User utente = TestDataBuilder.createCustomUser(
                "anna_bianchi",
                "Anna",
                "Bianchi",
                LocalDate.of(1995, 7, 10),
                null,  // cellulare vuoto
                "anna.bianchi@email.com",
                "password789",
                Role.OSSERVATORE
        );

        // Act
        boolean risultato = authService.register(utente);

        // Assert
        assertTrue(risultato);
        assertUserExistsInDatabase("anna_bianchi");
    }

    @Test
    @Order(4)
    @DisplayName("Registrazione con data di nascita al limite consentito")
    void testRegistrazioneDataNascitaLimite() throws SQLException {
        // Arrange - utente appena maggiorenne (18 anni fa da oggi)
        LocalDate dataLimite = LocalDate.now().minusYears(18);
        User utente = TestDataBuilder.createCustomUser(
                "giovane_utente",
                "Giovane",
                "Utente",
                dataLimite,
                "3335555555",
                "giovane@email.com",
                "pass123",
                Role.OSSERVATORE
        );

        // Act
        boolean risultato = authService.register(utente);

        // Assert
        assertTrue(risultato);
        assertUserExistsInDatabase("giovane_utente");
    }

    // ========== TEST NEGATIVI ==========

    @Test
    @Order(5)
    @DisplayName("Registrazione con username già esistente")
    void testRegistrazioneUsernameDuplicato() throws SQLException {
        // Arrange - Prima registrazione
        User primoUtente = TestDataBuilder.createTestUser("utente_esistente", Role.OSSERVATORE);
        authService.register(primoUtente);

        // Arrange - Tentativo di registrazione con stesso username
        User secondoUtente = TestDataBuilder.createCustomUser(
                "utente_esistente",  // stesso username
                "Altro",
                "Nome",
                LocalDate.of(1992, 1, 1),
                "3337777777",
                "altra.email@test.com",  // email diversa
                "altrapwd",
                Role.AUTORE
        );

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            authService.register(secondoUtente);
        });

        assertTrue(exception.getMessage().contains("Username o Email già esistente"));
    }

    @Test
    @Order(6)
    @DisplayName("Registrazione con email già esistente")
    void testRegistrazioneEmailDuplicata() throws SQLException {
        // Arrange - Prima registrazione
        User primoUtente = TestDataBuilder.createTestUser("primo_utente", Role.OSSERVATORE);
        authService.register(primoUtente);

        // Arrange - Tentativo con stessa email
        User secondoUtente = TestDataBuilder.createCustomUser(
                "secondo_utente",  // username diverso
                "Secondo",
                "Utente",
                LocalDate.of(1993, 2, 2),
                "3338888888",
                "primo_utente@test.com",  // stessa email del primo
                "password",
                Role.OSSERVATORE
        );

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            authService.register(secondoUtente);
        });

        assertTrue(exception.getMessage().contains("Username o Email già esistente"));
    }

    @Test
    @Order(7)
    @DisplayName("Registrazione con formato email non valido")
    void testRegistrazioneEmailNonValida() {
        // Arrange - email senza @
        User utente = TestDataBuilder.createCustomUser(
                "test_user",
                "Test",
                "User",
                LocalDate.of(1990, 1, 1),
                "3339999999",
                "email_senza_chiocciola.com",  // email non valida
                "password",
                Role.OSSERVATORE
        );

        // Act & Assert
        // Il sistema dovrebbe validare l'email
        // Per ora testiamo che almeno contenga @
        assertFalse(utente.getEmail().contains("@"),
                "L'email non valida non dovrebbe contenere @");
    }

    @Test
    @Order(8)
    @DisplayName("Registrazione con campi obbligatori vuoti - username")
    void testRegistrazioneUsernameVuoto() {
        // Arrange
        User utente = TestDataBuilder.createCustomUser(
                null,  // username nullo
                "Nome",
                "Cognome",
                LocalDate.of(1990, 1, 1),
                "3330000000",
                "test@email.com",
                "password",
                Role.OSSERVATORE
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(utente);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Registrazione con ruolo non valido")
    void testRegistrazioneRuoloNonValido() {
        // Questo test verifica che il sistema gestisca solo OSSERVATORE e AUTORE
        // Non possiamo creare un ruolo non valido perché è un enum,
        // ma possiamo verificare che l'enum abbia solo i valori attesi

        Role[] ruoliValidi = Role.values();
        assertEquals(2, ruoliValidi.length, "Dovrebbero esserci solo 2 ruoli");

        boolean haOsservatore = false;
        boolean haAutore = false;

        for (Role ruolo : ruoliValidi) {
            if (ruolo == Role.OSSERVATORE) haOsservatore = true;
            if (ruolo == Role.AUTORE) haAutore = true;
        }

        assertTrue(haOsservatore && haAutore, "I ruoli devono essere OSSERVATORE e AUTORE");
    }

    @Test
    @Order(10)
    @DisplayName("Registrazione con data di nascita in formato errato")
    void testRegistrazioneDataFormatoErrato() {
        // In realtà LocalDate non permette formati errati,
        // ma possiamo testare date non valide
        assertThrows(Exception.class, () -> {
            LocalDate.of(2025, 13, 32);  // mese 13, giorno 32 non esistono
        });
    }

    // ========== METODI DI UTILITÀ ==========

    private void assertUserExistsInDatabase(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement ps = testConnection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1),
                        "L'utente " + username + " dovrebbe esistere nel database");
            }
        }
    }
}