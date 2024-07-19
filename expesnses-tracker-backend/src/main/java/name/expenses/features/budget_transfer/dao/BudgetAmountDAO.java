package name.expenses.features.budget_transfer.dao;

import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BudgetAmountDAO {
    BudgetAmount create(BudgetAmount expense);
    Optional<BudgetAmount> get(String refNo);
    Page<BudgetAmount> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<BudgetAmount> get();
    BudgetAmount update(BudgetAmount expense);
    String delete(String refNo);
    Set<BudgetAmount> getEntities(Set<String> refNos);
}
