package model.mapper;

import model.Comment;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Mapper per la conversione tra Comment e ResultSet/PreparedStatement.
 * Gestisce la mappatura dei dati tra l'oggetto Comment e la sua
 * rappresentazione nel database.
 */
public class CommentMapper {

    /**
     * Converte un ResultSet in un oggetto Comment.
     * Assume che il ResultSet sia già posizionato sulla riga corretta.
     *
     * @param rs il ResultSet contenente i dati del commento
     * @return un oggetto Comment popolato con i dati del ResultSet
     * @throws SQLException se si verifica un errore nell'accesso ai dati
     */
    public Comment fromResultSet(ResultSet rs) throws SQLException {
        Comment comment = new Comment();

        comment.setCommentId(rs.getInt("comment_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setCommenterUsername(rs.getString("commenter_username"));
        comment.setCommentText(rs.getString("comment_text"));

        // Gestione del timestamp del commento
        Timestamp commentedAtTimestamp = rs.getTimestamp("commented_at");
        if (commentedAtTimestamp != null) {
            comment.setCommentedAt(commentedAtTimestamp.toLocalDateTime());
        }

        return comment;
    }

    /**
     * Popola un PreparedStatement con i dati di un Comment per l'inserimento.
     * L'ordine corrisponde alla query INSERT in CommentDAOImpl:
     * (post_id, commenter_username, comment_text)
     *
     * @param ps il PreparedStatement da popolare
     * @param comment l'oggetto Comment contenente i dati
     * @throws SQLException se si verifica un errore nel setting dei parametri
     */
    public void setInsertParameters(PreparedStatement ps, Comment comment) throws SQLException {
        ps.setInt(1, comment.getPostId());
        ps.setString(2, comment.getCommenterUsername());
        ps.setString(3, comment.getCommentText());
    }

    /**
     * Popola un PreparedStatement per query di ricerca commenti per post.
     * Usato per query come: SELECT * FROM comments WHERE post_id = ?
     *
     * @param ps il PreparedStatement da popolare
     * @param postId l'ID del post di cui cercare i commenti
     * @throws SQLException se si verifica un errore nel setting dei parametri
     */
    public void setFindByPostIdParameters(PreparedStatement ps, int postId) throws SQLException {
        ps.setInt(1, postId);
    }

    /**
     * Crea un oggetto Comment vuoto con i valori di default appropriati.
     * Utile per inizializzazioni e test.
     *
     * @return un nuovo oggetto Comment con valori di default
     */
    public Comment createEmptyComment() {
        Comment comment = new Comment();
        comment.setCommentedAt(LocalDateTime.now());
        return comment;
    }

    /**
     * Verifica se un Comment ha tutti i campi obbligatori valorizzati.
     * Utile per validazioni prima dell'inserimento.
     *
     * @param comment il commento da validare
     * @return true se il commento è valido, false altrimenti
     */
    public boolean isValid(Comment comment) {
        return comment != null &&
                comment.getPostId() > 0 &&
                comment.getCommenterUsername() != null &&
                !comment.getCommenterUsername().trim().isEmpty() &&
                comment.getCommentText() != null &&
                !comment.getCommentText().trim().isEmpty();
    }
}