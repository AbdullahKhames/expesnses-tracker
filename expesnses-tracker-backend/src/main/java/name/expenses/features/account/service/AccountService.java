package name.expenses.features.account.service;

import jakarta.ejb.Local;


import name.expenses.features.account.dtos.request.AccountReqDto;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface AccountService {
    ResponseDto create(AccountReqDto expense);
    ResponseDto get(String refNo);

    ResponseDto update(String refNo, AccountUpdateDto expense);

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto addAssociation(String accountRefNo, String pocketRefNo);

    ResponseDto removeAssociation(String accountRefNo, String pocketRefNo);
}