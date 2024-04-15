package name.expenses.features.expesnse.dao;

import jakarta.ejb.Local;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface ExpenseDAO {
    Expense createExpense(Expense expense);
    Optional<Expense> getExpense(String refNo);
    Page<Expense> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Expense> getAllExpenses();
    Expense updateExpense(Expense expense);
    String deleteExpense(String refNo);
    Set<Expense> getEntities(Set<String> refNos);

    Set<Expense> saveAll(Set<Expense> expenses);
}
