package name.expenses.features.account.service.service_impl;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.account.models.Account;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.Models;
import name.expenses.features.association.CollectionRemover;


import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AccountAssociationManager {
    private final PocketService pocketService;


    private final Map<Models, CollectionAdder<Account>> adderHandler = new HashMap<>(5);
    private final Map<Models, CollectionRemover<Account>> removerHandler = new HashMap<>(5);
//    private final Map<Models, CollectionAdder<Account, PocketUpdateDto>> adderHandler = new HashMap<>(5);
//    private final Map<Models, CollectionRemover<Account, PocketUpdateDto>> removerHandler = new HashMap<>(5);

    @PostConstruct
    private void init(){
        adderHandler.put(Models.POCKET, pocketService);


        removerHandler.put(Models.POCKET, pocketService);


    }
    public <T> boolean addAssociation(Account account,
                                      Models accountModels,
                                      T refNo) {

        CollectionAdder<Account> AccountCollectionAdder = adderHandler.get(accountModels);
//        CollectionAdder<Account, PocketUpdateDto> AccountCollectionAdder = adderHandler.get(accountModels);
        if (AccountCollectionAdder == null) {
            return false;
        }
        if (refNo instanceof String){
            return AccountCollectionAdder.addAssociation(account, Models.ACCOUNT, (String) refNo);
        }
        return false;
    }


    public boolean removeAssociation(Account account,
                                     Models accountModels,
                                     Object refNo) {

//        CollectionRemover<Account, PocketUpdateDto> AccountCollectionRemover = removerHandler.get(accountModels);
        CollectionRemover<Account> AccountCollectionRemover = removerHandler.get(accountModels);
        if (AccountCollectionRemover == null) {
            return false;
        }
//        return AccountCollectionRemover.removeAssociation(account, Models.ACCOUNT, refNo);
        return false;

    }
}