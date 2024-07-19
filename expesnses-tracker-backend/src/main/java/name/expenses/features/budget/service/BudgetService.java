package name.expenses.features.budget.service;

import jakarta.ejb.Local;


import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;

import name.expenses.features.budget.dtos.request.BudgetReqDto;
import name.expenses.features.budget.dtos.request.BudgetUpdateDto;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.CrudService;
import name.expenses.globals.SortDirection;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.association.UpdateAssociation;
import name.expenses.globals.responses.ResponseDto;

import java.util.Set;

@Local
public interface BudgetService extends
        CollectionAdder<Account>,
        CollectionRemover <Account>,
//        CollectionAdder<Account, BudgetUpdateDto>,
//        CollectionRemover <Account, BudgetUpdateDto>,
        UpdateAssociation<Account, AccountUpdateDto>,
        CrudService<BudgetReqDto, BudgetUpdateDto, String, Budget> {
    ResponseDto create(BudgetReqDto expense);

    void associateAccount(String accountRefNo, Budget sentBudget, Customer customer);

    ResponseDto get(String refNo);

    ResponseDto update(String refNo, BudgetUpdateDto expense);

    Budget createDefaultBudget();

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Set<Budget> updateAll(Set<Budget> Budgets);
    ResponseDto getAllEntitiesWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Set<BudgetRespDto> entityToRespDto(Set<Budget> Budgets);

    ResponseDto getBudgetByName(String name);

    Budget update(Budget oldBudget);
}