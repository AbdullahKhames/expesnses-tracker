package name.expenses.services.utils;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.EntityManagerConfig;
import name.expenses.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class CurrentCustomerCollectionsTest {
    EntityManager entityManager = EntityManagerConfig.entityManager();
    User user;
    CurrentCustomerCollections currentCustomerCollections;
    @BeforeEach
    void setUp() {
        user = entityManager.find(User.class, 1L);
        currentCustomerCollections = new CurrentCustomerCollections();
        currentCustomerCollections.setEntityManager(entityManager);
    }

    @Test
    void isAccountPresentCollection() {
        Account account = entityManager.find(Account.class, 1L);
        Account account2 = entityManager.find(Account.class, 2L);
        log.info("{}", user);

        boolean presentCollection = currentCustomerCollections.isPresentCollection(user, account, Models.ACCOUNT);
        log.info("is the first customer has the first account {}", presentCollection);
        assertFalse(presentCollection);

        boolean presentCollection1 = currentCustomerCollections.isPresentCollection(user, account2, Models.ACCOUNT);
        log.info("is the first customer has the second account {}", presentCollection1);
        assertTrue(presentCollection1);
    }
    @Test
    void isPocketPresentCollection() {
        Pocket account = entityManager.find(Pocket.class, 1L);
        Pocket account2 = entityManager.find(Pocket.class, 2L);
        log.info("{}", user);

        boolean presentCollection = currentCustomerCollections.isPresentCollection(user, account, Models.POCKET);
        log.info("is the first customer has the first pocket {}", presentCollection);
        assertTrue(presentCollection);

        boolean presentCollection1 = currentCustomerCollections.isPresentCollection(user, account2, Models.POCKET);
        log.info("is the first customer has the second pocket {}", presentCollection1);
        assertTrue(presentCollection1);
    }
    @Test
    void isPocketPresentCollectionFalse() {
        User user1 = entityManager.find(User.class, 2L);
        Pocket account = entityManager.find(Pocket.class, 1L);
        Pocket account2 = entityManager.find(Pocket.class, 2L);
        log.info("{}", user);

        boolean presentCollection = currentCustomerCollections.isPresentCollection(user1, account, Models.POCKET);
        log.info("is the first customer has the first pocket {}", presentCollection);
        assertFalse(presentCollection);

        boolean presentCollection1 = currentCustomerCollections.isPresentCollection(user1, account2, Models.POCKET);
        log.info("is the first customer has the second pocket {}", presentCollection1);
        assertFalse(presentCollection1);
    }

    @Test
    void isCategoryPresentCollection() {
        Category category = entityManager.find(Category.class, 1L);
        Category category2 = entityManager.find(Category.class, 2L);
        log.info("{}", user);

        boolean presentCollection = currentCustomerCollections.isPresentCollection(user, category, Models.CATEGORY);
        log.info("is the first customer has the first category {}", presentCollection);
        assertTrue(presentCollection);

        boolean presentCollection1 = currentCustomerCollections.isPresentCollection(user, category2, Models.CATEGORY);
        log.info("is the first customer has the second category {}", presentCollection1);
        assertFalse(presentCollection1);
    }
    @Test
    void isSubCategoryPresentCollection() {
        SubCategory subCategory = entityManager.find(SubCategory.class, 1L);
        SubCategory subCategory2 = entityManager.find(SubCategory.class, 2L);
        log.info("{}", user);

        boolean presentCollection = currentCustomerCollections.isPresentCollection(user, subCategory, Models.SUB_CATEGORY);
        log.info("is the first customer has the first subCategory {}", presentCollection);
        assertTrue(presentCollection);

        boolean presentCollection1 = currentCustomerCollections.isPresentCollection(user, subCategory2, Models.SUB_CATEGORY);
        log.info("is the first customer has the second subCategory {}", presentCollection1);
        assertFalse(presentCollection1);
    }
    @Test
    void isExpensePresentCollection() {
        Expense expense = entityManager.find(Expense.class, 1L);
        Expense expense2 = entityManager.find(Expense.class, 2L);
        log.info("{}", user);

        boolean presentCollection = currentCustomerCollections.isPresentCollection(user, expense, Models.EXPENSE);
        log.info("is the first customer has the first expense {}", presentCollection);
        assertTrue(presentCollection);

        boolean presentCollection1 = currentCustomerCollections.isPresentCollection(user, expense2, Models.EXPENSE);
        log.info("is the first customer has the second expense {}", presentCollection1);
        assertTrue(presentCollection1);
    }

}