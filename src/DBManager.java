import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static DBManager instance;
    private Connection connection;

    // Costruttore privato: nessuno può creare un DBManager se non dall'interno
    private DBManager() {
        try {
            // Esempio con SQLite: "jdbc:sqlite:percorso_al_tuo_db"
            String url = "jdbc:sqlite:database/users.db";
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo pubblico per ottenere l'istanza
    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    // Restituisce la Connection
    public Connection getConnection() {
        return connection;
    }
}