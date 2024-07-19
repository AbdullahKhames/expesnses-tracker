package name.expenses.features.budget.dao;

import jakarta.ejb.Local;
import name.expenses.features.budget.models.Budget;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface BudgetDAO {
    Budget create(Budget expense);
    Optional<Budget> get(String refNo);
    Page<Budget> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Budget> findAllWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    List<Budget> get();
    String delete(String refNo);

    Long checkAccountAssociation(Budget Budget);
    Set<Budget> getEntities(Set<String> refNos);

    Set<Budget> saveAll(Set<Budget> Budgets);
    Set<Budget> updateAll(Set<Budget> Budgets);
    Budget update(Budget expense);

    List<Budget> getByName(String name);
}