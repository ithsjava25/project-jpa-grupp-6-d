package org.example;

import jakarta.persistence.EntityManager;
import org.example.Entities.Book;

import java.util.List;

public class BookSearch {

    public List<Book> searchByTitle(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();
        String q = query.trim().toLowerCase();

        return em.createQuery("""
            SELECT DISTINCT b
            FROM Book b
            LEFT JOIN FETCH b.authors a
            LEFT JOIN FETCH b.genres g
            LEFT JOIN FETCH b.loan l
            WHERE LOWER(b.title) LIKE :q
            ORDER BY b.title
            """, Book.class)
            .setParameter("q", "%" + q + "%")
            .getResultList();
    }

    public List<Book> searchByAuthor(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();
        String q = query.trim().toLowerCase();

        return em.createQuery("""
            SELECT DISTINCT b
            FROM Book b
            JOIN FETCH b.authors a
            LEFT JOIN FETCH b.genres g
            LEFT JOIN FETCH b.loan l
            WHERE LOWER(a.firstName) LIKE :q
               OR LOWER(a.lastName)  LIKE :q
            ORDER BY b.title
            """, Book.class)
            .setParameter("q", "%" + q + "%")
            .getResultList();
    }

    public List<Book> searchByGenre(EntityManager em, String query) {
        if (query == null || query.isBlank()) return List.of();
        String q = query.trim().toLowerCase();

        return em.createQuery("""
            SELECT DISTINCT b
            FROM Book b
            JOIN FETCH b.genres g
            LEFT JOIN FETCH b.authors a
            LEFT JOIN FETCH b.loan l
            WHERE LOWER(g.genreName) LIKE :q
            ORDER BY b.title
            """, Book.class)
            .setParameter("q", "%" + q + "%")
            .getResultList();
    }
}
