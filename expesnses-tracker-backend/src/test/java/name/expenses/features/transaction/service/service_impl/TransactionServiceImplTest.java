package name.expenses.features.transaction.service.service_impl;

import jakarta.persistence.EntityManager;
import name.expenses.Catalog;
import name.expenses.config.EntityManagerConfig;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.pocket_transfer.mappers.PocketAmountMapper;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.features.pocket_transfer.service.PocketAmountService;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.features.transaction.dao.TransactionDAO;
import name.expenses.features.transaction.dtos.request.TransactionUpdateDto;
import name.expenses.features.transaction.mappers.TransactionMapper;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.globals.responses.ResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
//    private static EntityManager entityManager = EntityManagerConfig.entityManager();
    private static final String REF_NO = "123";
    @Mock
    private TransactionDAO transactionDAO;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private PocketAmountMapper pocketAmountMapper;
    @Mock
    private CustomerService customerService;
    @Mock
    private PocketService pocketService;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private SubService subService;
    @Mock
    private PocketAmountService pocketAmountService;
    @InjectMocks
    private TransactionServiceImpl transactionService;
    private static TransactionUpdateDto transactionUpdateDto;
    private static Transaction transaction;
    private static Customer customer;
    private static Expense expense;
    private static Set<PocketAmount> pocketAmounts = new HashSet<>();
    private static PocketAmount pocketAmount1;
    private static PocketAmount pocketAmount2;
    private static PocketAmount pocketAmount3;
    private static Catalog catalog = new Catalog();
    @BeforeAll
    static void init(){
        expense = catalog.getObject(Expense.class, 1);

        customer = catalog.getObject(Customer.class, 1);

        transaction = catalog.getObject(Transaction.class, 1);

        pocketAmount1 = catalog.getObject(PocketAmount.class, 5);
        pocketAmount2 = catalog.getObject(PocketAmount.class, 6);
        pocketAmount3 = catalog.getObject(PocketAmount.class, 7);
        pocketAmounts.add(pocketAmount1);
//        pocketAmounts.add(pocketAmount2);
//        pocketAmounts.add(pocketAmount3);

        transaction.getPocketAmounts().addAll(pocketAmounts);
        transactionUpdateDto = TransactionUpdateDto.builder()
                .name("updated")
                .amount(200.0)
                .build();
    }
    @Test
    void test_update_transaction_NotFound() {
        when(transactionDAO.get(REF_NO)).thenReturn(Optional.empty());
        ResponseDto responseDto = transactionService.update(REF_NO, transactionUpdateDto);
        assertThat(responseDto.getCode()).isEqualTo(804);
    }
}