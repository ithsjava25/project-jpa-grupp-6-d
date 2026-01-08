package org.example.repository;

import org.example.User;
import java.util.List;

public interface AccountRepository {
    User createAccount(User user);
    User updateAccount(User user);
    boolean deleteAccount(Long id);

    User findById(Long id);
    User findByEmail(String email);
    User findByUsername(String username);

    List<User> findAllUsers();
}
