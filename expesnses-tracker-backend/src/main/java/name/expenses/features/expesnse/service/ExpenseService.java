package name.expenses.features.expesnse.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ejb.Local;
import jakarta.ws.rs.core.Response;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

import java.util.List;


@Local
public interface ExpenseService {
    ResponseDto createExpense(ExpenseReqDto expense) throws JsonProcessingException;
    ResponseDto getExpense(String refNo);

    ResponseDto updateExpense(String refNo, ExpenseUpdateDto expense) throws JsonProcessingException;

    ResponseDto deleteExpense(String refNo) throws JsonProcessingException;

    ResponseDto getAllEntities(int pageNumber, int pageSize, String sortBy, SortDirection sortDirection);

}
