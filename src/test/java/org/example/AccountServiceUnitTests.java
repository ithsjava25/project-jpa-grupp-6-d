package org.example;

import org.example.account.AccountService;
import org.example.account.repository.AccountRepository;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/* *********************************** */
/* **** AccountService Unit Tests **** */
/* *********************************** */

class AccountServiceUnitTests {

    @Test
    void testLoginFailed_WrongUsername_ShowsErrorMessage() {
        // Mock the repository
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Tell the mock to return null when trying to find 'testuser'
        when(mockRepository.findByUsername("testuser")).thenReturn(null);

        assertThatThrownBy(() -> accountService.login("wronguser", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Couldn't find user with username wronguser");
    }

    @Test
    void testLoginFailed_WrongPassword_ShowsErrorMessage() {
        // Mock the repository
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Create a user for testing
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("rightpassword");

        // Tell the mock to return 'testUser' when trying to find 'testuser'
        when(mockRepository.findByUsername("testuser")).thenReturn(testUser);

        // Test login with wrong password
        assertThatThrownBy(() -> accountService.login("testuser", "wrongPassword"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid password");
    }

    @Test
    void testLogin_Success() {
        // Mock the repository
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Create a user for testing
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("a$$word");

        // Tell the mock to return 'testUser' when trying to find 'testuser'
        when(mockRepository.findByUsername("testuser")).thenReturn(testUser);

        User result = accountService.login("testuser", "a$$word");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testCreateAccount_Success() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // !Help from AI!:
        // When createAccount is called, return the account that got sent in
        when(mockRepository.createAccount(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Tell test there's no username collision
        when(mockRepository.findByUsername(anyString())).thenReturn(null);

        // Create an account
        User created = accountService.createAccount("Test", "User", "test@test.com", "a$$word");

        // Make sure everything went through
        assertThat(created.getUsername()).isEqualTo("tesuse");
        assertThat(created.getFirstName()).isEqualTo("Test");
        assertThat(created.getEmail()).isEqualTo("test@test.com");

        verify(mockRepository, times(1)).createAccount(any(User.class));
    }

    @Test
    void testCreateAccount_InvalidEmail_ThrowsException() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        assertThatThrownBy(() ->
            accountService.createAccount("Test", "User", "invalidemail", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void testCreateAccount_ShortPassword_ThrowsException() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        assertThatThrownBy(() ->
            accountService.createAccount("Test", "User", "test@test.com", "12"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password must be at least 3 characters long");
    }

    @Test
    void testCreateAccount_EmptyFields_ThrowsException() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        assertThatThrownBy(() ->
            accountService.createAccount("", "User", "test@test.com", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No fields can be empty");
    }

    @Test
    void testCreateAccount_DuplicateEmail_ThrowsException() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Simulera att e-posten redan finns
        when(mockRepository.findByEmail("test@test.com")).thenReturn(new User());

        assertThatThrownBy(() ->
            accountService.createAccount("Test", "User", "test@test.com", "a$$word"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("An account with this email already exists");
    }

    @Test
    void testDeleteAccountById_Success() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Mock successful delete
        when(mockRepository.deleteAccountById(1337L)).thenReturn(true);

        // Run through delete method in service
        boolean result = accountService.deleteAccountById(1337L);

        // Make sure it was successful
        assertThat(result).isTrue();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteAccountById(1337L);
    }

    @Test
    void testDeleteAccountById_UserNotFound() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Mock failed delete
        when(mockRepository.deleteAccountById(1337L)).thenReturn(false);

        // Run through delete method in service
        boolean result = accountService.deleteAccountById(1337L);

        // Make sure it failed
        assertThat(result).isFalse();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteAccountById(1337L);
    }

    @Test
    void testDeleteAccountByUsername_Success() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Mock successful delete
        when(mockRepository.deleteAccountByUsername("testuser")).thenReturn(true);

        boolean result = accountService.deleteAccountByUsername("testuser");

        assertThat(result).isTrue();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteAccountByUsername("testuser");
    }

    @Test
    void testDeleteAccountByUsername_UserNotFound() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        // Mock return failed deletion
        when(mockRepository.deleteAccountByUsername("testuser")).thenReturn(false);

        boolean result = accountService.deleteAccountByUsername("testuser");

        assertThat(result).isFalse();
        // Verify that delete was called only once
        verify(mockRepository, times(1)).deleteAccountByUsername("testuser");
    }

    @Test
    void testFindByUsername_Found() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        User testUser = new User();
        testUser.setUsername("Waldo");

        when(mockRepository.findByUsername("Waldo")).thenReturn(testUser);

        User result = accountService.findByUsername("Waldo");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("Waldo");
    }

    @Test
    void testFindByUsername_NotFound() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        when(mockRepository.findByUsername("Waldo")).thenReturn(null);

        User result = accountService.findByUsername("Waldo");

        assertThat(result).isNull();
    }

    @Test
    void testFindAllUsers_ReturnsMultipleUsers() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        User user1 = new User();
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUsername("user2");

        // Create a list of users
        List<User> testUsers = Arrays.asList(user1, user2);

        // Tell the mock to return the list of users
        when(mockRepository.findAllUsers()).thenReturn(testUsers);

        // Run through find users method in service
        List<User> result = accountService.findAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    void testFindAllUsers_ReturnsEmptyList() {
        AccountRepository mockRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(mockRepository);

        when(mockRepository.findAllUsers()).thenReturn(Collections.emptyList());

        List<User> result = accountService.findAllUsers();

        assertThat(result).isEmpty();
    }
}


