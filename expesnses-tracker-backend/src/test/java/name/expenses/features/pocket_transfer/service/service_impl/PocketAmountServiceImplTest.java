package name.expenses.features.pocket_transfer.service.service_impl;

import name.expenses.Catalog;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.pocket.mappers.PocketMapper;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.pocket_transfer.dao.PocketAmountDAO;
import name.expenses.features.pocket_transfer.dtos.request.PocketAmountUpdateDto;
import name.expenses.features.pocket_transfer.mappers.PocketAmountMapper;
import name.expenses.features.pocket_transfer.models.AmountType;
import name.expenses.features.pocket_transfer.models.PocketAmount;
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
class PocketAmountServiceImplTest {
    @InjectMocks
    private  PocketAmountMapper pocketAmountMapper = spy(PocketAmountMapper.class);
    @InjectMocks
    private  PocketMapper pocketMapper = spy(PocketMapper.class);
    @InjectMocks
    private AccountMapper accountMapper = spy(AccountMapper.class);
    @Mock
    private  PocketService pocketService;
    @Mock
    private  PocketAmountDAO pocketAmountDAO;

    @InjectMocks
    PocketAmountServiceImpl pocketAmountService;
    PocketAmountUpdateDto pocketAmountUpdateDto;
    PocketAmount pocketAmount1;
    Pocket pocket1;
    Pocket pocket2;
    Catalog catalog = new Catalog();
    @BeforeEach
    void setUp() {
        pocketAmount1 = catalog.getObject(PocketAmount.class, 1);
        pocket1 = catalog.getObject(Pocket.class, 1);
        pocket2 = catalog.getObject(Pocket.class, 2);
        pocketAmountUpdateDto = PocketAmountUpdateDto.builder()
                .amount(100.0)
                .refNo(pocketAmount1.getRefNo())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateAssociationSamePocketCredit() {
        pocketAmountUpdateDto.setPocketRefNo(pocket1.getRefNo());
        pocketAmount1.setAmountType(AmountType.CREDIT);

        when(pocketService.getEntity(pocketAmountUpdateDto.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket1));

        pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketAmountMapper, times(1)).update(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketService,times(1)).update(pocket1);
        assertThat(pocket1.getAmount()).isEqualTo(550);
        assertThat(pocket2.getAmount()).isEqualTo(150);

    }
    @Test
    void updateAssociationSamePocketDEBIT() {
        pocketAmountUpdateDto.setPocketRefNo(pocket1.getRefNo());

        when(pocketService.getEntity(pocketAmountUpdateDto.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket1));

        pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketAmountMapper, times(1)).update(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketService,times(1)).update(pocket1);
        assertThat(pocket1.getAmount()).isEqualTo(450);
        assertThat(pocket2.getAmount()).isEqualTo(150);
    }
    @Test
    void updateAssociationDiffPocketCredit() {
        pocketAmountUpdateDto.setPocketRefNo(pocket2.getRefNo());
        pocketAmount1.setAmountType(AmountType.CREDIT);
        doAnswer((context) -> {
            PocketAmount pockAmt = context.getArgument(0);
            PocketAmountUpdateDto pockAmtDto = context.getArgument(1);
            pockAmt.setAmount(pockAmtDto.getAmount());
            return pockAmt;
        }).when(pocketAmountMapper).update(pocketAmount1, pocketAmountUpdateDto);
        when(pocketService.getEntity(pocketAmountUpdateDto.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket2));

        pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketAmountMapper, times(1)).update(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketService,times(1)).update(pocket1);
        assertThat(pocket1.getAmount()).isEqualTo(450);
        assertThat(pocket2.getAmount()).isEqualTo(250);


    }
    @Test
    void updateAssociationDiffPocketDEBIT() {
        pocketAmountUpdateDto.setPocketRefNo(pocket2.getRefNo());

        when(pocketService.getEntity(pocketAmountUpdateDto.getPocketRefNo())).thenReturn(Optional.ofNullable(pocket2));

        doAnswer((context) -> {
            PocketAmount pockAmt = context.getArgument(0);
            PocketAmountUpdateDto pockAmtDto = context.getArgument(1);
            pockAmt.setAmount(pockAmtDto.getAmount());
            return pockAmt;
        }).when(pocketAmountMapper).update(pocketAmount1, pocketAmountUpdateDto);
        pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketAmountMapper, times(1)).update(pocketAmount1, pocketAmountUpdateDto);
        verify(pocketService,times(1)).update(pocket1);
        assertThat(pocket1.getAmount()).isEqualTo(550);
        assertThat(pocket2.getAmount()).isEqualTo(50);

    }
    @Test
    void updateAssociationNoRefNo() {
        pocketAmountUpdateDto.setRefNo(null);
        assertThatThrownBy(() -> pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto))
                .isInstanceOf(APIException.class)
                .hasMessage(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
    }
    @Test
    void updateAssociationDiffRefNo() {
        pocketAmountUpdateDto.setRefNo("1234");
        assertThatThrownBy(() -> pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto))
                .isInstanceOf(APIException.class)
                .hasMessage(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
    }

    @Test
    void updateAssociationEmptyOptional() {
        pocketAmountUpdateDto.setPocketRefNo(pocket1.getRefNo());

        when(pocketService.getEntity(pocketAmountUpdateDto.getPocketRefNo())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pocketAmountService.updateAssociation(pocketAmount1, pocketAmountUpdateDto))
                .isInstanceOf(APIException.class)
                .hasMessage(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
    }

}