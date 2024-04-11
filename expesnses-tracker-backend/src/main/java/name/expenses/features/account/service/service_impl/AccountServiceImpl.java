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
import name.expenses.features.category.models.Category;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.association.CollectionAssociation;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.account_association_manager.AccountAssociationManager;
import name.expenses.utils.account_association_manager.UpdateAccountServiceImpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

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
            updateAccountService.updateCategoryAssociations(account, accountUpdateDto);
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
    public ResponseDto addAssociation(String accountRefNo, String pocketRefNo) {
        Optional<Account> accountOptional = getEntity(accountRefNo);
        if (accountOptional.isPresent()){
            Account account = accountOptional.get();
            if (accountAssociationManager.addAssociation(account, CollectionAssociation.POCKET, pocketRefNo)){
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
            if (accountAssociationManager.removeAssociation(account, CollectionAssociation.POCKET, pocketRefNo)){
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
}
