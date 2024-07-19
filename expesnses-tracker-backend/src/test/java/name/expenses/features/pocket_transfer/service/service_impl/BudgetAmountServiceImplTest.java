package name.expenses.features.pocket_transfer.service.service_impl;

import name.expenses.Catalog;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.budget.mappers.BudgetMapper;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.budget_transfer.dao.BudgetAmountDAO;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;
import name.expenses.features.budget_transfer.mappers.BudgetAmountMapper;
import name.expenses.features.budget_transfer.models.AmountType;
import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.features.budget_transfer.service.service_impl.BudgetAmountServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetAmountServiceImplTest {
    @InjectMocks
    private BudgetAmountMapper budgetAmountMapper = spy(BudgetAmountMapper.class);
    @InjectMocks
    private  BudgetMapper budgetMapper = spy(BudgetMapper.class);
    @InjectMocks
    private AccountMapper accountMapper = spy(AccountMapper.class);
    @Mock
    private BudgetService budgetService;
    @Mock
    private BudgetAmountDAO budgetAmountDAO;

    @InjectMocks
    BudgetAmountServiceImpl budgetAmountService;
    BudgetAmountUpdateDto budgetAmountUpdateDto;
    BudgetAmount budgetAmount1;
    Budget budget1;
    Budget budget2;
    Catalog catalog = new Catalog();
    @BeforeEach
    void setUp() {
        budgetAmount1 = catalog.getObject(BudgetAmount.class, 1);
        budget1 = catalog.getObject(Budget.class, 1);
        budget2 = catalog.getObject(Budget.class, 2);
        budgetAmountUpdateDto = budgetAmountUpdateDto.builder()
                .amount(100.0)
                .refNo(budgetAmount1.getRefNo())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateAssociationSamebudgetCredit() {
        budgetAmountUpdateDto.setBudgetRefNo(budget1.getRefNo());
        budgetAmount1.setAmountType(AmountType.CREDIT);

        when(budgetService.getEntity(budgetAmountUpdateDto.getBudgetRefNo())).thenReturn(Optional.ofNullable(budget1));

        budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetAmountMapper, times(1)).update(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetService,times(1)).update(budget1);
        assertThat(budget1.getAmount()).isEqualTo(550);
        assertThat(budget2.getAmount()).isEqualTo(150);

    }
    @Test
    void updateAssociationSamebudgetDEBIT() {
        budgetAmountUpdateDto.setBudgetRefNo(budget1.getRefNo());

        when(budgetService.getEntity(budgetAmountUpdateDto.getBudgetRefNo())).thenReturn(Optional.ofNullable(budget1));

        budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetAmountMapper, times(1)).update(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetService,times(1)).update(budget1);
        assertThat(budget1.getAmount()).isEqualTo(450);
        assertThat(budget2.getAmount()).isEqualTo(150);
    }
    @Test
    void updateAssociationDiffbudgetCredit() {
        budgetAmountUpdateDto.setBudgetRefNo(budget2.getRefNo());
        budgetAmount1.setAmountType(AmountType.CREDIT);
        doAnswer((context) -> {
            BudgetAmount pockAmt = context.getArgument(0);
            BudgetAmountUpdateDto pockAmtDto = context.getArgument(1);
            pockAmt.setAmount(pockAmtDto.getAmount());
            return pockAmt;
        }).when(budgetAmountMapper).update(budgetAmount1, budgetAmountUpdateDto);
        when(budgetService.getEntity(budgetAmountUpdateDto.getBudgetRefNo())).thenReturn(Optional.ofNullable(budget2));

        budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetAmountMapper, times(1)).update(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetService,times(1)).update(budget1);
        assertThat(budget1.getAmount()).isEqualTo(450);
        assertThat(budget2.getAmount()).isEqualTo(250);


    }
    @Test
    void updateAssociationDiffbudgetDEBIT() {
        budgetAmountUpdateDto.setBudgetRefNo(budget2.getRefNo());

        when(budgetService.getEntity(budgetAmountUpdateDto.getBudgetRefNo())).thenReturn(Optional.ofNullable(budget2));

        doAnswer((context) -> {
            BudgetAmount pockAmt = context.getArgument(0);
            BudgetAmountUpdateDto pockAmtDto = context.getArgument(1);
            pockAmt.setAmount(pockAmtDto.getAmount());
            return pockAmt;
        }).when(budgetAmountMapper).update(budgetAmount1, budgetAmountUpdateDto);
        budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetAmountMapper, times(1)).update(budgetAmount1, budgetAmountUpdateDto);
        verify(budgetService,times(1)).update(budget1);
        assertThat(budget1.getAmount()).isEqualTo(550);
        assertThat(budget2.getAmount()).isEqualTo(50);

    }
    @Test
    void updateAssociationNoRefNo() {
        budgetAmountUpdateDto.setRefNo(null);
        assertThatThrownBy(() -> budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto))
                .isInstanceOf(APIException.class)
                .hasMessage(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
    }
    @Test
    void updateAssociationDiffRefNo() {
        budgetAmountUpdateDto.setRefNo("1234");
        assertThatThrownBy(() -> budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto))
                .isInstanceOf(APIException.class)
                .hasMessage(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
    }

    @Test
    void updateAssociationEmptyOptional() {
        budgetAmountUpdateDto.setBudgetRefNo(budget1.getRefNo());

        when(budgetService.getEntity(budgetAmountUpdateDto.getBudgetRefNo())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> budgetAmountService.updateAssociation(budgetAmount1, budgetAmountUpdateDto))
                .isInstanceOf(APIException.class)
                .hasMessage(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
    }

}