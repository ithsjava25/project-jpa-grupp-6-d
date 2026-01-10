package org.example;

import jakarta.persistence.EntityManager;
import org.example.user.UserService;
import org.example.user.repository.UserRepository;
import org.example.user.repository.JpaUserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/* *********************************** */
/* **** User integration tests **** */
/* *********************************** */

public class UserIT {

    private final UserRepository userRepository = new JpaUserRepository();
    private final UserService userService = new UserService(userRepository);

    // Makes sure we start with a clean slate after each test
    @AfterEach
    void deleteAllUsersAfterEachTest() {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        }
    }

    // Test a full user lifecycle to make sure database and business logic work together
    @Test
    void handlingFullUserLifecycle() {
        // Create a new user in the database
        User testUser = userService.createUser("Tester", "Testsson", "test@test.com", "assword");
        // Make sure a user was created and returned
        assertThat(userService.login(testUser.getUsername(), "assword")).isNotNull();

        // Update user in database
        testUser = userService.updateUser(testUser.getUserId(), "Testor", "Nytest", "nytest@nytest.com", "newPassword");
        // Make sure the user was updated in the database
        assertThat(userService.findById(testUser.getUserId()).getFirstName()).isEqualTo("Testor");

        // Remove user from database
        boolean deleted = userService.deleteUserById(testUser.getUserId());
        // Make sure the user was deleted from the database
        assertThat(deleted).isTrue();
        assertThat(userService.findById(testUser.getUserId())).isNull();
    }

}
