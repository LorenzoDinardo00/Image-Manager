package model;

public enum Role {
    OSSERVATORE("osservatore"),
    AUTORE("autore");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Role fromDbValue(String dbValue) {
        for (Role r : values()) {
            if (r.dbValue.equalsIgnoreCase(dbValue)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + dbValue);
    }

    public static Role fromInputString(String input) {
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(input) || r.getDbValue().equalsIgnoreCase(input)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Ruolo non valido: " + input + ". Usare 'osservatore' o 'autore'.");
    }
}