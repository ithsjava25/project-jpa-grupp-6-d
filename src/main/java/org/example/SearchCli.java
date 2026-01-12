package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.user.SessionManager;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SearchCli {
        private final BookSearch searchService = new BookSearch();
        private final Scanner sc;

        // Constructor?
        public SearchCli(Scanner sc) {
            this.sc = sc;
        }

        public void bookSearchCli() {
            // Try-with to force em to auto-close
            try (EntityManager em = EMFactory.getEntityManager()) {
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
                            handleSearchFlow(results, em);
                        }
                        case "2" -> {
                            System.out.print("Skriv författare (för- eller efternamn): ");
                            String q = sc.nextLine();
                            List<Book> results = searchService.searchByAuthor(em, q);
                            handleSearchFlow(results, em);
                        }
                        case "3" -> {
                            System.out.print("Skriv genre: ");
                            String q = sc.nextLine();
                            List<Book> results = searchService.searchByGenre(em, q);
                            handleSearchFlow(results, em);
                        }
                        case "0" -> running = false;
                        default -> System.out.println("Ogiltigt val, försök igen.");
                    }
                }
            }
        }

    // Flöde: lista -> välj -> detalj

    private void handleSearchFlow(List<Book> results, EntityManager em) {
        printSearchResults(results);
        chooseAndShowBookDetails(results, em);
    }

    private void printSearchResults(List<Book> results) {
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

    private void chooseAndShowBookDetails(List<Book> results, EntityManager em) {
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
            printBookDetails(selected, em);

            System.out.print("Tryck Enter för att gå tillbaka till sök");
            sc.nextLine();
            return;
        }
    }

    private void printBookDetails(Book b, EntityManager em) {
        LoanServices loanServices = new LoanServices(em);
        System.out.println("\n==============================");
        System.out.println(" " + b.getTitle());
        System.out.println("------------------------------");
        System.out.println("Författare: " + formatAuthors(b));
        System.out.println("Genre: " + formatGenres(b));
        System.out.println("Utgiven: " + b.getPublishYear());

        String desc = b.getDescription();
        if (desc == null || desc.isBlank()) desc = "(Ingen beskrivning)";
        System.out.println("\nBeskrivning:\n" + desc);

        if (loanServices.isBookLoaned(b.getId())){
            System.out.println("Status: Utlånad");
        } else {
            System.out.println("Status: Tillgänglig");

            boolean isLoggedIn = SessionManager.isLoggedIn();
            boolean isRunning = true;

            if (isLoggedIn){
                while (isRunning) {
                    System.out.println("1. Låna bok | 2. Tillbaka");
                    String choice = sc.nextLine();

                    switch (choice) {
                        case "1" -> {
                            loanServices.loanBook(SessionManager.getCurrentUser(), b);
                            isRunning = false;
                            System.out.println("Du har nu lånat boken!");
                        }

                        case "2" -> {
                            isRunning = false;
                        }
                        default -> System.out.println("Ogiltigt val, försök igen.");
                    }
                }
            }
        }
        System.out.println("==============================");
    }

    // ===== Format helpers =====

    private String formatListRow(Book b) {
        return b.getTitle() + " — " + formatAuthors(b);
    }

    private String formatAuthors(Book b) {
        if (b.getAuthors() == null || b.getAuthors().isEmpty()) return "Okänd författare";
        return b.getAuthors().stream()
            .map(a -> (a.getFirstName() + " " + a.getLastName()).trim())
            .collect(Collectors.joining(", "));
    }

    private String formatGenres(Book b) {
        if (b.getGenres() == null || b.getGenres().isEmpty()) return "Okänd genre";
        return b.getGenres().stream()
            .map(g -> g.getGenre().trim())   // <-- byt till getGenreName() om det är din getter
            .collect(Collectors.joining(", "));
    }
}
