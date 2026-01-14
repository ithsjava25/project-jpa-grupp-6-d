package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoanServicesTest {

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

    // test - isBookLoaned funkar utan error
    @Test
    void isBookLoaned_executes_without_error() {
        LoanServices service = new LoanServices();
        boolean result = service.isBookLoaned(1L, em);
        assertNotNull(result);
    }

    // test - loanBook funkar utan error
    @Test
    void loanBook_executes_without_error() {
        LoanServices service = new LoanServices();
        User user = em.find(User.class, 1L);
        Book book = em.find(Book.class, 1L);
        boolean result = service.loanBook(user, book, em);
        assertNotNull(result);
    }

    // test - returnBook funkar utan error
    @Test
    void returnBook_executes_without_error() {
        LoanServices service = new LoanServices();

        User user = em.find(User.class, 1L);
        Book book = em.find(Book.class, 1L);

        boolean result = service.returnBook(user, book, em);

        assertNotNull(result);
    }

    // test - activeLoans funkar utan error
    @Test
    void activeLoans_executes_without_error() {
        LoanServices service = new LoanServices();
        List<Loan> result = service.activeLoans(em.find(User.class, 1L), em);
        assertNotNull(result);
    }
}
