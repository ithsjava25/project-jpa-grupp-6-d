package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SearchCli {
        static EntityManager em;
        static BookSearch searchService = new BookSearch();
        static Scanner sc;

        // Constructor?
        public SearchCli(Scanner sc, EntityManager em) {
            this.sc = sc;
            this.em = em;
        }

        public static void bookSearchCli() {
            boolean running = true;

            while (running) {
                System.out.println("\n Bibliotekssystem – Sök");
                System.out.println("1) Sök på titel");
                System.out.println("2) Sök på författare");
                System.out.println("3) Sök på genre");
                System.out.println("0) Tillbaka");
                System.out.print("Välj: ");

                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1" -> {
                        System.out.print("Skriv titel (eller del av titel): ");
                        String q = sc.nextLine();
                        List<Book> results = searchService.searchByTitle(em, q);
                        handleSearchFlow(sc, results);
                    }
                    case "2" -> {
                        System.out.print("Skriv författare (för- eller efternamn): ");
                        String q = sc.nextLine();
                        List<Book> results = searchService.searchByAuthor(em, q);
                        handleSearchFlow(sc, results);
                    }
                    case "3" -> {
                        System.out.print("Skriv genre: ");
                        String q = sc.nextLine();
                        List<Book> results = searchService.searchByGenre(em, q);
                        handleSearchFlow(sc, results);
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Ogiltigt val, försök igen.");
                }
            }
        }



    // Flöde: lista -> välj -> detalj

    private static void handleSearchFlow(Scanner sc, List<Book> results) {
        printSearchResults(results);
        chooseAndShowBookDetails(sc, results);
    }

    private static void printSearchResults(List<Book> results) {
        if (results.isEmpty()) {
            System.out.println("Inga träffar.");
            return;
        }

        System.out.println("\nTräffar:");
        for (int i = 0; i < results.size(); i++) {
            Book b = results.get(i);
            System.out.println((i + 1) + ") " + formatListRow(b));
        }
    }

    private static void chooseAndShowBookDetails(Scanner sc, List<Book> results) {
        if (results.isEmpty()) return;

        while (true) {
            System.out.print("\nVälj boknummer (0 = tillbaka): ");
            String input = sc.nextLine().trim();

            int n;
            try {
                n = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Skriv en siffra.");
                continue;
            }

            if (n == 0) return;

            if (n < 1 || n > results.size()) {
                System.out.println("Ogiltigt nummer.");
                continue;
            }

            Book selected = results.get(n - 1);
            printBookDetails(selected);

            System.out.print("Tryck Enter för att gå tillbaka till sök");
            sc.nextLine();
            return;
        }
    }

    private static void printBookDetails(Book b) {
        LoanServices loanServices = new LoanServices(em);
        System.out.println("\n==============================");
        System.out.println(" " + b.getTitle());
        System.out.println("------------------------------");
        System.out.println("Författare: " + formatAuthors(b));
        System.out.println("Genre: " + formatGenres(b));
        System.out.println("Utgiven: " + b.getPublishYear());
        System.out.println("Låna bok");

        String desc = b.getDescription();
        if (desc == null || desc.isBlank()) desc = "(Ingen beskrivning)";
        System.out.println("\nBeskrivning:\n" + desc);

        if (loanServices.isBookLoaned(b.getId())){
            System.out.println("Status: Utlånad");
        } else {
            System.out.println("Status: Tillgänglig");
        }

        //todo:
        // Status på boken: tillgänglig/utlånad
        // om tillgänglig och inloggad - låna bok
            // om ej inloggad - skicka till logga in sidan.

        System.out.println("==============================");
    }

    // ===== Format helpers =====

    private static String formatListRow(Book b) {
        return b.getTitle() + " — " + formatAuthors(b);
    }

    private static String formatAuthors(Book b) {
        if (b.getAuthors() == null || b.getAuthors().isEmpty()) return "Okänd författare";
        return b.getAuthors().stream()
            .map(a -> (a.getFirstName() + " " + a.getLastName()).trim())
            .collect(Collectors.joining(", "));
    }

    private static String formatGenres(Book b) {
        if (b.getGenres() == null || b.getGenres().isEmpty()) return "Okänd genre";
        return b.getGenres().stream()
            .map(g -> g.getGenre().trim())   // <-- byt till getGenreName() om det är din getter
            .collect(Collectors.joining(", "));
    }
}
