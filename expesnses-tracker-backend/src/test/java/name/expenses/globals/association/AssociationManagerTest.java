package name.expenses.globals.association;

import name.expenses.features.account.models.Account;
import name.expenses.features.account.service.AccountService;
import name.expenses.features.association.AssociationManager;
import name.expenses.features.association.Models;
import name.expenses.features.category.service.CategoryService;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.service.PocketService;
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
    private PocketService pocketService;

    @Mock
    private AccountService accountService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SubService subService;

    @Mock
    private ExpenseService expenseService;
//    @Mock
//    private PocketDAO pocketDAO;
//
//    @Mock
//    private PocketMapper pocketMapper;

//    @Mock
//    private PocketServiceImpl pocketServiceImpl;
    @Mock
    private CustomerService customerService;

    @InjectMocks
//    private AssociationManager<PocketUpdateDto, String, Account> associationManager;
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
        Models associationModels = Models.POCKET;

        Account account = new Account();
        Optional<Account> entityOptional = Optional.of(account);
        when(accountService.getEntity(entityRefNo)).thenReturn(entityOptional);


        ResponseDto mockResponse = ResponseDtoBuilder.getCreateResponse("Accoont", null, null);

        Set<String> pocketRefs = new HashSet<>(Arrays.asList("assoc1", "assoc2"));

        when(pocketService.addAssociation(account, entityModels, pocketRefs)).thenReturn(mockResponse);

        ResponseDto responseDto = associationManager.addAssociation(entityRefNo, entityModels, pocketRefs, associationModels);

        System.out.println(responseDto);
        assertNotNull(responseDto);
        verify(accountService, times(1)).getEntity(entityRefNo);
        verify(pocketService, times(1)).addAssociation(account,entityModels, pocketRefs);
    }

}