public class LoginManager {
    private UserRepository userRepository;

    public LoginManager() {
        this.userRepository = new UserRepository();
    }

    // Oppure passare un UserRepository da fuori
    public LoginManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String passwordInChiaro) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            return false; // Utente inesistente
        }

        // Recupera la password hashed dal DB
        String hashedPasswordDB = user.getHashedPassword();

        // Hasha la password inserita dall'utente e confronta
        // In produzione, usa Bcrypt/Argon2/PBKDF2, non un semplice MD5/SHA1
        String hashedInput = hashPassword(passwordInChiaro);

        return hashedPasswordDB.equals(hashedInput);
    }

    private String hashPassword(String password) {
        // Qui dovresti usare una libreria sicura, per esempio:
        // Bcrypt: https://github.com/patrickfav/bcrypt
        // PBKDF2, Argon2, ecc.
        // Per semplicità, ipotizziamo una funzione "fittizia":

        return "HASHED_" + password;
    }
}