package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SeedData {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library_system");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        int saved = 0;

        try {
            System.out.println("Starta seeding");

            tx.begin();

            InputStream is = SeedData.class.getResourceAsStream("/seed-books.tsv");
            if (is == null) {
                throw new IllegalStateException("Hittar inte tsv-fil");
            }

            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
            )) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;

                    String[] c = line.split("\t");
                    if (c.length < 7) {
                        throw new IllegalArgumentException("För få kolumner i raden: " + line);
                    }

                    // TSV: title, description, isbn, publishYear, firstName, lastName, genre
                    String title = c[0].trim();
                    String description = c[1].trim();
                    String isbn = c[2].trim();
                    int publishYear = Integer.parseInt(c[3].trim());
                    String firstName = c[4].trim();
                    String lastName = c[5].trim();
                    String genreName = c[6].trim();

                    Author author = em.createQuery(
                            "SELECT a FROM Author a WHERE a.firstName = :fn AND a.lastName = :ln",
                            Author.class)
                        .setParameter("fn", firstName)
                        .setParameter("ln", lastName)
                        .getResultStream()
                        .findFirst()
                        .orElseGet(() -> {
                            Author a = new Author();
                            a.setFirstName(firstName);
                            a.setLastName(lastName);
                            em.persist(a);
                            return a;
                        });

                    Genre genre = em.createQuery(
                            "SELECT g FROM Genre g WHERE g.genreName = :name",
                            Genre.class)
                        .setParameter("name", genreName)
                        .getResultStream()
                        .findFirst()
                        .orElseGet(() -> {
                            Genre g = new Genre();
                            g.setGenre(genreName);
                            em.persist(g);
                            return g;
                        });

                    Book book = em.createQuery(
                            "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                        .setParameter("isbn", isbn)
                        .getResultStream()
                        .findFirst()
                        .orElseGet(() -> {
                            Book b = new Book();
                            b.setTitle(title);
                            b.setDescription(description);
                            b.setIsbn(isbn);
                            b.setPublishYear(publishYear);
                            em.persist(b);
                            return b;
                        });

                    // Koppla relationer
                    book.getAuthors().add(author);
                    book.getGenres().add(genre);
                    genre.getBooks().add(book);

                    saved++;
                    System.out.println("Sparade 1 bok: " + title);
                }
            }

            tx.commit();
            System.out.println("Seeding klart (Saved=" + saved + ")");

        } catch (Exception e) {
            System.err.println("Seeding misslyckades: " + e.getMessage());
            e.printStackTrace();

            if (tx.isActive()) {
                tx.rollback();
                System.err.println("Rollback");
            }

        } finally {
            if (em.isOpen()) em.close();
            emf.close();
        }
    }
}
