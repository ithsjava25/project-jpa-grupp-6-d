package org.example;

import org.example.EMFactory;
import org.example.user.SessionManager;
import org.example.user.UserCLI;
import org.example.user.UserService;
import org.example.user.repository.JpaUserRepository;
import org.example.user.repository.UserRepository;

import java.util.Scanner;

public class App {
    static void main(String[] args) {
        initialize(); // "Warm up" the factory

        final UserRepository userRepository = new JpaUserRepository();
        final UserService userService = new UserService(userRepository);
        Scanner scanner = new Scanner(System.in);
        final UserCLI userCli = new UserCLI(userService, scanner);
        SearchCli searchCli = new SearchCli(scanner, EMFactory.getEntityManager());

        // #######################
        // Application starts here
        // #######################
        boolean appRunning = true;
        while (appRunning) {
            System.out.println("\n\nBIBLIOTEKSSYSTEMET");
            System.out.println("Startsida           Inloggad som: " + SessionManager.loggedInDisplayName());
            System.out.println("========================================================");

            if (SessionManager.isLoggedIn()) {
                System.out.println("1. Sök bok  |  5. Mina lån |  3. Hantera användare  |  4. Logga ut |  0. Avsluta");
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
                case "5" -> {
                    System.out.println("Dina aktiva lån:");
                    System.out.println(loanServices.activeLoans(SessionManager.getCurrentUser()));
                    // kunna välja att lämna tillbaka en bok
                    //                     System.out.println("Du har lämnat tillbaka din bok!");
                }
                case "2" -> {
                    if (SessionManager.isLoggedIn()) userCli.manageUserMenu();
                    else userCli.userMenu();
                }
                case "4" -> {
                    if (SessionManager.isLoggedIn()) userCli.logout();
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


