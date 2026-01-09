package org.example;

import jakarta.persistence.EntityManager;
import java.util.List;

public class BookSearch {

    // Serch on book-title
    public List<Book> searchByTitle(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();

        return em.createQuery("""
            SELECT DISTINCT b
            FROM Book b
            LEFT JOIN FETCH b.authors a
            WHERE LOWER(b.title) LIKE LOWER(:q)
            ORDER BY b.title
            """, Book.class)
            .setParameter("q", "%" + query.trim() + "%")
            .getResultList();
    }

    //todo: Serch by author
    public List<Book> searchByAuthor(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();

        return em.createQuery("""
            SELECT DISTINCT b
            FROM Book b
            JOIN FETCH b.authors a
            WHERE LOWER(a.firstName) LIKE LOWER(:q)
               OR LOWER(a.lastName)  LIKE LOWER(:q)
            ORDER BY b.title
            """, Book.class)
            .setParameter("q", "%" + query.trim() + "%")
            .getResultList();
    }


    //todo: Serch by genre
    public List<Book> searchByGenre(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();

        return em.createQuery("""
            SELECT DISTINCT b
            FROM Book b
            JOIN b.genres g
            LEFT JOIN FETCH b.authors a
            WHERE LOWER(g.name) LIKE LOWER(:q)
            ORDER BY b.title
            """, Book.class)
            .setParameter("q", "%" + query.trim() + "%")
            .getResultList();
    }

}


