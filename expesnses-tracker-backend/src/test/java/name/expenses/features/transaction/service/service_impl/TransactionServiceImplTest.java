package name.expenses.features.transaction.service.service_impl;

import jakarta.ws.rs.core.SecurityContext;
import name.expenses.Catalog;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.pocket_transfer.dtos.request.PocketAmountUpdateDto;
import name.expenses.features.pocket_transfer.mappers.PocketAmountMapper;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.features.pocket_transfer.service.PocketAmountService;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    //    private static EntityManager entityManager = EntityManagerConfig.entityManager();
    private static final String REF_NO = "123";
    private PocketAmountUpdateDto pocketUpdateDto1;
    private PocketAmountUpdateDto pocketAmountUpdateDto2;
    private PocketAmountUpdateDto pocketAmountUpdateDto3;
    private PocketAmountUpdateDto pocketAmountUpdateDto4;
    private PocketAmountUpdateDto pocketAmountUpdateDto5;
    private PocketAmount mappedPocketAmount1;
    private PocketAmount mappedPocketAmount2;
    private PocketAmount mappedPocketAmount3;
    private PocketAmount mappedPocketAmount4;
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
    private SecurityContext securityContext;
    @InjectMocks
    private PocketAmountService pocketAmountService = spy(PocketAmountService.class);
    @InjectMocks
    private TransactionServiceImpl transactionService;
    private static TransactionUpdateDto transactionUpdateDto;
    private static Transaction transaction;
    private static Customer customer;
    private static Expense expense;
    private static Set<PocketAmount> pocketAmounts = new HashSet<>();
    private static PocketAmount pocketAmount4;
    private static PocketAmount pocketAmount5;
    private static PocketAmount pocketAmount6;
    private static PocketAmount pocketAmount7;
    private static Pocket pocket3;
    private static Pocket pocket5;
    private static Pocket pocket6;
    private static Pocket pocket7;
    private static Pocket pocket8;
    private static Catalog catalog = new Catalog();
    @BeforeAll
    static void init(){
        expense = catalog.getObject(Expense.class, 1);

        customer = catalog.getObject(Customer.class, 1);

        transaction = catalog.getObject(Transaction.class, 1);

        pocketAmount4 = catalog.getObject(PocketAmount.class, 4);
        pocketAmount5 = catalog.getObject(PocketAmount.class, 5);
        pocketAmount6 = catalog.getObject(PocketAmount.class, 6);
        pocketAmount7 = catalog.getObject(PocketAmount.class, 7);
//        pocketAmounts.add(pocketAmount5);
//        pocketAmounts.add(pocketAmount6);
//        pocketAmounts.add(pocketAmount7);
        pocket3 = catalog.getObject(Pocket.class, 3);
        pocket5 = catalog.getObject(Pocket.class, 5);
        pocket6 = catalog.getObject(Pocket.class, 6);
        pocket7 = catalog.getObject(Pocket.class, 7);
        pocket8 = catalog.getObject(Pocket.class, 8);

//        transaction.getPocketAmounts().addAll(pocketAmounts);

    }
    @BeforeEach
    public void beforeEach(){
        transactionService.setSecurityContext(securityContext);
        pocketUpdateDto1 = PocketAmountUpdateDto.builder()
                .pocketRefNo(pocket5.getRefNo())
                .refNo(pocketAmount5.getRefNo())
                .amount(25.0)
                .build();
        pocketAmountUpdateDto2 = PocketAmountUpdateDto.builder()
                .pocketRefNo(pocket7.getRefNo())
                .amount(25.0)
                .build();
        pocketAmountUpdateDto3 = PocketAmountUpdateDto.builder()
                .pocketRefNo(pocket7.getRefNo())
                .refNo(pocketAmount7.getRefNo())
                .amount(25.0)
                .build();
        pocketAmountUpdateDto4 = PocketAmountUpdateDto.builder()
                .pocketRefNo(pocket8.getRefNo())
                .amount(25.0)
                .build();
        pocketAmountUpdateDto5 = PocketAmountUpdateDto.builder()
                .pocketRefNo(pocket3.getRefNo())
                .refNo(pocketAmount4.getRefNo())
                .amount(25.0)
                .build();

        mappedPocketAmount1 = new PocketAmount();
        mappedPocketAmount1.setPocket(pocket5);
        mappedPocketAmount1.setAmount(25.0);
        mappedPocketAmount1.setRefNo(pocketAmount5.getRefNo());

        mappedPocketAmount2 = new PocketAmount();
        mappedPocketAmount2.setPocket(pocket6);
        mappedPocketAmount2.setAmount(25.0);

        mappedPocketAmount3 = new PocketAmount();
        mappedPocketAmount3.setPocket(pocket7);
        mappedPocketAmount3.setAmount(25.0);
        mappedPocketAmount3.setRefNo(pocketAmount7.getRefNo());

        mappedPocketAmount4 = new PocketAmount();
        mappedPocketAmount4.setPocket(pocket8);
        mappedPocketAmount4.setAmount(25.0);


        transactionUpdateDto = TransactionUpdateDto.builder()
                .name("updated")
                .amount(200.0)
                .pocketAmountUpdateDtos(new HashSet<>(Arrays.asList(
                        pocketUpdateDto1,
                        pocketAmountUpdateDto2,
                        pocketAmountUpdateDto3,
                        pocketAmountUpdateDto4,
                        pocketAmountUpdateDto5
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
        transactionUpdateDto.setPocketAmountUpdateDtos(null);
        when(transactionDAO.get(REF_NO)).thenReturn(Optional.of(transaction));
        assertThatThrownBy(() ->{
            transactionService.update(REF_NO, transactionUpdateDto);
        }).hasMessage("DB_001");
    }

    @Test
    void test_update_transaction_Found() {
        transaction.getExpense().setAmount(105);
        when(transactionDAO.get(REF_NO)).thenReturn(Optional.of(transaction));
        when(pocketAmountMapper.updateDtoToEntity(pocketAmountUpdateDto2)).thenReturn(mappedPocketAmount2);
        when(pocketService.getEntity(pocketAmountUpdateDto2.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket7));

        when(pocketAmountMapper.updateDtoToEntity(pocketAmountUpdateDto4)).thenReturn(mappedPocketAmount4);
        when(pocketService.getEntity(pocketAmountUpdateDto4.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket8));
        when(transactionDAO.update(transaction)).thenReturn(transaction);
        when(transactionMapper.entityToRespDto(transaction)).thenReturn(new TransactionRespDto());
        User user = new User();
        user.setCustomer(customer);
        when((User) securityContext.getUserPrincipal()).thenReturn(user);
//        when(pocketAmountService.updateAssociation(pocketAmount5, pocketUpdateDto1))
//        when(pocketService.getEntity(pocketUpdateDto1.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket5));
        ResponseDto responseDto = transactionService.update(REF_NO, transactionUpdateDto);
        System.out.println(responseDto);
        verify(pocketAmountMapper, times(1)).updateDtoToEntity(pocketAmountUpdateDto2);
        verify(pocketAmountMapper, times(1)).updateDtoToEntity(pocketAmountUpdateDto4);
        verify(pocketAmountService, times(2)).updateAssociation(any(), any());
    }

}