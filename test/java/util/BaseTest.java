package util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

/**
 * Classe base per tutti i test che necessitano del database
 */
public abstract class BaseTest {

    @BeforeAll
    public static void initializeTestEnvironment() throws SQLException {
        // Inizializza il database una sola volta per tutti i test
        TestDatabaseConfig.getTestConnection();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Pulisce il database prima di ogni test
        TestDatabaseConfig.clearDatabase();
        TestDataBuilder.resetCounters();
        additionalSetup();
    }

    @AfterEach
    public void tearDown() throws Exception {
        additionalTearDown();
        TestDatabaseConfig.clearDatabase();
    }

    @AfterAll
    public static void cleanupTestEnvironment() throws SQLException {
        TestDatabaseConfig.closeTestConnection();
    }

    /**
     * Override per setup aggiuntivi specifici del test
     */
    protected void additionalSetup() throws Exception {
        // Da implementare nelle sottoclassi se necessario
    }

    /**
     * Override per cleanup aggiuntivi specifici del test
     */
    protected void additionalTearDown() throws Exception {
        // Da implementare nelle sottoclassi se necessario
    }
}