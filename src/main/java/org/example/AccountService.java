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
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
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

    public boolean deleteAccount(Long id) {
        return accountRepository.deleteAccount(id);
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
}
