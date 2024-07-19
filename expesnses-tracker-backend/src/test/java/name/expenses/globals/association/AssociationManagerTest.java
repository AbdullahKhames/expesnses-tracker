package name.expenses.globals.association;

import name.expenses.features.account.models.Account;
import name.expenses.features.account.service.AccountService;
import name.expenses.features.association.AssociationManager;
import name.expenses.features.association.Models;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.category.service.CategoryService;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssociationManagerTest {

    @Mock
    private BudgetService budgetService;

    @Mock
    private AccountService accountService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SubService subService;

    @Mock
    private ExpenseService expenseService;
//    @Mock
//    private budgetDAO budgetDAO;
//
//    @Mock
//    private budgetMapper budgetMapper;

//    @Mock
//    private budgetServiceImpl budgetServiceImpl;
    @Mock
    private CustomerService customerService;

    @InjectMocks
//    private AssociationManager<budgetUpdateDto, String, Account> associationManager;
    private AssociationManager associationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        associationManager.init();
    }

    @Test
    void addAssociation_Success() {
        String entityRefNo = "entityRefNo";
        Models entityModels = Models.ACCOUNT;
        Models associationModels = Models.Budget;

        Account account = new Account();
        Optional<Account> entityOptional = Optional.of(account);
        when(accountService.getEntity(entityRefNo)).thenReturn(entityOptional);


        ResponseDto mockResponse = ResponseDtoBuilder.getCreateResponse("Accoont", null, null);

        Set<String> budgetRefs = new HashSet<>(Arrays.asList("assoc1", "assoc2"));

        when(budgetService.addAssociation(account, entityModels, budgetRefs)).thenReturn(mockResponse);

        ResponseDto responseDto = associationManager.addAssociation(entityRefNo, entityModels, budgetRefs, associationModels);

        System.out.println(responseDto);
        assertNotNull(responseDto);
        verify(accountService, times(1)).getEntity(entityRefNo);
        verify(budgetService, times(1)).addAssociation(account,entityModels, budgetRefs);
    }

}