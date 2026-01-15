package org.example.user.repository;

import org.example.User;
import java.util.List;

public interface UserRepository {
    User createUser(User user);
    User updateUser(Long id, String firstName, String lastName, String email, String password);
    boolean deleteUserById(Long id);
    boolean deleteUserByUsername(String username);

    User findById(Long id);
    User findByEmail(String email);
    User findByUsername(String username);

    List<User> findAllUsers();
}
