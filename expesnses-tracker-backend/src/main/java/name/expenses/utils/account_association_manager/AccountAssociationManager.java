package name.expenses.utils.account_association_manager;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.account.models.Account;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.globals.association.CollectionAdder;
import name.expenses.globals.association.CollectionAssociation;
import name.expenses.globals.association.CollectionRemover;


import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AccountAssociationManager {
    private final PocketService pocketService;


    private final Map<CollectionAssociation, CollectionAdder<Account>> adderHandler = new HashMap<>(5);
    private final Map<CollectionAssociation, CollectionRemover<Account>> removerHandler = new HashMap<>(5);

    @PostConstruct
    private void init(){
        adderHandler.put(CollectionAssociation.POCKET, pocketService);


        removerHandler.put(CollectionAssociation.POCKET, pocketService);


    }
    public boolean addAssociation(Account category,
                                  CollectionAssociation AccountCollectionAssociation,
                                  String refNo) {

        CollectionAdder<Account> AccountCollectionAdder = adderHandler.get(AccountCollectionAssociation);
        if (AccountCollectionAdder == null) {
            return false;
        }
        return AccountCollectionAdder.addAssociation(category, refNo);
    }


    public boolean removeAssociation(Account category,
                                     CollectionAssociation AccountCollectionAssociation,
                                     String refNo) {

        CollectionRemover<Account> AccountCollectionRemover = removerHandler.get(AccountCollectionAssociation);
        if (AccountCollectionRemover == null) {
            return false;
        }
        return AccountCollectionRemover.removeAssociation(category, refNo);
    }
}