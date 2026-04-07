package com.colocmanager;

import com.colocmanager.enums.Role;
import com.colocmanager.model.Expense;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;
import com.colocmanager.repository.ExpenseRepository;
import com.colocmanager.repository.ExpenseShareRepository;
import com.colocmanager.service.ExpenseService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — ExpenseService")
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private ExpenseShareRepository expenseShareRepository;

    private ExpenseService expenseService;
    private User adnan;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository, expenseShareRepository);

        adnan = new User("Adnan", "adnan@test.com", "pass", Role.ADMIN);
        adnan.setId(UUID.randomUUID());

        alice = new User("Alice", "alice@test.com", "pass", Role.USER);
        alice.setId(UUID.randomUUID());

        bob = new User("Bob", "bob@test.com", "pass", Role.USER);
        bob.setId(UUID.randomUUID());
    }

    // ===== CRÉATION =====

    @Test
    @DisplayName("Créer une dépense et répartir entre 3 participants")
    void testCreateExpense_SplitAmong3() {
        List<User> participants = List.of(adnan, alice, bob);

        Expense expense = expenseService.createExpense(
                "Courses", 30.0, "", LocalDate.now(), adnan, participants
        );

        assertNotNull(expense);
        assertEquals("Courses", expense.getLabel());
        assertEquals(30.0, expense.getAmount());
        assertEquals(3, expense.getShares().size());
        expense.getShares().forEach(s -> assertEquals(10.0, s.getAmountDue(), 0.01));

        verify(expenseRepository, times(1)).save(expense);
        verify(expenseShareRepository, times(3)).save(any(), any());
    }


    @Test
    @DisplayName("Créer une dépense avec 1 seul participant")
    void testCreateExpense_SingleParticipant() {
        Expense expense = expenseService.createExpense(
                "Achat perso", 50.0, "", LocalDate.now(), adnan, List.of(adnan)
        );

        assertEquals(1, expense.getShares().size());
        assertEquals(50.0, expense.getShares().get(0).getAmountDue(), 0.01);
    }

    @Test
    @DisplayName("Montant divisé correctement entre participants")
    void testCreateExpense_CorrectSplit() {
        List<User> participants = List.of(adnan, alice);

        Expense expense = expenseService.createExpense(
                "Loyer", 100.0, "", LocalDate.now(), adnan, participants
        );

        assertEquals(2, expense.getShares().size());
        expense.getShares().forEach(s -> assertEquals(50.0, s.getAmountDue(), 0.01));
    }

    // ===== RÉCUPÉRATION =====

    @Test
    @DisplayName("Récupérer toutes les dépenses")
    void testGetAllExpenses() {
        List<Expense> expenses = List.of(
                new Expense("Courses", 30.0, "", LocalDate.now(), adnan),
                new Expense("Loyer",  800.0, "", LocalDate.now(), adnan)
        );
        when(expenseRepository.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getAllExpenses();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Récupérer les dépenses d'un utilisateur")
    void testGetExpensesByUser() {
        List<Expense> expenses = List.of(
                new Expense("Courses", 30.0, "", LocalDate.now(), adnan)
        );
        when(expenseRepository.findByPaidBy(adnan.getId())).thenReturn(expenses);

        List<Expense> result = expenseService.getExpensesByUser(adnan.getId());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Récupérer le total dû par un utilisateur")
    void testGetTotalDueByUser() {
        when(expenseShareRepository.getTotalDueByUser(alice.getId())).thenReturn(25.5);

        double total = expenseService.getTotalDueByUser(alice.getId());
        assertEquals(25.5, total, 0.01);
    }

    // ===== SUPPRESSION =====

    @Test
    @DisplayName("Supprimer une dépense supprime aussi ses parts")
    void testDeleteExpense_CascadeShares() {
        UUID expenseId = UUID.randomUUID();

        expenseService.deleteExpense(expenseId);

        verify(expenseShareRepository, times(1)).deleteByExpenseId(expenseId);
        verify(expenseRepository, times(1)).delete(expenseId);
    }

    // ===== PAIEMENT =====

    @Test
    @DisplayName("Marquer une part comme payée")
    void testMarkShareAsPaid() {
        UUID shareId = UUID.randomUUID();

        expenseService.markShareAsPaid(shareId);

        verify(expenseShareRepository, times(1)).markAsPaid(shareId);
    }

    @Test
    @DisplayName("Récupérer les parts d'un utilisateur")
    void testGetSharesForUser() {
        List<ExpenseShare> shares = List.of(
                new ExpenseShare(alice, 10.0),
                new ExpenseShare(alice, 25.0)
        );
        when(expenseShareRepository.findByUserId(alice.getId())).thenReturn(shares);

        List<ExpenseShare> result = expenseService.getSharesForUser(alice.getId());
        assertEquals(2, result.size());
    }
}
