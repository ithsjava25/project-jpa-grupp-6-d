package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMFactory implements AutoCloseable {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("library_system");

    public static void init(){
        // Call this once to initialize the factory att app start
    }

    // Use this in try-with
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    // Close the factory
    @Override
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
