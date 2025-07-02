package model.mapper;

import model.User;
import model.Role;
import java.sql.*;
import java.time.LocalDate;

/**
 * Mapper per la conversione tra User e ResultSet/PreparedStatement.
 * Questa classe gestisce la mappatura dei dati tra l'oggetto User
 * e la sua rappresentazione nel database.
 */
public class UserMapper {

    /**
     * Converte un ResultSet in un oggetto User.
     * Assume che il ResultSet sia già posizionato sulla riga corretta.
     *
     * @param rs il ResultSet contenente i dati dell'utente
     * @return un oggetto User popolato con i dati del ResultSet
     * @throws SQLException se si verifica un errore nell'accesso ai dati
     */
    public User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUsername(rs.getString("username"));
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));

        // Gestione della data di nascita (può essere null)
        Date sqlDateOfBirth = rs.getDate("dateofbirth");
        if (sqlDateOfBirth != null) {
            user.setDateOfBirth(sqlDateOfBirth.toLocalDate());
        }

        user.setCellphone(rs.getString("cellphone"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));

        // Conversione del ruolo dal valore del database
        String roleValue = rs.getString("role");
        user.setRole(Role.fromDbValue(roleValue));

        return user;
    }

    /**
     * Popola un PreparedStatement con i dati di un User per l'inserimento.
     * L'ordine dei parametri corrisponde alla query di INSERT in UserDAOImpl.
     *
     * @param ps il PreparedStatement da popolare
     * @param user l'oggetto User contenente i dati
     * @throws SQLException se si verifica un errore nel setting dei parametri
     */
    public void setInsertParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getName());
        ps.setString(3, user.getSurname());
        ps.setDate(4, Date.valueOf(user.getDateOfBirth()));
        ps.setString(5, user.getCellphone());
        ps.setString(6, user.getEmail());
        ps.setString(7, user.getPassword());
        ps.setString(8, user.getRole().getDbValue());
    }

    /**
     * Popola un PreparedStatement con i dati di un User per l'aggiornamento.
     * Può essere utilizzato per query UPDATE dove i campi sono nello stesso ordine.
     *
     * @param ps il PreparedStatement da popolare
     * @param user l'oggetto User contenente i dati aggiornati
     * @throws SQLException se si verifica un errore nel setting dei parametri
     */
    public void setUpdateParameters(PreparedStatement ps, User user) throws SQLException {
        // Per ora usa la stessa logica dell'insert
        // In futuro potrebbe differire se l'UPDATE ha campi diversi
        setInsertParameters(ps, user);
    }
}