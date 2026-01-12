package org.example;

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
        Scanner scanner = new Scanner(System.in);
        final UserCLI userCli = new UserCLI(userService, scanner);
        SearchCli searchCli = new SearchCli(scanner);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

         /*################################# \
        |  ---- Application starts here ----  |
         \ #################################*/
        boolean appRunning = true;
        while (appRunning) {
            System.out.println("\n\nBIBLIOTEKSSYSTEMET");
            System.out.println("Startsida           Inloggad som: " + SessionManager.loggedInDisplayName());
            System.out.println("========================================================");

            if (SessionManager.isLoggedIn()) {
                System.out.println("1. Sök bok  |  2. Hantera användare  |  3. Logga ut  |  4. Mina lån  |  0. Avsluta");
            } else {
                System.out.println("1. Sök bok  |  2. Logga in/Skapa konto  |  0. Avsluta");
            }

            System.out.print("Menyval: ");
            String choice = scanner.nextLine();
            LoanServices loanServices = new LoanServices(EMFactory.getEntityManager());
            switch (choice) {
                case "1" -> {
                    searchCli.bookSearchCli();
                }
                case "2" -> {
                    if (SessionManager.isLoggedIn()) {
                        userCli.manageUserMenu();
                    } else {
                        userCli.userMenu();
                    }
                }
                case "3" -> {
                    if (SessionManager.isLoggedIn()) userCli.logout();
                }
                case "4" -> {
                    List<Loan> loans = loanServices.activeLoans(SessionManager.getCurrentUser());

                    boolean running = true;
                    while (running) {
                        if (!loans.isEmpty()) {
                            System.out.println("Dina aktiva lån:");
                            int counter = 0;
                            for (Loan loan : loans) {
                                counter += 1;
                                System.out.println(counter + ". Titel: " + loan.getBook().getTitle() + " - Åter: " + loan.getReturnDate()
                                    .format(formatter));
                            }
                            System.out.println("Välj nummer för den bok du vill lämna tillbaka (0 = tillbaka): ");
                            String inputString = scanner.nextLine();
                            Integer input = Integer.parseInt(inputString);
                            if (input >= 1 && input <= loans.size()) {
                                loanServices.returnBook(
                                    SessionManager.getCurrentUser(),
                                    loans.get(input - 1).getBook()
                                );
                                loans.remove(input - 1);
                                System.out.println("Du har lämnat tillbaka din bok!");
                            } else if (input == 0) {
                                running = false;
                            } else {
                                System.out.println("ogiltigt val.");
                            }

                        } else {
                            System.out.println("Inga aktiva lån.");
                            running = false;
                        }
                    }
                }
                case "0" -> appRunning = false;
                default -> System.out.println("Ogiltigt val.");
            }
        }
        terminate();
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


