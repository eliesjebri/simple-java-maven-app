package com.mycompany.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test simple pour vérifier que l'application s'exécute sans erreur.
 */
public class AppTest {

    @Test
    public void appRunsWithoutError() {
        // Vérifie que l'application démarre sans lever d'exception
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
