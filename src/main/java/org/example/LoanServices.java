package org.example;

import jakarta.persistence.EntityManager;
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
    public boolean returnBook(User user, Book book, EntityManager em) {

        try{
            Loan loan = em.createQuery(
                    "SELECT l FROM Loan l WHERE l.user = :user AND l.book = :book AND l.returnDate IS NOT NULL",
                    Loan.class
                )
                .setParameter("user", user)
                .setParameter("book", book)
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (loan == null) {
                return false;
            }

            em.getTransaction().begin();
            Book managedBook = loan.getBook();
            managedBook.setLoan(null);
            em.remove(loan);

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

        return em.createQuery(
            "SELECT l FROM Loan l WHERE l.user = :user",
            Loan.class
        )
            .setParameter("user", user)
            .getResultList();
    }
}
