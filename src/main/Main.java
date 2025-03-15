package main;

import auth.AuthService;
import auth.User;
import image.Image;
import image.ImageManager;

import java.sql.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        boolean isLoggedIn = false;  // Per tracciare se l'utente ha effettuato il login

        // Variabile per tenere in memoria l'ultima immagine caricata
        Image loadedImg = null;

        System.out.println("Benvenuto!");
        int scelta;
        do {
            // Se l'utente è loggato, mostriamo anche l'opzione 3 per caricare l'immagine
            if (isLoggedIn) {
                System.out.println("Digita 0 per uscire, 3 per caricare immagine, 4 per salvare immagine:");
            } else {
                System.out.println("Digita 0 per uscire, 1 per il Login, 2 per la Registrazione:");
            }

            scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline residuo

            // Se non è loggato, gestiamo solo login/registrazione/uscita
            if (!isLoggedIn) {
                if (scelta == 1) {
                    // LOGIN
                    System.out.print("Inserisci username: ");
                    String username = scanner.nextLine();
                    System.out.print("Inserisci password: ");
                    String password = scanner.nextLine();

                    if (authService.login(username, password)) {
                        System.out.println("Login effettuato con successo!");
                        isLoggedIn = true;  // Aggiorniamo lo stato di login
                    } else {
                        System.out.println("Credenziali errate.");
                    }

                } else if (scelta == 2) {
                    // REGISTRAZIONE
                    System.out.print("Inserisci username: ");
                    String username = scanner.nextLine();
                    System.out.print("Inserisci nome: ");
                    String name = scanner.nextLine();
                    System.out.print("Inserisci cognome: ");
                    String surname = scanner.nextLine();
                    System.out.print("Inserisci data di nascita (YYYY-MM-DD): ");
                    String dobStr = scanner.nextLine();
                    Date dateOfBirth = Date.valueOf(dobStr);

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

                } else if (scelta == 0) {
                    System.out.println("Uscita dall'applicazione.");
                } else {
                    System.out.println("Scelta non valida.");
                }

            } else {
                // Se l'utente è loggato, mostriamo opzioni aggiuntive
                if (scelta == 3) {
                    // CARICAMENTO IMMAGINE ORIGINALE
                    if (loadedImg != null) {
                        System.out.println("Attenzione: stai per sovrascrivere l'immagine precedentemente caricata.");
                        System.out.println("Se non l'hai salvata, la perderai. Vuoi continuare? (y/n)");
                        String conferma = scanner.nextLine();
                        if (!conferma.equalsIgnoreCase("y")) {
                            System.out.println("Operazione annullata. L'immagine precedente rimane caricata.");
                            continue;  // Torna al menu senza sovrascrivere
                        }
                    }

                    System.out.print("Inserisci il path dell'immagine da caricare: ");
                    String path = scanner.nextLine();
                    ImageManager manager = new ImageManager();

                    try {
                        Image tempImg = manager.loadImage(path);
                        if (tempImg != null) {
                            loadedImg = tempImg;  // Sovrascriviamo l'immagine caricata
                            System.out.println("Immagine caricata con successo!");
                            // Esempio: mostrare le dimensioni dell'immagine
                            System.out.println("Dimensioni: "
                                    + loadedImg.getWidth() + "x" + loadedImg.getHeight());
                        } else {
                            System.out.println("Impossibile caricare l'immagine. Verifica il percorso o il formato.");
                        }
                    } catch (Exception e) {
                        System.out.println("Errore durante il caricamento dell'immagine: " + e.getMessage());
                    }

                } else if (scelta == 4) {
                    if (loadedImg == null) {
                        System.out.println("Nessuna immagine caricata. Carica prima un'immagine.");
                    } else {
                        System.out.print("Inserisci il path della cartella dove salvare l'immagine: ");
                        String folderPath = scanner.nextLine();
                        ImageManager manager = new ImageManager();
                        boolean saved = manager.saveImage(loadedImg, folderPath);
                        if (saved) {
                            System.out.println("Immagine salvata con successo!");
                        } else {
                            System.out.println("Errore nel salvataggio dell'immagine.");
                        }
                    }

                } else if (scelta == 0) {
                    System.out.println("Uscita dall'applicazione.");
                } else {
                    System.out.println("Scelta non valida.");
                }
            }
        } while (scelta != 0);

        scanner.close();
    }
}