package org.example;

import org.example.repository.AccountRepository;

// Account business logic
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public User login(String username, String password) {
        User user = accountRepository.findByUsername(username);

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

    public User createAccount(String firstName, String lastName, String email, String password) {
        // Generate username
        String username = generateUsername(firstName, lastName);

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setUsername(username);
        return accountRepository.createAccount(newUser);
    }

    public User updateAccount(Long id, String firstName, String lastName, String email, String password) {
        // Get user by id
        User user = accountRepository.findById(id);

        // Check if a user was found
        if (user == null) {
            throw new IllegalArgumentException("Couldn't find user with id " + id);
        }

        // Update user
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return accountRepository.updateAccount(user);
    }

    public boolean deleteAccountById(Long id) {
        return accountRepository.deleteAccountById(id);
    }

    public boolean deleteAccountByUsername(String username) {
        return accountRepository.deleteAccountByUsername(username);
    }

    public User findById(Long id) {
        return accountRepository.findById(id);
    }

    public User findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Iterable<User> findAll() {
        return accountRepository.findAllUsers();
    }

    private String generateUsername(String firstName, String lastName) {
        // Create username from first and last name (Dennis Dennisson = denden)
        String concatUsername = (createSubstrings(firstName) + createSubstrings(lastName)).toLowerCase();
        String username = concatUsername;

        int counter = 1;
        while(accountRepository.findByUsername(username) != null) {
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

}
