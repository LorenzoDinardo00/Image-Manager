package main; // Assumendo che Main sia nel package root del progetto refactorizzato

import controller.AuthController;
import controller.ImageController;
import controller.PostController;
import dao.CommentDao; // Importa l'interfaccia
import dao.PostDao;    // Importa l'interfaccia
import dao.UserDao;    // Importa l'interfaccia
import dao.impl.CommentDAOImpl;
import dao.impl.PostDAOImpl;
import dao.impl.UserDAOImpl;
import model.Image;
import model.Role;
import model.User;
import service.AuthService; // Importa l'interfaccia
import service.ImageService; // Importa l'interfaccia
import service.PostService;  // Importa l'interfaccia
import service.impl.AuthServiceImpl;
import service.impl.ImageServiceImpl;
import service.impl.PostServiceImpl;
import util.DatabaseConnection; // Per chiudere la connessione alla fine

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static User currentUser = null;
    private static Image loadedImg = null; // Immagine attualmente caricata in memoria

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- Setup delle Dipendenze (Dependency Injection manuale) ---

        // 1. Creazione dei DAO (implementazioni concrete)
        UserDao userDAO = new UserDAOImpl();
        PostDao postDAO = new PostDAOImpl();
        CommentDao commentDAO = new CommentDAOImpl();

        // 2. Creazione dei Service (implementazioni concrete, con iniezione dei DAO)
        AuthService authService = new AuthServiceImpl(userDAO);
        PostService postService = new PostServiceImpl(postDAO, commentDAO);
        ImageService imageService = new ImageServiceImpl();

        // 3. Creazione dei Controller (con iniezione dei Service)
        AuthController authController = new AuthController(authService, scanner);
        ImageController imageController = new ImageController(imageService, scanner);
        PostController postController = new PostController(postService, scanner);

        // --- Fine Setup Dipendenze ---


        System.out.println("Benvenuto all'Image Manager (Refactored)!");
        int scelta;

        do {
            if (currentUser == null) {
                displayVisitorMenu();
            } else {
                displayUserMenu(currentUser);
            }
            System.out.print("Scegli un'opzione: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Input non valido. Per favore inserisci un numero.");
                scanner.next();
                scelta = -1;
                continue;
            }
            scelta = scanner.nextInt();
            scanner.nextLine();

            if (currentUser == null) {
                handleVisitorActions(scelta, authController, postController);
            } else {
                handleUserActions(scelta, authController, imageController, postController);
            }

        } while (scelta != 0);

        scanner.close();
        try {
            DatabaseConnection.getInstance().close();
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura della connessione al database: " + e.getMessage());
        }
        System.out.println("Arrivederci!");
    }

    private static void displayVisitorMenu() {
        System.out.println("\n--- Menu Principale (Visitatore) ---");
        System.out.println("1. Login");
        System.out.println("2. Registrazione");
        System.out.println("7. Visualizza Elenco Post");
        System.out.println("0. Esci");
    }

    private static void displayUserMenu(User user) {
        System.out.println("\n--- Menu Utente: " + user.getUsername() + " (" + user.getRole() + ") ---");
        if (user.getRole() == Role.AUTORE) {
            System.out.println("3. Carica Immagine");
            System.out.println("4. Salva Immagine Caricata");
            System.out.println("5. Modifica Immagine Caricata");
            System.out.println("8. Pubblica Immagine Caricata come Post");
        }
        System.out.println("7. Visualizza Elenco Post");
        if (user.getRole() == Role.OSSERVATORE) {
            System.out.println("Come OSSERVATORE, hai accesso limitato alle funzionalità di creazione.");
        }
        System.out.println("6. Logout");
        System.out.println("0. Esci dall'applicazione");
    }

    private static void handleVisitorActions(int scelta, AuthController authController, PostController postController) {
        switch (scelta) {
            case 1: // LOGIN
                currentUser = authController.loginUser();
                break;
            case 2: // REGISTRAZIONE
                authController.registerUser();
                break;
            case 7: // VISUALIZZA ELENCO POST
                postController.viewAllPosts(currentUser); // currentUser sarà null qui
                break;
            case 0: // USCITA
                break;
            default:
                System.out.println("Scelta non valida.");
                break;
        }
    }

    private static void handleUserActions(int scelta, AuthController authController,
                                          ImageController imageController, PostController postController) {
        // Gestione specifica per AUTORE
        if (currentUser.getRole() == Role.AUTORE) {
            switch (scelta) {
                case 3: // CARICA IMMAGINE
                    if (loadedImg != null) {
                        System.out.println("Attenzione: un'immagine è già caricata. Se carichi una nuova immagine, quella precedente verrà sovrascritta (se non salvata/pubblicata).");
                        System.out.print("Vuoi continuare? (y/n): ");
                        Scanner tempScanner = new Scanner(System.in);
                        if (!tempScanner.nextLine().equalsIgnoreCase("y")) {
                            System.out.println("Caricamento annullato.");
                            break;
                        }
                    }
                    loadedImg = imageController.loadImage();
                    break;
                case 4: // SALVA IMMAGINE CARICATA (LOCALE)
                    imageController.saveLoadedImage(loadedImg);
                    break;
                case 5: // MODIFICA IMMAGINE CARICATA
                    Image potentiallyModifiedImage = imageController.modifyLoadedImage(loadedImg);
                    if (potentiallyModifiedImage != null) {
                        loadedImg = potentiallyModifiedImage;
                    }
                    break;
                case 8: // PUBBLICA IMMAGINE CARICATA COME POST
                    postController.publishLoadedImageAsPost(loadedImg, currentUser);
                    break;
            }
        }

        boolean actionHandled = false;
        if (currentUser.getRole() == Role.AUTORE && (scelta == 3 || scelta == 4 || scelta == 5 || scelta == 8)) {
            actionHandled = true;
        }


        switch (scelta) {
            case 7: // VISUALIZZA ELENCO POST (Accessibile da tutti gli utenti loggati)
                postController.viewAllPosts(currentUser);
                actionHandled = true;
                break;
            case 6: // LOGOUT
                currentUser = authController.logoutUser(currentUser);
                loadedImg = null; // Resetta l'immagine caricata al logout
                actionHandled = true;
                break;
            case 0: // USCITA
                actionHandled = true;
                break;
            default:
                if (!actionHandled) {
                    if (currentUser.getRole() == Role.OSSERVATORE && (scelta == 3 || scelta == 4 || scelta == 5 || scelta == 8)) {
                        System.out.println("Scelta non valida per OSSERVATORE.");
                    } else {
                        System.out.println("Scelta non valida.");
                    }
                }
                break;
        }
    }
}