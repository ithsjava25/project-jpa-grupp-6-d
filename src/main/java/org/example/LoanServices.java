package org.example;

import jakarta.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;

public class LoanServices {

    private final EntityManager em;

    public LoanServices(EntityManager em) {
        this.em = em;
    }

    // Kolla om en bok är utlånad
    public boolean isBookLoaned(Long bookId) {

        List<Loan> loans = em.createQuery(
            "SELECT l FROM Loan l WHERE l.book.bookId = :bookId AND l.returnDate IS NOT NULL",
            Loan.class
        )
        .setParameter("bookId", bookId)
            .getResultList();

        if (loans.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    // Låna en bok
    public boolean loanBook(User user, Book book) {

        if (isBookLoaned(book.getId())) {
            return false;
        }

        em.getTransaction().begin();

        Book managedBook = em.merge(book);

        Loan loan = new Loan();

        loan.setUser(user);
        loan.setLoanDate(ZonedDateTime.now());
        loan.setReturnDate(ZonedDateTime.now().plusDays((7)));

        loan.setBook(managedBook);
        managedBook.setLoan(loan);
        em.persist(loan);
        em.getTransaction().commit();

        return true;
    }

    // Lämna tillbaka en bok
    public boolean returnBook(User user, Book book) {

        Loan loan = em.createQuery(
            "SELECT l FROM Loan l WHERE l.user = :user AND l.book = :book AND l.returnDate IS NULL",
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

        loan.setReturnDate(ZonedDateTime.now());
        Book managedBook = loan.getBook();
        managedBook.setLoan(null);
        em.getTransaction().commit();
        return true;
    }

    public List<Loan> activeLoans(User user) {

        return em.createQuery(
            "SELECT l FROM Loan l WHERE l.user = :user",
            Loan.class
        )
            .setParameter("user", user)
            .getResultList();
    }
}
