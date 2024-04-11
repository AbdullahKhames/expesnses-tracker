package name.expenses.features.expesnse.service.service_impl;

import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.responses.ResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ExpenseServiceStatelessmplTest {

    @Mock
    private ExpenseDAO expenseDAO;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceStatelessmpl expenseService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSomeMethod() {
        // Arrange
        ExpenseReqDto expenseReqDto = new ExpenseReqDto();
        // Set up your ExpenseReqDto object as needed

        Expense expense = new Expense();
        // Set up your Expense object as needed

        when(expenseMapper.reqDtoToEntity(expenseReqDto)).thenReturn(expense);
        when(expenseDAO.createExpense(expense)).thenReturn(expense);

        // Act
        ResponseDto result = expenseService.createExpense(expenseReqDto);

        // Assert
        verify(expenseMapper, times(1)).reqDtoToEntity(expenseReqDto);
        verify(expenseDAO, times(1)).createExpense(expense);
        // Add more assertions as needed
    }
    @Test
    public void testUpdateExpensesAssociation() {
        // Arrange
        SubCategory existingSubCategory = new SubCategory();
        existingSubCategory.setExpenses(new HashSet<>());

        SubCategoryUpdateDto newSubCategory = new SubCategoryUpdateDto();
        newSubCategory.setExpenses(new HashSet<>());

        ExpenseUpdateDto newExpense = new ExpenseUpdateDto();
        newExpense.setRefNo("refNo");
        newSubCategory.getExpenses().add(newExpense);

        Expense expense = new Expense();
        when(expenseMapper.reqEntityToEntity(newExpense)).thenReturn(expense);

        // Act
        expenseService.updateExpensesAssociation(existingSubCategory, newSubCategory);

        // Assert
        verify(expenseMapper, times(1)).reqEntityToEntity(newExpense);
    }

    @Test
    public void testRetainAll (){
        Set<Integer> first = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> second = new HashSet<>(Arrays.asList(3, 4, 5, 6, 7));
        boolean retainAll = first.retainAll(second);
        first.addAll(second);
        System.out.println(retainAll);
        System.out.println(first);
        System.out.println(second);

    }
}