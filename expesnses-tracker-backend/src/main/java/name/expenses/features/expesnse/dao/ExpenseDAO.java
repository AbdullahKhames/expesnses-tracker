package name.expenses.features.expesnse.dao;

import jakarta.ejb.Local;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;

@Local
public interface ExpenseDAO {
    Expense createExpense(Expense expense);
    Expense getExpense(String refNo);
    Page<Expense> findAll(int pageNumber, int pageSize, String sortBy, SortDirection sortDirection);
    List<Expense> getAllExpenses();
    Expense updateExpense(Expense expense);
    String deleteExpense(String refNo);
}
