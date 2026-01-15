package org.example;

import jakarta.persistence.EntityManager;
import org.example.user.SessionManager;
import org.example.user.UserCLI;
import org.example.user.UserService;
import org.example.user.repository.JpaUserRepository;
import org.example.user.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class App {
    static void main(String[] args) {
        initialize(); // "Warm up" the factory

        final UserRepository userRepository = new JpaUserRepository();
        final UserService userService = new UserService(userRepository);
        Scanner sc = new Scanner(System.in);
        final UserCLI userCli = new UserCLI(userService, sc);
        SearchCli searchCli = new SearchCli(sc);

         /*################################# \
        |  ---- Application starts here ----  |
         \ #################################*/
        boolean appRunning = true;
        while (appRunning) {
            // Print main menu and get user input
            printMainMenu();
            System.out.print("Menyval: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> searchCli.bookSearchCli();
                case "2" -> {
                    if (SessionManager.isLoggedIn()) {
                        // Print loans
                        loanMenu(sc);
                    }
                    else {
                        // Login / Create user
                        userMenu(userCli);
                    }
                }
                case "3" -> {
                    if (SessionManager.isLoggedIn()) {
                        // Manage user
                        userMenu(userCli);
                    }
                    else {
                        System.out.println("Ogiltigt val.");
                    }
                }
                case "9" -> {
                    if (SessionManager.isLoggedIn()) {
                        logOut(userCli);
                    }
                    else System.out.println("Ogiltigt val.");
                }
                case "0" -> appRunning = false;
                default -> System.out.println("Ogiltigt val.");
            }
        }
        terminate();
    }

    private static void printMainMenu() {
        System.out.println("\n\nCLIBRARY");
        System.out.println("Startsida           Inloggad som: " + SessionManager.loggedInDisplayName());
        System.out.println("========================================================");

        if (SessionManager.isLoggedIn()) {
            System.out.println("1. Sök bok  |  2. Mina lån  |  3. Hantera användare  |  9. Logga ut  |  0. Avsluta");
        } else {
            System.out.println("1. Sök bok  |  2. Logga in/Skapa konto  |  0. Avsluta");
        }
    }

    private static void userMenu(UserCLI userCli) {
        if (SessionManager.isLoggedIn()) {
            userCli.manageUserMenu();
        }
        else {
            userCli.userMenu();
        }
    }

    private static void logOut(UserCLI userCli) {
        if(SessionManager.isLoggedIn()) {
            userCli.logout();
        }
        else {
            System.out.println("Inte inloggad.");
        }
    }

    private static void loanMenu(Scanner sc) {
        if (!SessionManager.isLoggedIn()) {
            System.out.println("Logga in för att se dina lån.");
            return;
        }

        LoanServices loanServices = new LoanServices();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (EntityManager em = EMFactory.getEntityManager()) {
            List<Loan> loans = loanServices.activeLoans(SessionManager.getCurrentUser(), em);

            if (loans.isEmpty()) {
                System.out.println("Inga aktiva lån.");
                return;
            }

            System.out.println("Aktiva lån:");
            for (int i = 0; i < loans.size(); i++) {
                Loan loan = loans.get(i);
                System.out.println((i + 1) + ". " + loan.getBook().getTitle() + " - Återlämnas: " + loan.getReturnDate().format(formatter));
            }

            System.out.print("Välj nummer att lämna tillbaka (0 = tillbaka): ");
            try {
                int input = Integer.parseInt(sc.nextLine());

                // Go back
                if (input == 0) {
                    return;
                }
                else if (input > 0 && input <= loans.size()) {
                    boolean success = loanServices.returnBook(
                        loans.get(input - 1).getBook(),
                        em
                    );
                    if (success) {
                        System.out.println("Boken har lämnats tillbaka!");
                    }
                    else {
                        System.out.println("Kunde inte lämna tillbaka boken.");
                    }
                }
                else {
                    System.out.println("Ogiltigt val.");
                }
            }
            // Catch invalid input
            catch (NumberFormatException e) {
                System.out.println("Ogiltigt val, skriv en siffra.");
            }

        }
    }

    private static void terminate() {
        System.out.println("Bibliotekssystemet avslutat. Välkommen åter!");
        EMFactory.close();
    }

    public static void initialize() {
        // Turn off Hibernate logging
        System.setProperty("org.jboss.logging.provider", "jdk");
        java.util.logging.Logger hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate");
        hibernateLogger.setLevel(java.util.logging.Level.SEVERE);

        // Initialize EntityManagerFactory
        EMFactory.init();
    }
}


