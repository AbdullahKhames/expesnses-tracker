package name.expenses.features.expesnse.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ejb.Local;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;


@Local
public interface ExpenseService {
    ResponseDto createExpense(ExpenseReqDto expense) throws JsonProcessingException;
    ResponseDto getExpense(String refNo);

    ResponseDto updateExpense(String refNo, ExpenseUpdateDto expense) throws JsonProcessingException;

    ResponseDto deleteExpense(String refNo) throws JsonProcessingException;

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    void updateExpensesAssociation(SubCategory existingSubCategory, SubCategoryUpdateDto newSubCategory);
}
