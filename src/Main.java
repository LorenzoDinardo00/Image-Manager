import java.util.Scanner;
import java.sql.Date;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();

        System.out.println("Benvenuto!");
        System.out.println("Digita 1 per il Login, 2 per la Registrazione:");
        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline residuo

        if (scelta == 1) {
            System.out.print("Inserisci username: ");
            String username = scanner.nextLine();
            System.out.print("Inserisci password: ");
            String password = scanner.nextLine();

            if (authService.login(username, password)) {
                System.out.println("Login effettuato con successo!");
                // Prosegui con il resto dell'applicazione
            } else {
                System.out.println("Credenziali errate.");
            }
        } else if (scelta == 2) {
            System.out.print("Inserisci username: ");
            String username = scanner.nextLine();
            System.out.print("Inserisci nome: ");
            String name = scanner.nextLine();
            System.out.print("Inserisci cognome: ");
            String surname = scanner.nextLine();
            System.out.print("Inserisci data di nascita (YYYY-MM-DD): ");
            String dobStr = scanner.nextLine();
            Date dateOfBirth = Date.valueOf(dobStr); // Assumi che il formato sia corretto
            System.out.print("Inserisci numero di cellulare (opzionale, premi invio per saltare): ");
            String cellphone = scanner.nextLine();
            if (cellphone.isEmpty()) {
                cellphone = null;
            }
            System.out.print("Inserisci email: ");
            String email = scanner.nextLine();
            System.out.print("Inserisci password: ");
            String password = scanner.nextLine();

            User newUser = new User(username, name, surname, dateOfBirth, cellphone, email, password);
            if (authService.register(newUser)) {
                System.out.println("Registrazione avvenuta con successo!");
            } else {
                System.out.println("Errore nella registrazione.");
            }
        } else {
            System.out.println("Scelta non valida.");
        }
        scanner.close();
    }
}