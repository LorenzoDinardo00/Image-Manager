package service.impl;

import service.PostService;
import dao.PostDao;
import dao.CommentDao;
import model.Post;
import model.Comment;
import model.User;
import model.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PostServiceImpl implements PostService {

    private final PostDao postDAO;
    private final CommentDao commentDAO;

    public PostServiceImpl(PostDao postDAO, CommentDao commentDAO) {
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
    }

    @Override
    public boolean createPost(Post post, User currentUser) throws SQLException, IllegalArgumentException {
        Objects.requireNonNull(post, "Il post non può essere nullo.");
        Objects.requireNonNull(currentUser, "L'utente corrente non può essere nullo.");
        Objects.requireNonNull(post.getAuthorUsername(), "L'autore del post non può essere nullo.");
        Objects.requireNonNull(post.getImageData(), "I dati dell'immagine del post non possono essere nulli.");
        if (post.getImageData().length == 0) {
            throw new IllegalArgumentException("I dati dell'immagine non possono essere vuoti.");
        }
        if (post.getDescription() == null || post.getDescription().trim().isEmpty()) {
            post.setDescription(""); // o lanciare eccezione se la descrizione è obbligatoria
        }
        if (!post.getAuthorUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("L'autore del post deve corrispondere all'utente corrente.");
        }
        // Solo gli AUTORI possono creare post
        if (currentUser.getRole() != Role.AUTORE) {
            throw new IllegalArgumentException("Solo gli utenti con ruolo AUTORE possono creare post.");
        }


        return postDAO.createPost(post);
    }

    @Override
    public Post getPostById(int postId) throws SQLException {
        if (postId <= 0) {
            return null;
        }
        return postDAO.getPostById(postId);
    }

    @Override
    public List<Post> getAllPosts() throws SQLException {
        return postDAO.getAllPosts();
    }

    @Override
    public boolean addLikeToPost(int postId, User currentUser) throws SQLException, IllegalArgumentException {
        // currentUser è opzionale qui, potrebbe essere usato per logica futura
        // (es. impedire like multipli dallo stesso utente, se non gestito a livello DB/DAO)
        if (postId <= 0) {
            throw new IllegalArgumentException("ID Post non valido.");
        }
        Post post = postDAO.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post non trovato con ID: " + postId);
        }
        return postDAO.addLike(postId);
    }

    @Override
    public boolean addCommentToPost(Comment comment, User currentUser) throws SQLException, IllegalArgumentException {
        Objects.requireNonNull(comment, "Il commento non può essere nullo.");
        Objects.requireNonNull(currentUser, "L'utente corrente non può essere nullo.");
        Objects.requireNonNull(comment.getCommenterUsername(), "L'autore del commento non può essere nullo.");
        if (!comment.getCommenterUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("L'autore del commento deve corrispondere all'utente corrente.");
        }
        if (comment.getPostId() <= 0) {
            throw new IllegalArgumentException("ID Post non valido nel commento.");
        }
        if (comment.getCommentText() == null || comment.getCommentText().trim().isEmpty()) {
            throw new IllegalArgumentException("Il testo del commento non può essere vuoto.");
        }

        Post post = postDAO.getPostById(comment.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("Post non trovato con ID: " + comment.getPostId() + " per cui aggiungere il commento.");
        }
        // Non ci sono restrizioni di ruolo per commentare nel codice originale

        return commentDAO.addComment(comment);
    }

    @Override
    public List<Comment> getCommentsForPost(int postId) throws SQLException, IllegalArgumentException {
        if (postId <= 0) {
            throw new IllegalArgumentException("ID Post non valido.");
        }
        Post post = postDAO.getPostById(postId); // Verifica se il post esiste
        if (post == null) {
            throw new IllegalArgumentException("Post non trovato con ID: " + postId);
        }
        return commentDAO.getCommentsForPost(postId);
    }
}