package org.example;

import jakarta.persistence.EntityManager;
import org.example.Entities.Book;
import org.example.Entities.Loan;
import org.example.Entities.User;

import java.time.ZonedDateTime;
import java.util.List;

public class LoanServices {



    // Kolla om en bok är utlånad
    public boolean isBookLoaned(Long bookId, EntityManager em) {
        List<Loan> loans = em.createQuery(
            "SELECT l FROM Loan l WHERE l.book.bookId = :bookId AND l.returnDate IS NOT NULL",
            Loan.class
        )
        .setParameter("bookId", bookId)
            .getResultList();

        return !loans.isEmpty();
    }

    // Låna en bok
    public boolean loanBook(User user, Book book, EntityManager em) {

        if (isBookLoaned(book.getId(), em)) {
            return false;
        }

        try {
            em.getTransaction().begin();
            // Makes sure JPA knows about the user
            User managedUser = em.merge(user);
            Book managedBook = em.merge(book);

            Loan loan = new Loan();
            loan.setUser(managedUser);
            loan.setLoanDate(ZonedDateTime.now());
            loan.setReturnDate(ZonedDateTime.now().plusDays(7));
            loan.setBook(managedBook);

            managedBook.setLoan(loan);

            em.persist(loan);
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            // If transactions fail but are still active, rollback
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        }
    }

    // Lämna tillbaka en bok
    public boolean returnBook(Book book, EntityManager em) {
        try {
            // Find loan
            Book managedBook = em.find(Book.class, book.getId());
            if (managedBook == null || managedBook.getLoan() == null) {
                return false;
            }

            Loan loan = managedBook.getLoan();

            em.getTransaction().begin();

            // Break the connection between the book and loan
            managedBook.setLoan(null);
            loan.setBook(null);
            loan.setUser(null);

            // Remove the loan
            Loan managedLoan = em.merge(loan);
            em.remove(managedLoan);

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            // If transactions fail but are still active, rollback
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        }
    }

    public List<Loan> activeLoans(User user, EntityManager em) {

        return em.createQuery("""
            SELECT l FROM Loan l
            LEFT JOIN FETCH l.book b
            LEFT JOIN FETCH l.user u
            WHERE l.user = :user
            """,
            Loan.class

        )
            .setParameter("user", user)
            .getResultList();
    }
}
