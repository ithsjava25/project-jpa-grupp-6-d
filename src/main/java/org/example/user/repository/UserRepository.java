package org.example.user.repository;

import org.example.User;
import java.util.List;

public interface UserRepository {
    User createUser(User user);
    User updateUser(User user);
    boolean deleteUserById(Long id);
    boolean deleteUserByUsername(String username);

    User findById(Long id);
    User findByEmail(String email);
    User findByUsername(String username);

    List<User> findAllUsers();
}
