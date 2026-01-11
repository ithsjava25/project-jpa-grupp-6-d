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

// test - search by title funkar utan error
    @Test
    void searchByTitle_executes_without_error() {
        BookSearch search = new BookSearch();

        var resultTitle = search.searchByTitle(em, "a");

        assertNotNull(resultTitle);
    }
// test- search by author funkar utan att få error
    @Test
    void searchByAuthor_executes_without_error() {
        BookSearch search = new BookSearch();

        var resultAuthor = search.searchByAuthor(em, "b");

        assertNotNull(resultAuthor);
    }

    // test - search by genre om funkar utan error
    @Test
    void searchByGenre_executes_without_error(){
        BookSearch search = new BookSearch();
        var resultGenre = search.searchByGenre(em,"c");
        assertNotNull(resultGenre);
    }
}
