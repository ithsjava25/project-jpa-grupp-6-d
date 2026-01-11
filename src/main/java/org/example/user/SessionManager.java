package org.example.user;

import org.example.User;

public class SessionManager {
    private static User currentUser = null;

    // Log in user by setting currentUser to the passed in user
    public static void login(User user) {
        currentUser = user;
    }

    // Log out user by setting currentUser to null
    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String loggedInDisplayName() {
        if (currentUser != null)
            return currentUser.getDisplayName();
        else
            return "Gäst";
    }

    // Returns true if a user is logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
