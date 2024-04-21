package name.expenses.features.account.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.account.dao.AccountDAO;
import name.expenses.features.account.dtos.request.AccountReqDto;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.dtos.response.AccountRespDto;
import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.account.models.Account;
import name.expenses.features.account.service.AccountService;
import name.expenses.features.association.AssociationResponse;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.features.association.Models;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;
import name.expenses.utils.account_association_manager.AccountAssociationManager;
import name.expenses.utils.account_association_manager.UpdateAccountServiceImpl;
import name.expenses.utils.collection_getter.AccountGetter;
import name.expenses.utils.collection_getter.ExpenseGetter;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
//@Transactional
public class AccountServiceImpl implements AccountService {
    public static final String ACCOUNT = "Account";
    private final AccountDAO accountDAO;
    private final AccountMapper accountMapper;
    private final AccountAssociationManager accountAssociationManager;
    private final UpdateAccountServiceImpl updateAccountService;
    private final PocketService pocketService;


    @Override
    public ResponseDto create(AccountReqDto account) {
        Account sentAccount = accountMapper.reqDtoToEntity(account);
        Account savedAccount = accountDAO.create(sentAccount);
        log.info("created account {}", savedAccount);
        return ResponseDtoBuilder.getCreateResponse(ACCOUNT, savedAccount.getRefNo(), accountMapper.entityToRespDto(savedAccount));
    }

    public Optional<Account> getEntity(String refNo){
        try {
            Optional<Account> accountOptional = accountDAO.get(refNo);
            log.info("fetched account {}", accountOptional);
            return accountOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<Account> accountOptional = getEntity(refNo);
            log.info("fetched account {}", accountOptional);
            if (accountOptional.isPresent()){
                Account account = accountOptional.get();
                return ResponseDtoBuilder.getFetchResponse(ACCOUNT, account.getRefNo(), accountMapper.entityToRespDto(account));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("account with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("account with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, AccountUpdateDto accountUpdateDto) {
        Optional<Account> accountOptional = getEntity(refNo);
        if (accountOptional.isPresent()){
            Account account = accountOptional.get();
            log.info("fetched account {}", account);
            accountMapper.update(account, accountUpdateDto);
//            updateAccountService.updateCategoryAssociations(account, accountUpdateDto);
            log.info("updated account {}", account);
            account.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(ACCOUNT, account.getRefNo(), accountMapper.entityToRespDto(accountDAO.update(account)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("account with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(ACCOUNT,accountDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        if (pageNumber < 1){
            pageNumber = 1L;
        }
        if (pageSize < 1)
        {
            pageSize = 1L;
        }
        Page<Account> accountPage = accountDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);
        Page<AccountRespDto> accountDtos = accountMapper.entityToRespDto(accountPage);
        return ResponseDtoBuilder.getFetchAllResponse(ACCOUNT, accountDtos);
    }

    @Override
    public Set<Account> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }
        return accountDAO.getEntities(refNos);
    }

    @Override
    public ResponseDto addAssociation(String accountRefNo, String pocketRefNo) {
        Optional<Account> accountOptional = getEntity(accountRefNo);
        if (accountOptional.isPresent()){
            Account account = accountOptional.get();
            if (accountAssociationManager.addAssociation(account, Models.POCKET, pocketRefNo)){
                return ResponseDtoBuilder.getUpdateResponse(ACCOUNT, accountRefNo, accountMapper.entityToRespDto(account));
            }
            return ResponseDtoBuilder.getErrorResponse(804, "something went wrong couldn't add");

        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("account with the ref number %s was not found", accountRefNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);
    }

    @Override
    public ResponseDto removeAssociation(String accountRefNo, String pocketRefNo) {
        Optional<Account> accountOptional = getEntity(accountRefNo);
        if (accountOptional.isPresent()){
            Account account = accountOptional.get();
            if (accountAssociationManager.removeAssociation(account, Models.POCKET, pocketRefNo)){
                return ResponseDtoBuilder.getUpdateResponse(ACCOUNT, accountRefNo, accountMapper.entityToRespDto(account));
            }
            return ResponseDtoBuilder.getErrorResponse(804, "something went wrong couldn't remove");
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("account with the ref number %s was not found", accountRefNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);
    }

    @Override
    public ResponseDto getAccountPOckets(String refNo) {
        Optional<Account> accountOptional = getEntity(refNo);
        if (accountOptional.isPresent()){
            Account account = accountOptional.get();
            return ResponseDtoBuilder.getFetchResponse(ACCOUNT, account.getRefNo(), pocketService.entityToRespDto(account.getPockets()));

        }else {
            return ResponseDtoBuilder.getErrorResponse(804, "no account found with given ref");
        }
    }

    @Override
    public ResponseDto getAccountByName(String name) {
        if (name == null || name.isBlank()) {
            return ResponseDtoBuilder.getErrorResponse(804, "name cannot be null");
        }
        List<Account> accounts = accountDAO.getByName(name);
        if (!accounts.isEmpty()){
            return ResponseDtoBuilder.getFetchAllResponse(ACCOUNT, accountMapper.entityToRespDto(accounts));
        }else {
            return ResponseDtoBuilder.getErrorResponse(804, "not found");
        }
    }

    @Override
    public boolean addAssociation(Customer entity, Models entityModel, String refNo) {
        return false;
    }



    @Override
    public ResponseDto addDtoAssociation(Object entity, Models entityModel, Set<?> associationUpdateDto) {
        return null;
    }

    @Override
    public boolean removeAssociation(Customer entity, Models entityModel, String refNo) {
        return false;
    }



    @Override
    public ResponseDto removeDtoAssociation(Object entity, Models entityModel, Set<?> associationsUpdateDto) {
        return null;
    }

    @Override
    public ResponseDto addAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, AccountGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        AccountGetter accountsGetter = (AccountGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Account> accountOptional = getEntity(refNo);
            if (accountOptional.isPresent()){
                Account account = accountOptional.get();
                if (accountsGetter.getAccounts() == null){
                    accountsGetter.setAccounts(new HashSet<>());
                }
                if (accountsGetter.getAccounts().contains(account)){
                    associationResponse.getError().put(refNo, "this accountsGetter already contain this account");
                }else {
                    accountsGetter.getAccounts().add(account);
                    associationResponse.getSuccess().put(refNo, "was added successfully");
                }
            }else {
                associationResponse.getError().put(refNo, "no account corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), accountsGetter.getRefNo(), associationResponse);
    }
    @Override
    public ResponseDto removeAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, AccountGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        AccountGetter accountsGetter = (AccountGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Account> accountOptional = getEntity(refNo);
            if (accountOptional.isPresent()){
                Account account = accountOptional.get();
                if (accountsGetter.getAccounts() == null){
                    accountsGetter.setAccounts(new HashSet<>());
                }
                if (accountsGetter.getAccounts().contains(account)){
                    accountsGetter.getAccounts().remove(accountOptional.get());
                    associationResponse.getSuccess().put(refNo, "was removed successfully");
                }else {
                    associationResponse.getError().put(refNo, "this accountsGetter doesn't contain this account");
                }
            }else {
                associationResponse.getError().put(refNo, "no account corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), accountsGetter.getRefNo(), associationResponse);
    }
}
