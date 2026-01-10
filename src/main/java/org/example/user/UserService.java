package org.example.user;

import org.example.User;
import org.example.user.repository.UserRepository;

import java.util.List;
import java.util.regex.Pattern;

// User business logic
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        // Check if a user was found
        if (user == null) {
            throw new IllegalArgumentException("Couldn't find user with username " + username);
        }

        // Check if the password is correct
        if (!user.getPassword().equals(password) ) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    public User createUser(String firstName, String lastName, String email, String password) {
        // Check for null or empty
        if (isInvalid(firstName) || isInvalid(lastName) || isInvalid(email) || isInvalid(password)) {
            throw new IllegalArgumentException("No fields can be empty.");
        }

        // Validate email with regex pattern
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // Validate password length
        if (password.length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters long.");
        }

        // Check for existing email
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        // Generate username
        String username = generateUsername(firstName, lastName);

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setUsername(username);
        return userRepository.createUser(newUser);
    }

    public User updateUser(Long id, String firstName, String lastName, String email, String password) {
        // Get user by id
        User user = userRepository.findById(id);

        // Check if a user was found
        if (user == null) {
            throw new IllegalArgumentException("Couldn't find user with id " + id);
        }

        // Update user
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.updateUser(user);
    }

    public boolean deleteUserById(Long id) {
        return userRepository.deleteUserById(id);
    }

    public boolean deleteUserByUsername(String username) {
        return userRepository.deleteUserByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    private String generateUsername(String firstName, String lastName) {
        // Create username from first and last name (Dennis Dennisson = denden)
        String concatUsername = (createSubstrings(firstName) + createSubstrings(lastName)).toLowerCase();
        String username = concatUsername;

        int counter = 1;
        while(userRepository.findByUsername(username) != null) {
            username = concatUsername + counter;
            counter++;
        }
        return username;
    }

    private String createSubstrings(String name) {
        // Length of 3 or length of name if shorter than 3
        int length = Math.min(name.length(), 3);
        return name.substring(0, length);
    }

    private boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }

}
