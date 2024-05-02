package name.expenses.features.account.service.service_impl;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.association.UpdateAssociation;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class UpdateAccountServiceImpl {
    private final PocketService pocketService;


    private final List<UpdateAssociation<Account, AccountUpdateDto>> updateProductAssociations = new ArrayList<>(4);
    @PostConstruct
    private void init(){
        updateProductAssociations.add(pocketService);

    }
    public void updateCategoryAssociations(Account account, AccountUpdateDto accountUpdateDto){
        updateProductAssociations
                .forEach(updateProductAssociation ->
                        updateProductAssociation.updateAssociation(account, accountUpdateDto));
    }

}