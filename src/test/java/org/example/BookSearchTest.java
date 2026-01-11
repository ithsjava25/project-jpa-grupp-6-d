package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookSearchTitleTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("library_system");
    }

    @AfterAll
    static void shutdown() {
        emf.close();
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.close();
    }
}
