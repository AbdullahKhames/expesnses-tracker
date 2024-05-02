package name.expenses.features.account.service;

import jakarta.ejb.Local;


import name.expenses.features.account.dtos.request.AccountReqDto;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.CrudService;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface AccountService extends CrudService<AccountReqDto, AccountUpdateDto, String, Account>,
//        CollectionRemover<Customer, AccountUpdateDto>,
//        CollectionAdder<Customer, AccountUpdateDto>,
        CollectionRemover<Customer>,
        CollectionAdder<Customer> {
    ResponseDto addAssociation(String accountRefNo, String pocketRefNo);

    ResponseDto removeAssociation(String accountRefNo, String pocketRefNo);

    ResponseDto getAccountPOckets(String refNo);

    ResponseDto getAccountByName(String name);

    Account getDefaultAccount();
}