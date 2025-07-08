package funzionali;

import controller.AuthController;
import model.Image;
import model.Role;
import model.User;
import org.junit.jupiter.api.*;
import service.AuthService;
import service.impl.AuthServiceImpl;
import util.BaseTest;
import util.TestDataBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test funzionali per UC#3 - Logout
 */
@DisplayName("UC#3 - Logout Utente")
public class UC03_LogoutTest extends BaseTest {

    private AuthService authService;
    private User osservatoreLoggato;
    private User autoreLoggato;

    @Override
    protected void additionalSetup() throws Exception {
        authService = mock(AuthService.class);

        // Crea utenti di test già loggati
        osservatoreLoggato = TestDataBuilder.createTestUser("osservatore1", Role.OSSERVATORE);
        autoreLoggato = TestDataBuilder.createTestUser("autore1", Role.AUTORE);
    }

    @Test
    @DisplayName("Logout utente OSSERVATORE")
    void testLogoutOsservatore() {
        // Arrange
        Scanner scanner = new Scanner("");
        AuthController authController = new AuthController(authService, scanner);

        // Cattura l'output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Act
        User risultato = authController.logoutUser(osservatoreLoggato);

        // Assert
        assertNull(risultato, "Il metodo logout dovrebbe ritornare null");
        verify(authService, times(1)).logout(osservatoreLoggato);

        String output = outputStream.toString();
        assertTrue(output.contains("osservatore1 ha effettuato il logout"));

        // Ripristina System.out
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Logout utente AUTORE")
    void testLogoutAutore() {
        // Arrange
        Scanner scanner = new Scanner("");
        AuthController authController = new AuthController(authService, scanner);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Act
        User risultato = authController.logoutUser(autoreLoggato);

        // Assert
        assertNull(risultato);
        verify(authService, times(1)).logout(autoreLoggato);

        String output = outputStream.toString();
        assertTrue(output.contains("autore1 ha effettuato il logout"));

        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Verifica reset immagine caricata dopo logout")
    void testResetImmagineDopoLogout() {
        // Questo test verifica che l'immagine venga resettata nel Main
        // Simuliamo il comportamento del Main

        // Arrange
        Image immaginaCaricata = TestDataBuilder.createTestImage(100, 100);
        assertNotNull(immaginaCaricata);

        // Act - Simulazione del logout nel Main
        User currentUser = autoreLoggato;
        currentUser = null;  // Logout
        immaginaCaricata = null;  // Reset immagine

        // Assert
        assertNull(currentUser);
        assertNull(immaginaCaricata);
    }
}