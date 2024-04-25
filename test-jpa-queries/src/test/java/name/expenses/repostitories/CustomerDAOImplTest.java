package name.expenses.repostitories;

import jakarta.persistence.EntityManager;
import name.expenses.config.EntityManagerConfig;
import name.expenses.models.*;
import name.expenses.services.utils.CurrentCustomerCollections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDAOImplTest {
    EntityManager entityManager = EntityManagerConfig.entityManager();
    Customer customer;
    CustomerDAOImpl customerDAO;
    @BeforeEach
    void setUp() {
        customer = entityManager.find(Customer.class, 1L);
        customerDAO = new CustomerDAOImpl();
        customerDAO.setEntityManager(entityManager);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllCustomerFirstAccountPockets() {
        Page<Pocket> pocketPage = customerDAO.getAllCustomerAccountPockets(customer.getId(), "ff810458-ad48-48be-abab-0d40aa4d528f", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(2, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerSecondAccountPockets() {
        Page<Pocket> pocketPage = customerDAO.getAllCustomerAccountPockets(customer.getId(), "65f895b4-73af-42ba-a9a0-9f06769a6451", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(3, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerThirdAccountPockets() {
        Page<Pocket> pocketPage = customerDAO.getAllCustomerAccountPockets(customer.getId(), "c97dba7c-e0e4-46a3-bf2c-7a17551fba95", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(0, pocketPage.getTotalElements());
    }


    @Test
    void getAllCustomerFirstCategorySubCategories() {
        Page<SubCategory> pocketPage = customerDAO.getAllCustomerCategorySubCategories(customer.getId(), "2de45e63-450b-418e-bbbc-a567dc7abd67", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(1, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerSecondCategorySubCategories() {
        Page<SubCategory> pocketPage = customerDAO.getAllCustomerCategorySubCategories(customer.getId(), "cd6774d9-24e5-41e9-99e5-c72cbd33e1eb", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(0, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerNinthCategorySubCategories() {
        Page<SubCategory> pocketPage = customerDAO.getAllCustomerCategorySubCategories(customer.getId(), "6bd9bca6-2cba-4542-98fe-2c737e1df422", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(1, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerFirstSubCategoryExpenses() {
        Page<Expense> pocketPage = customerDAO.getAllCustomerSubCategoryExpenses(customer.getId(), "ebbd3d18-7aa9-4da2-ad64-83c70731af1f", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(4, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerSecondSubCategoryExpenses() {
        Page<Expense> pocketPage = customerDAO.getAllCustomerSubCategoryExpenses(customer.getId(), "af61c5fe-050a-4b1d-bd85-9e0249350254", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(0, pocketPage.getTotalElements());
    }
    @Test
    void getAllCustomerFourtenthSubCategoryExpenses() {
        Page<Expense> pocketPage = customerDAO.getAllCustomerSubCategoryExpenses(customer.getId(), "dbc1256a-229e-4473-bedd-c226e8a58ae4", 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(3, pocketPage.getTotalElements());
    }

    @Test
    void getAllCustomerPockets() {
        Page<Pocket> pocketPage = customerDAO.getAllCustomerPockets(customer.getId(), 1L, 10L, "id", SortDirection.ASC);
        System.out.println(pocketPage);

        assertEquals(8, pocketPage.getTotalElements());
    }
}