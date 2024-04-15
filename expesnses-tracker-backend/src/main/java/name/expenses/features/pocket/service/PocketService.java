package name.expenses.features.pocket.service;

import jakarta.ejb.Local;


import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;

import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.CrudService;
import name.expenses.globals.SortDirection;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.association.UpdateAssociation;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface PocketService extends
        CollectionAdder<Account>,
        CollectionRemover <Account>,
//        CollectionAdder<Account, PocketUpdateDto>,
//        CollectionRemover <Account, PocketUpdateDto>,
        UpdateAssociation<Account, AccountUpdateDto>,
        CrudService<PocketReqDto, PocketUpdateDto, String, Pocket> {
    ResponseDto create(PocketReqDto expense);
    ResponseDto get(String refNo);

    ResponseDto update(String refNo, PocketUpdateDto expense);

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

}