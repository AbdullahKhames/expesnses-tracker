package name.expenses.features.customer.dao;

import jakarta.ejb.Local;
import name.expenses.features.account.models.Account;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.transaction.models.Transaction;
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

    Page<Account> getAllCustomerAccounts(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Category> getAllCustomerCategories(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Expense> getAllCustomerExpenses(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<SubCategory> getAllCustomerSubCategories(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Pocket> getAllCustomerPockets(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<PocketTransfer> getAllCustomerPocketTransfers(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Transaction> getAllCustomerTransactions(Long customerId,Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
}