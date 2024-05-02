package name.expenses.features.account.dao;

import jakarta.ejb.Local;
import name.expenses.features.account.models.Account;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface AccountDAO {
    Account create(Account expense);
    Optional<Account> get(String refNo);
    Page<Account> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Account> get();
    Account update(Account expense);
    String delete(String refNo);
    Set<Account> getEntities(Set<String> refNos);

    List<Account> getByName(String name);

    void refresh(Customer entity);

    Account getDefaultAccount();
}