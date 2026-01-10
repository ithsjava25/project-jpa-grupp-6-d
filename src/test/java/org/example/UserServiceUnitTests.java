package org.example;

import org.example.user.UserService;
import org.example.user.repository.UserRepository;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/* *********************************** */
/* **** UserService Unit Tests **** */
/* *********************************** */

class UserServiceUnitTests {

    @Test
    void testLoginFailed_WrongUsername_ShowsErrorMessage() {
        // Mock the repository
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Tell the mock to return null when trying to find 'testuser'
        when(mockRepository.findByUsername("testuser")).thenReturn(null);

        assertThatThrownBy(() -> userService.login("wronguser", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Couldn't find user with username wronguser");
    }

    @Test
    void testLoginFailed_WrongPassword_ShowsErrorMessage() {
        // Mock the repository
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Create a user for testing
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("rightpassword");

        // Tell the mock to return 'testUser' when trying to find 'testuser'
        when(mockRepository.findByUsername("testuser")).thenReturn(testUser);

        // Test login with wrong password
        assertThatThrownBy(() -> userService.login("testuser", "wrongPassword"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid password");
    }

    @Test
    void testLogin_Success() {
        // Mock the repository
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Create a user for testing
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("a$$word");

        // Tell the mock to return 'testUser' when trying to find 'testuser'
        when(mockRepository.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", "a$$word");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testCreateUser_Success() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // !Help from AI!:
        // When createUser is called, return the user that got sent in
        when(mockRepository.createUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Tell test there's no username collision
        when(mockRepository.findByUsername(anyString())).thenReturn(null);

        // Create a user
        User created = userService.createUser("Test", "User", "test@test.com", "a$$word");

        // Make sure everything went through
        assertThat(created.getUsername()).isEqualTo("tesuse");
        assertThat(created.getFirstName()).isEqualTo("Test");
        assertThat(created.getEmail()).isEqualTo("test@test.com");

        verify(mockRepository, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUser_InvalidEmail_ThrowsException() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        assertThatThrownBy(() ->
            userService.createUser("Test", "User", "invalidemail", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void testCreateUser_ShortPassword_ThrowsException() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        assertThatThrownBy(() ->
            userService.createUser("Test", "User", "test@test.com", "12"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password must be at least 3 characters long");
    }

    @Test
    void testCreateUser_EmptyFields_ThrowsException() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        assertThatThrownBy(() ->
            userService.createUser("", "User", "test@test.com", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No fields can be empty");
    }

    @Test
    void testCreateUser_DuplicateEmail_ThrowsException() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Simulera att e-posten redan finns
        when(mockRepository.findByEmail("test@test.com")).thenReturn(new User());

        assertThatThrownBy(() ->
            userService.createUser("Test", "User", "test@test.com", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("A user with this email already exists");
    }

    @Test
    void testDeleteUserById_Success() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Mock successful delete
        when(mockRepository.deleteUserById(1337L)).thenReturn(true);

        // Run through delete method in service
        boolean result = userService.deleteUserById(1337L);

        // Make sure it was successful
        assertThat(result).isTrue();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteUserById(1337L);
    }

    @Test
    void testDeleteUserById_UserNotFound() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Mock failed delete
        when(mockRepository.deleteUserById(1337L)).thenReturn(false);

        // Run through delete method in service
        boolean result = userService.deleteUserById(1337L);

        // Make sure it failed
        assertThat(result).isFalse();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteUserById(1337L);
    }

    @Test
    void testDeleteUserByUsername_Success() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Mock successful delete
        when(mockRepository.deleteUserByUsername("testuser")).thenReturn(true);

        boolean result = userService.deleteUserByUsername("testuser");

        assertThat(result).isTrue();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteUserByUsername("testuser");
    }

    @Test
    void testDeleteUserByUsername_UserNotFound() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        // Mock return failed deletion
        when(mockRepository.deleteUserByUsername("testuser")).thenReturn(false);

        boolean result = userService.deleteUserByUsername("testuser");

        assertThat(result).isFalse();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteUserByUsername("testuser");
    }

    @Test
    void testFindByUsername_Found() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        User testUser = new User();
        testUser.setUsername("Waldo");

        when(mockRepository.findByUsername("Waldo")).thenReturn(testUser);

        User result = userService.findByUsername("Waldo");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("Waldo");
    }

    @Test
    void testFindByUsername_NotFound() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        when(mockRepository.findByUsername("Waldo")).thenReturn(null);

        User result = userService.findByUsername("Waldo");

        assertThat(result).isNull();
    }

    @Test
    void testFindAllUsers_ReturnsMultipleUsers() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        User user1 = new User();
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUsername("user2");

        // Create a list of users
        List<User> testUsers = Arrays.asList(user1, user2);

        // Tell the mock to return the list of users
        when(mockRepository.findAllUsers()).thenReturn(testUsers);

        // Run through find users method in service
        List<User> result = userService.findAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    void testFindAllUsers_ReturnsEmptyList() {
        UserRepository mockRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockRepository);

        when(mockRepository.findAllUsers()).thenReturn(Collections.emptyList());

        List<User> result = userService.findAllUsers();

        assertThat(result).isEmpty();
    }
}


