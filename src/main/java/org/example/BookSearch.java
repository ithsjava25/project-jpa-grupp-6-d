package org.example;

import jakarta.persistence.EntityManager;
import java.util.List;

public class BookSearch {

    // Serch on book-title
    public List<Book> searchByTitle(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();

        return em.createQuery("""
                SELECT b
                FROM Book b
                WHERE LOWER(b.title) LIKE LOWER(:q)
                ORDER BY b.title
                """, Book.class)
            .setParameter("q", "%" + query.trim() + "%")
            .getResultList();
    }

    static void main() {

    }
}


