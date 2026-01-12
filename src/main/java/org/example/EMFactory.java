package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class EMFactory {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("library_system");

    private EMFactory() {
        // Prevent instantiation
    }

    public static void init(){
        // Call this once to initialize the factory at app start
    }

    // Use this in try-with
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    // Close the factory
    public static void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
