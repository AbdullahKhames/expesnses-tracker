package name.expenses.features.budget_transfer.dao;

import jakarta.ejb.Local;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface BudgetTransferDAO {
    BudgetTransfer create(BudgetTransfer expense);
    Optional<BudgetTransfer> get(String refNo);
    Page<BudgetTransfer> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<BudgetTransfer> get();
    BudgetTransfer update(BudgetTransfer expense);
    String delete(String refNo);
    Set<BudgetTransfer> getEntities(Set<String> refNos);
}