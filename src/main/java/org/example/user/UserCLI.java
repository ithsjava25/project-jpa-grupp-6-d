package org.example.user;

import org.example.User;
import java.util.Scanner;

public class UserCLI {
    private final UserService userService;
    private final Scanner scanner;

    // Constructor
    public UserCLI(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    public void userMenu() {
        // Make sure user is not already logged in
        if (SessionManager.isLoggedIn()) return;

        boolean closeMenu = false;
        while (!closeMenu) {
            System.out.println("\n1. Logga in\n2. Skapa konto\n3. Tillbaka");
            System.out.print("Menyval: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> loginMenu();
                case "2" -> createUserMenu();
                case "3" -> closeMenu = true;
                default -> System.out.println("Ogiltigt val.");
            }

            // Check if user is logged in, if so, close menu
            if (SessionManager.isLoggedIn()) {
                closeMenu = true;
            }
        }
    }

    public void manageUserMenu() {
        // Make sure user is logged in
        if (!SessionManager.isLoggedIn()) return;

        boolean closeMenu = false;
        while (!closeMenu) {
            System.out.println("\n1. Uppdatera användare\n2. Radera användare\n3. Tillbaka");
            System.out.print("Menyval: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> updateUserMenu();
                case "2" -> deleteUser();
                case "3" -> closeMenu = true;
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    public void loginMenu() {
        // Make sure user is not already logged in
        if (SessionManager.isLoggedIn()) return;

        boolean success = false;

        while (!success) {
            try {
                System.out.println("************************\n******* LOGGA IN *******\n************************");
                // Ask for credentials
                System.out.print("Användarnamn: ");
                String username = scanner.nextLine();
                System.out.print("Lösenord: ");
                String password = scanner.nextLine();

                // Check credentials with database
                User user = userService.login(username, password);

                // Tell SessionManager to remember user
                SessionManager.login(user);

                if (SessionManager.isLoggedIn()){
                    System.out.println("Inloggningen lyckades! Välkommen " + user.getFirstName());
                    success = true;
                }
                else {
                    System.out.println("Inloggningen misslyckades.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Felmeddelande: " + e.getMessage());
            }
        }
    }

    public void logout() {
        SessionManager.logout();
        System.out.println("Du har loggats ut.");
    }

    public void createUserMenu() {
        // Make sure user is not already logged in
        if (SessionManager.isLoggedIn()) return;

        boolean success = false;

        while (!success) {
            try {
                System.out.println("*************************\n**** SKAPA ANVÄNDARE ****\n*************************");
                System.out.print("Förnamn: ");
                String firstName = scanner.nextLine();
                System.out.print("Efternamn: ");
                String lastName = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                String password = scanner.nextLine();

                // Create user
                User user = userService.createUser(firstName, lastName, email, password);

                System.out.println("Välkommen " + user.getFirstName() + "!");
                System.out.println("Ditt användarnamn är: " + user.getUsername());
                success = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Felmeddelande: " + e.getMessage());
            }
        }
    }

    public void updateUserMenu() {
        // Make sure user is logged in
        if (!SessionManager.isLoggedIn()) return;

        User currentUser = SessionManager.getCurrentUser();
        try {
            System.out.println("*************************\n**** UPPDATERA ANVÄNDARE ****\n*************************");
            System.out.print("Nytt förnamn [" + currentUser.getFirstName() + "]: ");
            String firstName = scanner.nextLine();
            System.out.print("Nytt efternamn [" + currentUser.getLastName() + "]: ");
            String lastName = scanner.nextLine();
            System.out.print("Ny email [" + currentUser.getEmail() + "]: ");
            String email = scanner.nextLine();
            System.out.print("Nytt lösenord: ");
            String password = scanner.nextLine();

            User updatedUser = userService.updateUser(currentUser.getUserId(), firstName, lastName, email, password);
            SessionManager.login(updatedUser); // Update session with new user
            System.out.println("Användaren har uppdaterats!");
        } catch (IllegalArgumentException e) {
            System.out.println("Kunde inte uppdatera: " + e.getMessage());
        }
    }

    public void deleteUser() {
        // Make sure user is logged in
        if (!SessionManager.isLoggedIn()) return;

        System.out.print("Är du säker på att du vill radera ditt konto? (ja/nej): ");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            userService.deleteUserById(SessionManager.getCurrentUser().getUserId());
            SessionManager.logout();
            System.out.println("Kontot har raderats och du har loggats ut.");
        }
    }
}
