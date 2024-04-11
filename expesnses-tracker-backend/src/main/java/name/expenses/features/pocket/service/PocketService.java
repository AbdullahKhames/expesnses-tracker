package name.expenses.features.pocket.service;

import jakarta.ejb.Local;


import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.association.CollectionAdder;
import name.expenses.globals.association.CollectionRemover;
import name.expenses.globals.association.UpdateAssociation;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface PocketService extends CollectionAdder<Account>, CollectionRemover <Account>, UpdateAssociation<Account, AccountUpdateDto> {
    ResponseDto create(PocketReqDto expense);
    ResponseDto get(String refNo);

    ResponseDto update(String refNo, PocketUpdateDto expense);

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

}