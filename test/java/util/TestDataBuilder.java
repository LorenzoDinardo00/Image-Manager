package util;

import model.User;
import model.Post;
import model.Comment;
import model.Role;
import model.Image;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Builder per creare oggetti di test con dati predefiniti
 */
public class TestDataBuilder {

    // Contatori per generare dati unici
    private static int userCounter = 0;
    private static int postCounter = 0;

    /**
     * Crea un utente di test con dati validi
     */
    public static User createTestUser(String username, Role role) {
        return new User(
                username,
                "Nome" + userCounter,
                "Cognome" + userCounter++,
                LocalDate.of(1990, 1, 1),
                "333" + String.format("%07d", userCounter),
                username + "@test.com",
                "password123",
                role
        );
    }

    /**
     * Crea un utente con tutti i parametri personalizzabili
     */
    public static User createCustomUser(String username, String name, String surname,
                                        LocalDate dateOfBirth, String cellphone,
                                        String email, String password, Role role) {
        return new User(username, name, surname, dateOfBirth, cellphone, email, password, role);
    }

    /**
     * Crea un post di test con immagine fittizia
     */
    public static Post createTestPost(String authorUsername, String description) {
        byte[] imageData = createTestImageData();
        return new Post(
                authorUsername,
                imageData,
                imageData.length,
                "png",
                description != null ? description : "Test post " + postCounter++
        );
    }

    /**
     * Crea un commento di test
     */
    public static Comment createTestComment(int postId, String username, String text) {
        return new Comment(
                postId,
                username,
                text != null ? text : "Test comment"
        );
    }

    /**
     * Crea dati binari di un'immagine di test
     */
    public static byte[] createTestImageData() {
        try {
            // Crea un'immagine 100x100 con un colore solido
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(new Color(100, 150, 200));
            g.fillRect(0, 0, 100, 100);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            // Se fallisce, ritorna un array minimo
            return new byte[]{1, 2, 3, 4, 5};
        }
    }

    /**
     * Crea un oggetto Image di test
     */
    public static Image createTestImage(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return new Image(bufferedImage);
    }

    /**
     * Resetta i contatori (utile tra test suite diverse)
     */
    public static void resetCounters() {
        userCounter = 0;
        postCounter = 0;
    }
}