package name.expenses.features.expesnse.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ejb.Local;
import jakarta.ws.rs.core.Response;
import name.expenses.features.expesnse.models.Expense;



@Local
public interface ExpenseService {
    public Response createExpense(Expense expense) throws JsonProcessingException;
}
