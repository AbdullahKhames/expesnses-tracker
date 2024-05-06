package name.expenses.utils;

import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.globals.PageReq;
import name.expenses.globals.responses.ResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidateInputUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validateWildCardSet_succeess() {
        Set<ExpenseReqDto> expenseReqDtos = new HashSet<>(Arrays.asList(new ExpenseReqDto(), new ExpenseReqDto()));
        ResponseDto responseDto = ValidateInputUtils.validateWildCardSet(expenseReqDtos, ExpenseReqDto.class);
        System.out.println(responseDto);
        assertNull(responseDto);
    }
    @Test
    void validateWildCardSet_failed() {
        Set<ExpenseUpdateDto> expenseReqDtos = new HashSet<>(Arrays.asList(new ExpenseUpdateDto(), new ExpenseUpdateDto()));
        ResponseDto responseDto = ValidateInputUtils.validateWildCardSet(expenseReqDtos, ExpenseReqDto.class);
        System.out.println(responseDto);
        assertNotNull(responseDto);
        assertFalse(responseDto.isStatus());

    }
    @Test
    void validateWildCardSet_rawSet() {
        Set expenseReqDtos = new HashSet<>(Arrays.asList(new ExpenseReqDto(), new ExpenseReqDto(),new ExpenseReqDto(),new ExpenseReqDto(),new ExpenseReqDto(),new ExpenseUpdateDto()));
        ResponseDto responseDto = ValidateInputUtils.validateWildCardSet(expenseReqDtos, ExpenseReqDto.class);
        System.out.println(responseDto);
        assertNotNull(responseDto);
        assertFalse(responseDto.isStatus());

    }

    @Test
    void validatePageData() {
        Long x = -5L;
        Long y = -10L;
        PageReq pageReq = ValidateInputUtils.validatePageData(x, y);

        System.out.println(x);
        System.out.println(y);
        System.out.println(pageReq.pageNumber());
        System.out.println(pageReq.pageSize());
    }
}