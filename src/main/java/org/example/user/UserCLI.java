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
        while (!closeMenu && !SessionManager.isLoggedIn()) {
            System.out.println(appName());
            System.out.println("Konto\n=========================================================");
            System.out.println("1. Logga in  |  2. Skapa konto  |  3. Tillbaka");
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
        while (!closeMenu && SessionManager.isLoggedIn()) {
            System.out.println(appName());
            System.out.println("Hantera användare\n=========================================================");
            System.out.println("1. Uppdatera användare  |  2. Radera användare  |  3. Logga ut  |  4. Tillbaka");
            System.out.print("Menyval: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> updateUserMenu();
                case "2" -> deleteUser();
                case "3" -> logout();
                case "4" -> closeMenu = true;
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    public void loginMenu() {
        // Make sure user is not already logged in
        if (SessionManager.isLoggedIn()) return;

        while (!SessionManager.isLoggedIn()) {
            try {
                System.out.println(appName());
                System.out.println("Logga in\n=========================================================");
                System.out.println("(Skriv 'avbryt' för att avbryta inloggningen)");
                // Ask for credentials, prompt to enable exiting from menu
                String username = prompt("Användarnamn: ");
                String password = prompt("Lösenord: ");

                // Check credentials with database
                User user = userService.login(username, password);

                // Tell SessionManager to remember user
                SessionManager.login(user);

                if (SessionManager.isLoggedIn()){
                    System.out.println("Inloggningen lyckades. Välkommen " + user.getFirstName() + "!");
                }
                else {
                    System.out.println("Inloggningen misslyckades.");
                }
            }
            // Catch validation errors
            catch (IllegalArgumentException e) {
                System.out.println("Felmeddelande: " + e.getMessage());
            }
            // Catch our "exit" exception"
            catch (RuntimeException e){
                if (e.getMessage().equals("AVBRUTET")) {
                    return;
                }
                // Catch any other exceptions
                throw e;
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
                System.out.println(appName());
                System.out.println("Skapa användare\n=========================================================");
                System.out.println("(Skriv 'avbryt' för att avbryta skapandet av ny användare)");

                String firstName = prompt("Förnamn: ");
                String lastName = prompt("Efternamn: ");
                String email = prompt("Email: ");
                String password = prompt("Lösenord: ");

                // Create user
                User user = userService.createUser(firstName, lastName, email, password);

                System.out.println("Välkommen " + user.getFirstName() + "!");
                System.out.println("Ditt användarnamn är: " + user.getUsername());
                success = true;
            }
            // Catch validation errors
            catch (IllegalArgumentException e) {
                System.out.println("Felmeddelande: " + e.getMessage());
            }
            // Catch our "exit" exception"
            catch (RuntimeException e){
                if (e.getMessage().equals("AVBRUTET")) {
                    return;
                }
                // Catch any other exceptions
                throw e;
            }
        }
    }

    public void updateUserMenu() {
        // Make sure user is logged in
        if (!SessionManager.isLoggedIn()) return;

        User currentUser = SessionManager.getCurrentUser();

        try {
            System.out.println(appName());
            System.out.println("UPPDATERA ANVÄNDARE\n=========================================================");
            System.out.println("(Skriv 'avbryt' för att avbryta uppdateringen av användaren)");

            String firstName = prompt("Nytt förnamn: ");
            String lastName = prompt("Nytt efternamn: ");
            String email = prompt("Ny email: ");
            String password = prompt("Nytt lösenord: ");

            User updatedUser = userService.updateUser(currentUser.getUserId(), firstName, lastName, email, password);
            SessionManager.login(updatedUser); // Update session with new user
            System.out.println("Användaren har uppdaterats!");
        }
        // Catch validation errors
        catch (IllegalArgumentException e) {
            System.out.println("Kunde inte uppdatera: " + e.getMessage());
        }
        // Catch our "exit" exception"
        catch (RuntimeException e){
            if (e.getMessage().equals("AVBRUTET")) {
                return;
            }
            // Catch any other exceptions
            throw e;
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
        } else {
            System.out.println("Du har valt att behålla kontot.");
        }
    }

    // Method to enable user to exit menu
    private String prompt(String message) {
        // Print the passed argument
        System.out.print(message);
        String input = scanner.nextLine();
        // Exit from current menu if "avbryt" is entered
        if (input.equalsIgnoreCase("avbryt")) {
            throw new RuntimeException("AVBRUTET");
        }
        return input;
    }

    private String appName() {
        return "\n\nBIBLIOTEKSSYSTEMET";
    }

}
