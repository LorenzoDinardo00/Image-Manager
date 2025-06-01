package controller;

import model.Post;
import model.Comment;
import model.User;
import model.Image;
import service.PostService;
import util.ImageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class PostController {
    private final PostService postService;
    private final Scanner scanner;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Definiamo qui la cartella di salvataggio per le immagini dei post visualizzate
    private static final String VIEWED_POST_IMAGES_FOLDER = "immagini_post_visualizzate";

    public PostController(PostService postService, Scanner scanner) {
        this.postService = postService;
        this.scanner = scanner;
    }

    public void publishLoadedImageAsPost(Image loadedImg, User currentUser) {
        if (loadedImg == null || loadedImg.getBufferedImage() == null) {
            System.out.println("Nessuna immagine valida caricata in memoria da pubblicare.");
            return;
        }
        if (currentUser == null) {
            System.out.println("Devi essere loggato per pubblicare un post.");
            return;
        }

        System.out.print("Inserisci una descrizione per il post: ");
        String description = scanner.nextLine();
        String imageFormat = "png";

        byte[] imageData = ImageUtils.bufferedImageToBytes(loadedImg.getBufferedImage(), imageFormat);

        if (imageData != null) {
            long imageSize = imageData.length;
            Post newPost = new Post(
                    currentUser.getUsername(),
                    imageData,
                    imageSize,
                    imageFormat,
                    description
            );

            try {
                if (postService.createPost(newPost, currentUser)) {
                    System.out.println("Post pubblicato con successo nel database! ID Post: " + newPost.getPostId());
                } else {
                    System.out.println("Errore nella pubblicazione del post (PostService ha restituito false).");
                }
            } catch (SQLException e) {
                System.err.println("Errore del database durante la pubblicazione del post: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Errore nei dati del post: " + e.getMessage());
            }
        } else {
            System.out.println("Errore: impossibile convertire l'immagine in dati binari per il post.");
        }
    }

    public void viewAllPosts(User currentUser) {
        System.out.println("\n--- Elenco dei Post ---");
        try {
            List<Post> posts = postService.getAllPosts();
            if (posts.isEmpty()) {
                System.out.println("Nessun post da visualizzare al momento.");
                return;
            }

            for (Post p : posts) {
                System.out.println("------------------------------------");
                System.out.println("Post ID: " + p.getPostId() + " | Autore: " + p.getAuthorUsername());
                System.out.println("Descrizione: " + p.getDescription());
                System.out.println("Likes: " + p.getLikesCount() + " | Data: " + (p.getCreatedAt() != null ? p.getCreatedAt().format(dateTimeFormatter) : "N/D"));
            }
            System.out.println("------------------------------------");

            interactWithPosts(posts, currentUser);

        } catch (SQLException e) {
            System.err.println("Errore del database durante la visualizzazione dei post: " + e.getMessage());
        }
    }

    private void interactWithPosts(List<Post> posts, User currentUser) {
        boolean continueInteractingThisSection = true;
        while (continueInteractingThisSection) {
            System.out.print("\nInserisci l'ID del post da visualizzare e con cui interagire (0 per tornare al menu precedente): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Input non valido. Inserisci un numero.");
                scanner.next();
                continue;
            }
            int postIdToInteract = scanner.nextInt();
            scanner.nextLine();

            if (postIdToInteract == 0) {
                continueInteractingThisSection = false;
                break;
            }

            Post selectedPost = posts.stream().filter(p -> p.getPostId() == postIdToInteract).findFirst().orElse(null); //

            if (selectedPost == null) {

                System.out.println("ID Post non trovato nella lista corrente.");
                continue;
            }

            displayPostDetails(selectedPost);
            displayAndHandlePostActions(selectedPost, currentUser);
        }
    }

    private void displayPostDetails(Post post) {
        System.out.println("\n--- Dettaglio Post ID: " + post.getPostId() + " ---");
        System.out.println("Autore: " + post.getAuthorUsername());
        System.out.println("Descrizione: " + post.getDescription());
        System.out.println("Formato Img: " + post.getImageFormat() + ", Dimensione: " + post.getImageSize() + " bytes");
        System.out.println("Likes: " + post.getLikesCount());
        System.out.println("Data Creazione: " + (post.getCreatedAt() != null ? post.getCreatedAt().format(dateTimeFormatter) : "N/D"));

        String viewedImagePath = saveBytesToLocalFolder(post.getImageData(),
                VIEWED_POST_IMAGES_FOLDER,
                "post_visualizzato_" + post.getPostId(),
                post.getImageFormat());
        if (viewedImagePath != null) {
            System.out.println("L'immagine del post è disponibile in: " + viewedImagePath);
            System.out.println("Puoi aprirla con un visualizzatore di immagini.");
        } else {
            System.out.println("Impossibile salvare/visualizzare l'immagine del post.");
        }

        try {
            List<Comment> comments = postService.getCommentsForPost(post.getPostId());
            System.out.println("Commenti (" + comments.size() + "):");
            if (!comments.isEmpty()) {
                for (Comment comment : comments) {
                    System.out.println("  [" + comment.getCommenterUsername() + " - " +
                            (comment.getCommentedAt() != null ? comment.getCommentedAt().format(dateTimeFormatter) : "N/D") + "]: " +
                            comment.getCommentText());
                }
            } else {
                System.out.println("  Nessun commento per questo post.");
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Errore nel recuperare i commenti: " + e.getMessage());
        }
    }

    private void displayAndHandlePostActions(Post selectedPost, User currentUser) {
        System.out.println("\nAzioni per il post ID " + selectedPost.getPostId() + ":");
        System.out.println("1. Metti Like");
        if (currentUser != null) {
            System.out.println("2. Aggiungi Commento");
        }
        System.out.println("0. Torna all'elenco post / Scegli un altro post");
        System.out.print("Scegli azione: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Input non valido.");
            scanner.next();
            return;
        }
        int actionChoice = scanner.nextInt();
        scanner.nextLine();
        try {
            switch (actionChoice) {
                case 1:
                    if (postService.addLikeToPost(selectedPost.getPostId(), currentUser)) {
                        System.out.println("Like aggiunto al post ID: " + selectedPost.getPostId());
                        selectedPost.setLikesCount(selectedPost.getLikesCount() + 1);
                    } else {
                        System.out.println("Errore nell'aggiungere il like (PostService ha restituito false).");
                    }
                    break;
                case 2:
                    if (currentUser != null) {
                        System.out.print("Inserisci il tuo commento: ");
                        String commentText = scanner.nextLine();
                        Comment newComment = new Comment(selectedPost.getPostId(), currentUser.getUsername(), commentText);
                        if (postService.addCommentToPost(newComment, currentUser)) {
                            System.out.println("Commento aggiunto al post ID: " + selectedPost.getPostId());
                        } else {
                            System.out.println("Errore nell'aggiungere il commento (PostService ha restituito false).");
                        }
                    } else {
                        System.out.println("Devi essere loggato per commentare. Azione non valida.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Azione non valida.");
                    break;
            }
        } catch (SQLException e) {
            System.err.println("Errore database durante l'interazione con il post: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Errore nei dati per l'interazione con il post: " + e.getMessage());
        }
    }


    private String saveBytesToLocalFolder(byte[] imageBytes, String subfolderName, String baseName, String format) {
        if (imageBytes == null || format == null) return null;
        try {
            String currentDir = System.getProperty("user.dir");
            Path folderPath = Paths.get(currentDir, subfolderName);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String fileName = baseName.replaceAll("[^a-zA-Z0-9-_\\.]", "_") + "_" + System.currentTimeMillis() + "." + format.toLowerCase();
            Path filePath = folderPath.resolve(fileName);
            Files.write(filePath, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio dell'immagine nella cartella locale (" + subfolderName + "): " + e.getMessage());
            return null;
        }
    }
}