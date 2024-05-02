package name.expenses.services.transaction;


import name.expenses.models.Page;
import name.expenses.models.SortDirection;
import name.expenses.models.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TransactionDAO {
    Transaction create(Transaction expense);
    Optional<Transaction> get(String refNo);
    Page<Transaction> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Transaction> get();
    Transaction update(Transaction expense);
    String delete(String refNo);
    Set<Transaction> getEntities(Set<String> refNos);
}