package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMFactory {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("library_system");

    // Use this in try-with
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Close the factory
    public static void close() {
        emf.close();
    }
}
