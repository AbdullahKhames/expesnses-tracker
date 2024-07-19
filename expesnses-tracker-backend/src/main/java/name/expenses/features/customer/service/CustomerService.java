package name.expenses.features.customer.service;

import jakarta.ejb.Local;

import name.expenses.features.association.Models;
import name.expenses.features.customer.dtos.request.CustomerReqDto;
import name.expenses.features.customer.dtos.request.CustomerUpdateDto;
import name.expenses.features.customer.dtos.response.CustomerRespDto;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.CrudService;
import name.expenses.features.association.UpdateAssociation;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface CustomerService extends
//        CollectionAdder<Customer>,
//        CollectionRemover <Customer>,
        UpdateAssociation<Customer, CustomerUpdateDto>,
        CrudService<CustomerReqDto, CustomerUpdateDto, String, Customer> {
    Customer update(Customer customer);

    ResponseDto getCustomerAssociation(Models models);

    CustomerRespDto create(Customer customer);

    ResponseDto getAllCustomerExpenses(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerSubCategories(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerBudgets(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerCategories(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerAccounts(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerBudgetTransfers(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerTransactions(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerSubCategoryExpenses(String subCategoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerCategorySubCategories(String categoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getAllCustomerAccountBudgets(String accountRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
}