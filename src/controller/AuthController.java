package controller;

import model.User;
import model.Role;
import service.AuthService;
import exception.AuthenticationException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AuthController {
    private final AuthService authService;
    private final Scanner scanner;

    public AuthController(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    public User loginUser() {
        System.out.print("Inserisci username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Inserisci password: ");
        String loginPassword = scanner.nextLine();
        try {
            User user = authService.login(loginUsername, loginPassword);
            System.out.println("Login effettuato con successo! Benvenuto " + user.getName());
            return user;
        } catch (AuthenticationException e) {
            System.out.println("Login fallito: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Errore del database durante il login: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Errore nei dati inseriti: " + e.getMessage());
        }
        return null;
    }

    public void registerUser() {
        System.out.print("Inserisci username: ");
        String regUsername = scanner.nextLine();
        System.out.print("Inserisci nome: ");
        String regName = scanner.nextLine();
        System.out.print("Inserisci cognome: ");
        String regSurname = scanner.nextLine();

        LocalDate regDateOfBirth = null;
        while (regDateOfBirth == null) {
            System.out.print("Inserisci data di nascita (YYYY-MM-DD): ");
            String dobStr = scanner.nextLine();
            try {
                regDateOfBirth = LocalDate.parse(dobStr);
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido. Usa YYYY-MM-DD.");
            }
        }

        System.out.print("Inserisci numero di cellulare (opzionale, premi invio per saltare): ");
        String regCellphone = scanner.nextLine();
        if (regCellphone.trim().isEmpty()) {
            regCellphone = null;
        }

        System.out.print("Inserisci email: ");
        String regEmail = scanner.nextLine();
        System.out.print("Inserisci password: ");
        String regPassword = scanner.nextLine();

        Role regRole = null;
        while (regRole == null) {
            System.out.print("Inserisci ruolo (osservatore/autore): ");
            String ruoloStr = scanner.nextLine();
            try {
                regRole = Role.fromInputString(ruoloStr);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        User newUser = new User(regUsername, regName, regSurname, regDateOfBirth, regCellphone, regEmail, regPassword, regRole);

        try {
            if (authService.register(newUser)) {
                System.out.println("Registrazione avvenuta con successo! Ora puoi effettuare il login.");
            } else {
                System.out.println("Errore sconosciuto nella registrazione.");
            }
        } catch (SQLException e) {
            System.err.println("Errore del database durante la registrazione: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Errore nei dati per la registrazione: " + e.getMessage());
        }
    }

    public User logoutUser(User currentUser) {
        if (currentUser != null) {
            authService.logout(currentUser);
            System.out.println(currentUser.getUsername() + " ha effettuato il logout.");
        }
        return null;
    }
}