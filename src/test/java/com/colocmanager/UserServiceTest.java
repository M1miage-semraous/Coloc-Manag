package com.colocmanager;

import com.colocmanager.enums.Role;
import com.colocmanager.model.User;
import com.colocmanager.repository.UserRepository;
import com.colocmanager.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    // ===== CRÉATION UTILISATEUR =====

    @Test
    @DisplayName("Créer un utilisateur avec des données valides")
    void testCreateUser_Success() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());

        User user = userService.createUser("Alice", "alice@test.com", "password123", Role.USER);

        assertNotNull(user);
        assertEquals("Alice", user.getFullName());
        assertEquals("alice@test.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getId());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Créer un utilisateur avec un email déjà existant doit échouer")
    void testCreateUser_EmailAlreadyExists() {
        User existingUser = new User("Bob", "bob@test.com", "pass", Role.USER);
        when(userRepository.findByEmail("bob@test.com")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("Bob2", "bob@test.com", "pass2", Role.USER)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Le mot de passe doit être hashé à la création")
    void testCreateUser_PasswordIsHashed() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        User user = userService.createUser("Test", "test@test.com", "motdepasse", Role.USER);

        assertNotEquals("motdepasse", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$"));
    }

    @Test
    @DisplayName("Créer un utilisateur ADMIN")
    void testCreateAdmin_Success() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());

        User admin = userService.createUser("Admin", "admin@test.com", "admin123", Role.ADMIN);

        assertEquals(Role.ADMIN, admin.getRole());
    }

    // ===== CONNEXION =====

    @Test
    @DisplayName("Connexion avec email et mot de passe corrects")
    void testLogin_Success() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        User user = userService.createUser("Alice", "alice@test.com", "password123", Role.USER);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("alice@test.com", "password123");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFullName());
    }

    @Test
    @DisplayName("Connexion avec mauvais mot de passe doit échouer")
    void testLogin_WrongPassword() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        User user = userService.createUser("Alice", "alice@test.com", "password123", Role.USER);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("alice@test.com", "wrongpassword");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Connexion avec email inexistant doit échouer")
    void testLogin_EmailNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.login("unknown@test.com", "password");

        assertFalse(result.isPresent());
    }

    // ===== RECHERCHE =====

    @Test
    @DisplayName("Trouver un utilisateur par son ID")
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        User user = new User("Alice", "alice@test.com", "pass", Role.USER);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    @DisplayName("Trouver un utilisateur par email")
    void testFindByEmail_Success() {
        User user = new User("Alice", "alice@test.com", "pass", Role.USER);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("alice@test.com");

        assertTrue(result.isPresent());
        assertEquals("alice@test.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Récupérer tous les utilisateurs")
    void testGetAllUsers() {
        List<User> users = List.of(
                new User("Alice", "alice@test.com", "pass", Role.USER),
                new User("Bob",   "bob@test.com",   "pass", Role.USER)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    // ===== SUPPRESSION =====

    @Test
    @DisplayName("Supprimer un utilisateur existant")
    void testDeleteUser_Success() {
        UUID id = UUID.randomUUID();
        User user = new User("Alice", "alice@test.com", "pass", Role.USER);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(id);

        assertTrue(result);
        verify(userRepository, times(1)).delete(id);
    }

    @Test
    @DisplayName("Supprimer un utilisateur inexistant doit retourner false")
    void testDeleteUser_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(id);

        assertFalse(result);
        verify(userRepository, never()).delete(any());
    }

    // ===== MISE À JOUR =====

    @Test
    @DisplayName("Mettre à jour un utilisateur existant")
    void testUpdateUser_Success() {
        UUID id = UUID.randomUUID();
        User user = new User("Alice", "alice@test.com", "pass", Role.USER);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("newalice@test.com")).thenReturn(Optional.empty());

        boolean result = userService.updateUser(id, "Alice Updated", "newalice@test.com", null, null);

        assertTrue(result);
        assertEquals("Alice Updated", user.getFullName());
        assertEquals("newalice@test.com", user.getEmail());
    }

    @Test
    @DisplayName("Mettre à jour avec un email déjà pris doit échouer")
    void testUpdateUser_EmailTaken() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        User user = new User("Alice", "alice@test.com", "pass", Role.USER);
        user.setId(id);

        User other = new User("Bob", "bob@test.com", "pass", Role.USER);
        other.setId(otherId);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("bob@test.com")).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(id, null, "bob@test.com", null, null)
        );
    }
}