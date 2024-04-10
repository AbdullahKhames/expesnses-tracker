package name.expenses.features.expesnse.dao;

import jakarta.ejb.Local;
import name.expenses.features.expesnse.models.Expense;

import java.util.List;

@Local
public interface ExpenseDAO {
    Expense createExpense(Expense expense);
    Expense getExpense(Long id);
    List<Expense> getAllExpenses();
    Expense updateExpense(Expense expense);
    void deleteExpense(Long id);
}
