package name.expenses.services.customerRel;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.EntityManagerConfig;
import name.expenses.models.*;
import name.expenses.services.utils.CurrentCustomerCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CustomerRelTest {
    EntityManager entityManager = EntityManagerConfig.entityManager();
    Customer customer;
    CustomerRel customerRel;
    @BeforeEach
    void setUp() {
        customer = entityManager.find(Customer.class, 1L);
        customerRel = new CustomerRel();
        customerRel.setEntityManager(entityManager);

    }

    @Test
    void getAllCustomerAccounts() {
        Page<Account> accountPage = customerRel.getAllCustomerAccounts(customer.getId(), 1L, 10L, "id", SortDirection.ASC);
        assertNotNull(accountPage);
//        assertEquals(3, accountPage.getTotalElements());
        log.info("{}", accountPage);
    }
}