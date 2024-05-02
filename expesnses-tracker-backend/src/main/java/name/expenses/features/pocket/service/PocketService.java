package name.expenses.features.pocket.service;

import jakarta.ejb.Local;


import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;

import name.expenses.features.customer.models.Customer;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.dtos.response.PocketRespDto;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.CrudService;
import name.expenses.globals.SortDirection;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.association.UpdateAssociation;
import name.expenses.globals.responses.ResponseDto;

import java.util.Set;

@Local
public interface PocketService extends
        CollectionAdder<Account>,
        CollectionRemover <Account>,
//        CollectionAdder<Account, PocketUpdateDto>,
//        CollectionRemover <Account, PocketUpdateDto>,
        UpdateAssociation<Account, AccountUpdateDto>,
        CrudService<PocketReqDto, PocketUpdateDto, String, Pocket> {
    ResponseDto create(PocketReqDto expense);

    void associateAccount(String accountRefNo, Pocket sentPocket, Customer customer);

    ResponseDto get(String refNo);

    ResponseDto update(String refNo, PocketUpdateDto expense);

    Pocket createDefaultPocket();

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Set<Pocket> updateAll(Set<Pocket> pockets);
    ResponseDto getAllEntitiesWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Set<PocketRespDto> entityToRespDto(Set<Pocket> pockets);

    ResponseDto getPocketByName(String name);

    Pocket update(Pocket oldPocket);
}