package name.expenses.config.filters;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import name.expenses.features.user.service.UserDetailsService;
import name.expenses.utils.property_loader.PropertyLoaderComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BasicSecurityFilterTest {
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PropertyLoaderComponent propertyLoaderComponent;
    @InjectMocks
    private BasicSecurityFilter basicSecurityFilter;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void filter() {
    }

    @Test
    void isPathInWhitelist() {
        assertTrue(basicSecurityFilter.isPathInWhitelist(new ArrayList<>(Arrays.asList("/expenses-tracker/api/user/** POST")),
                "/expenses-tracker/api/user/login", "POST"));
    }
    @Test
    void isPathInWhitelistWrongMethod() {
        assertFalse(basicSecurityFilter.isPathInWhitelist(new ArrayList<>(Arrays.asList("/expenses-tracker/api/user/** POST")),
                "/expenses-tracker/api/user/login", "GET"));
    }
    @Test
    void testSharedAPIS() {

        assertTrue(basicSecurityFilter.isPathInWhitelist(new ArrayList<>(Arrays.asList("/expenses-tracker/api/expenses/** *", "/expenses-tracker/api/pockets/** *")),
                "/expenses-tracker/api/expenses", "POST"));
    }
    @Test
    void testSharedAPIS2() {

        assertTrue(basicSecurityFilter.isPathInWhitelist(new ArrayList<>(Arrays.asList("/expenses-tracker/api/expenses/** *", "/expenses-tracker/api/pockets/** *")),
                "/expenses-tracker/api/expenses/logs", "POST"));
    }

}