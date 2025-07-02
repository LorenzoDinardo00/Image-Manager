package model.mapper;

import model.Post;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Mapper per la conversione tra Post e ResultSet/PreparedStatement.
 * Gestisce la mappatura dei dati tra l'oggetto Post e la sua
 * rappresentazione nel database.
 */
public class PostMapper {

    /**
     * Converte un ResultSet in un oggetto Post.
     * Assume che il ResultSet sia già posizionato sulla riga corretta.
     *
     * @param rs il ResultSet contenente i dati del post
     * @return un oggetto Post popolato con i dati del ResultSet
     * @throws SQLException se si verifica un errore nell'accesso ai dati
     */
    public Post fromResultSet(ResultSet rs) throws SQLException {
        Post post = new Post();

        post.setPostId(rs.getInt("post_id"));
        post.setAuthorUsername(rs.getString("author_username"));
        post.setImageData(rs.getBytes("image_data"));
        post.setImageSize(rs.getLong("image_size"));
        post.setImageFormat(rs.getString("image_format"));
        post.setDescription(rs.getString("description"));
        post.setLikesCount(rs.getInt("likes_count"));

        // Gestione del timestamp di creazione
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            post.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        return post;
    }

    /**
     * Popola un PreparedStatement con i dati di un Post per l'inserimento.
     * L'ordine corrisponde alla query INSERT in PostDAOImpl:
     * (author_username, image_data, image_size, image_format, description, likes_count)
     *
     * @param ps il PreparedStatement da popolare
     * @param post l'oggetto Post contenente i dati
     * @throws SQLException se si verifica un errore nel setting dei parametri
     */
    public void setInsertParameters(PreparedStatement ps, Post post) throws SQLException {
        ps.setString(1, post.getAuthorUsername());
        ps.setBytes(2, post.getImageData());
        ps.setLong(3, post.getImageSize());
        ps.setString(4, post.getImageFormat());
        ps.setString(5, post.getDescription());
        ps.setInt(6, 0); // likes_count iniziale sempre 0
    }

    /**
     * Popola un PreparedStatement per l'aggiornamento del conteggio dei like.
     * Usato per query come: UPDATE posts SET likes_count = likes_count + 1 WHERE post_id = ?
     *
     * @param ps il PreparedStatement da popolare
     * @param postId l'ID del post da aggiornare
     * @throws SQLException se si verifica un errore nel setting dei parametri
     */
    public void setLikeUpdateParameters(PreparedStatement ps, int postId) throws SQLException {
        ps.setInt(1, postId);
    }

    /**
     * Crea un oggetto Post vuoto con i valori di default appropriati.
     * Utile per inizializzazioni e test.
     *
     * @return un nuovo oggetto Post con valori di default
     */
    public Post createEmptyPost() {
        Post post = new Post();
        post.setLikesCount(0);
        post.setCreatedAt(LocalDateTime.now());
        return post;
    }
}