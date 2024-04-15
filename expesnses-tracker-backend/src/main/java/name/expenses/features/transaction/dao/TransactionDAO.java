package name.expenses.features.transaction.dao;

import jakarta.ejb.Local;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface TransactionDAO {
    Transaction create(Transaction expense);
    Optional<Transaction> get(String refNo);
    Page<Transaction> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Transaction> get();
    Transaction update(Transaction expense);
    String delete(String refNo);
    Set<Transaction> getEntities(Set<String> refNos);
}