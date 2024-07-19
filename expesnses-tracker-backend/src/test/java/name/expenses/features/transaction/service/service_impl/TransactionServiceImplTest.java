package name.expenses.features.transaction.service.service_impl;

import jakarta.ws.rs.core.SecurityContext;
import name.expenses.Catalog;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;
import name.expenses.features.budget_transfer.mappers.BudgetAmountMapper;
import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.features.budget_transfer.service.BudgetAmountService;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.features.transaction.dao.TransactionDAO;
import name.expenses.features.transaction.dtos.request.TransactionUpdateDto;
import name.expenses.features.transaction.dtos.response.TransactionRespDto;
import name.expenses.features.transaction.mappers.TransactionMapper;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.features.user.models.User;
import name.expenses.globals.responses.ResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    //    private static EntityManager entityManager = EntityManagerConfig.entityManager();
    private static final String REF_NO = "123";
    private BudgetAmountUpdateDto budgetUpdateDto1;
    private BudgetAmountUpdateDto budgetAmountUpdateDto2;
    private BudgetAmountUpdateDto budgetAmountUpdateDto3;
    private BudgetAmountUpdateDto budgetAmountUpdateDto4;
    private BudgetAmountUpdateDto budgetAmountUpdateDto5;
    private BudgetAmount mappedbudgetAmount1;
    private BudgetAmount mappedbudgetAmount2;
    private BudgetAmount mappedbudgetAmount3;
    private BudgetAmount mappedbudgetAmount4;
    @Mock
    private TransactionDAO transactionDAO;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private BudgetAmountMapper budgetAmountMapper;
    @Mock
    private CustomerService customerService;
    @Mock
    private BudgetService budgetService;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private SubService subService;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private BudgetAmountService budgetAmountService = spy(BudgetAmountService.class);
    @InjectMocks
    private TransactionServiceImpl transactionService;
    private static TransactionUpdateDto transactionUpdateDto;
    private static Transaction transaction;
    private static Customer customer;
    private static Expense expense;
    private static Set<BudgetAmount> budgetAmounts = new HashSet<>();
    private static BudgetAmount budgetAmount4;
    private static BudgetAmount budgetAmount5;
    private static BudgetAmount budgetAmount6;
    private static BudgetAmount budgetAmount7;
    private static Budget budget3;
    private static Budget budget5;
    private static Budget budget6;
    private static Budget budget7;
    private static Budget budget8;
    private static Catalog catalog = new Catalog();
    @BeforeAll
    static void init(){
        expense = catalog.getObject(Expense.class, 1);

        customer = catalog.getObject(Customer.class, 1);

        transaction = catalog.getObject(Transaction.class, 1);

        budgetAmount4 = catalog.getObject(BudgetAmount.class, 4);
        budgetAmount5 = catalog.getObject(BudgetAmount.class, 5);
        budgetAmount6 = catalog.getObject(BudgetAmount.class, 6);
        budgetAmount7 = catalog.getObject(BudgetAmount.class, 7);
//        budgetAmounts.add(budgetAmount5);
//        budgetAmounts.add(budgetAmount6);
//        budgetAmounts.add(budgetAmount7);
        budget3 = catalog.getObject(Budget.class, 3);
        budget5 = catalog.getObject(Budget.class, 5);
        budget6 = catalog.getObject(Budget.class, 6);
        budget7 = catalog.getObject(Budget.class, 7);
        budget8 = catalog.getObject(Budget.class, 8);

//        transaction.getbudgetAmounts().addAll(budgetAmounts);

    }
    @BeforeEach
    public void beforeEach(){
        transactionService.setSecurityContext(securityContext);
        budgetUpdateDto1 = BudgetAmountUpdateDto.builder()
                .budgetRefNo(budget5.getRefNo())
                .refNo(budgetAmount5.getRefNo())
                .amount(25.0)
                .build();
        budgetAmountUpdateDto2 = BudgetAmountUpdateDto.builder()
                .budgetRefNo(budget7.getRefNo())
                .amount(25.0)
                .build();
        budgetAmountUpdateDto3 = BudgetAmountUpdateDto.builder()
                .budgetRefNo(budget7.getRefNo())
                .refNo(budgetAmount7.getRefNo())
                .amount(25.0)
                .build();
        budgetAmountUpdateDto4 = BudgetAmountUpdateDto.builder()
                .budgetRefNo(budget8.getRefNo())
                .amount(25.0)
                .build();
        budgetAmountUpdateDto5 = BudgetAmountUpdateDto.builder()
                .budgetRefNo(budget3.getRefNo())
                .refNo(budgetAmount4.getRefNo())
                .amount(25.0)
                .build();

        mappedbudgetAmount1 = new BudgetAmount();
        mappedbudgetAmount1.setBudget(budget5);
        mappedbudgetAmount1.setAmount(25.0);
        mappedbudgetAmount1.setRefNo(budgetAmount5.getRefNo());

        mappedbudgetAmount2 = new BudgetAmount();
        mappedbudgetAmount2.setBudget(budget6);
        mappedbudgetAmount2.setAmount(25.0);

        mappedbudgetAmount3 = new BudgetAmount();
        mappedbudgetAmount3.setBudget(budget7);
        mappedbudgetAmount3.setAmount(25.0);
        mappedbudgetAmount3.setRefNo(budgetAmount7.getRefNo());

        mappedbudgetAmount4 = new BudgetAmount();
        mappedbudgetAmount4.setBudget(budget8);
        mappedbudgetAmount4.setAmount(25.0);


        transactionUpdateDto = TransactionUpdateDto.builder()
                .name("updated")
                .amount(200.0)
                .budgetAmountUpdateDtos(new HashSet<>(Arrays.asList(
                        budgetUpdateDto1,
                        budgetAmountUpdateDto2,
                        budgetAmountUpdateDto3,
                        budgetAmountUpdateDto4,
                        budgetAmountUpdateDto5
                )))
                .build();
    }
    @Test
    void test_update_transaction_NotFound() {
        when(transactionDAO.get(REF_NO)).thenReturn(Optional.empty());
        ResponseDto responseDto = transactionService.update(REF_NO, transactionUpdateDto);
        assertThat(responseDto.getCode()).isEqualTo(804);
    }
    @Test
    void test_update_transaction_Found_NULL_SET() {
        transactionUpdateDto.setBudgetAmountUpdateDtos(null);
        when(transactionDAO.get(REF_NO)).thenReturn(Optional.of(transaction));
        assertThatThrownBy(() ->{
            transactionService.update(REF_NO, transactionUpdateDto);
        }).hasMessage("DB_001");
    }

    @Test
    void test_update_transaction_Found() {
        transaction.getExpense().setAmount(105);
        when(transactionDAO.get(REF_NO)).thenReturn(Optional.of(transaction));
        when(budgetAmountMapper.updateDtoToEntity(budgetAmountUpdateDto2)).thenReturn(mappedbudgetAmount2);
        when(budgetService.getEntity(budgetAmountUpdateDto2.getBudgetRefNo())).thenReturn(Optional.ofNullable(budget7));

        when(budgetAmountMapper.updateDtoToEntity(budgetAmountUpdateDto4)).thenReturn(mappedbudgetAmount4);
        when(budgetService.getEntity(budgetAmountUpdateDto4.getBudgetRefNo())).thenReturn(Optional.ofNullable(budget8));
        when(transactionDAO.update(transaction)).thenReturn(transaction);
        when(transactionMapper.entityToRespDto(transaction)).thenReturn(new TransactionRespDto());
        User user = new User();
        user.setCustomer(customer);
        when((User) securityContext.getUserPrincipal()).thenReturn(user);
//        when(budgetAmountService.updateAssociation(budgetAmount5, budgetUpdateDto1))
//        when(budgetService.getEntity(budgetUpdateDto1.getbudgetRefNo())).thenReturn(Optional.ofNullable(budget5));
        ResponseDto responseDto = transactionService.update(REF_NO, transactionUpdateDto);
        System.out.println(responseDto);
        verify(budgetAmountMapper, times(1)).updateDtoToEntity(budgetAmountUpdateDto2);
        verify(budgetAmountMapper, times(1)).updateDtoToEntity(budgetAmountUpdateDto4);
        verify(budgetAmountService, times(2)).updateAssociation(any(), any());
    }

}