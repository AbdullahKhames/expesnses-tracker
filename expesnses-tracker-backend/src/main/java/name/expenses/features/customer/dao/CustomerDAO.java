package name.expenses.features.customer.dao;

import jakarta.ejb.Local;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface CustomerDAO {
    Customer create(Customer expense);
    Optional<Customer> get(String refNo);
    Optional<Customer> get(Long id);
    Page<Customer> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Customer> get();
    Customer update(Customer expense);
    String delete(String refNo);

    Long checkCustomerAssociation(Customer pocket);

    Set<Customer> getEntities(Set<String> refNos);

    boolean existByPocket(Pocket pocket);
    boolean existByExpense(Expense expense);
}