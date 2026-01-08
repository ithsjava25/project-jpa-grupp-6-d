package org.example;

//todo:
//1. läsa TSV-fil: seed-books.tsv
//2. skapa Author, Book, Genre-objekt
//3. koppla dem
//4. köra persist() + commit() → då hamnar datan i våran DB.f

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SeedData {
    public static void main(String[] args) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library_system");
        EntityManager em = emf.createEntityManager();

        System.out.println("Läser TSV...");

        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                SeedData.class.getResourceAsStream("/seed-books.tsv"),
                StandardCharsets.UTF_8
            )
        )) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = line.split("\t");

                String title = c[0].trim();
                String description = c[1].trim();
                String isbn = c[2].trim();
                String year = c[3].trim();
                String firstName = c[4].trim();
                String lastName = c[5].trim();
                String genre = c[6].trim();

                System.out.println(title + " | " + description + " | " + year + " | " + firstName + " " + lastName + " | " + genre + " | " + isbn);

                count++;
            }

            System.out.println("Rader: " + count);
        }

        em.close();
        emf.close();
    }
}

