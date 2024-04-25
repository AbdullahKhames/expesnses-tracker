package name.expenses.repostitories;



import name.expenses.models.*;


import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    Page<Expense> getAllCustomerSubCategoryExpenses(Long currentCustomerId, String subCategoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<SubCategory> getAllCustomerCategorySubCategories(Long currentCustomerId, String categoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Pocket> getAllCustomerAccountPockets(Long currentCustomerId, String accountRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
}