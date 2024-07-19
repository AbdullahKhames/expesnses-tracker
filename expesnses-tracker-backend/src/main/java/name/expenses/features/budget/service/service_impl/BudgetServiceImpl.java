package name.expenses.features.budget.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.account.dao.AccountDAO;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;
import name.expenses.features.association.AssociationResponse;
import name.expenses.features.association.Models;
import name.expenses.features.budget.dao.BudgetDAO;
import name.expenses.features.budget.dtos.request.BudgetReqDto;
import name.expenses.features.budget.dtos.request.BudgetUpdateDto;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.features.budget.mappers.BudgetMapper;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.models.BudgetType;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.models.Customer;


import name.expenses.globals.Page;
import name.expenses.globals.PageReq;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;
import name.expenses.utils.collection_getter.BudgetGetter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class BudgetServiceImpl implements BudgetService {
    public static final String BUDGET = "Budget";
    private final BudgetDAO budgetDAO;
    private final BudgetMapper budgetMapper;
    private final AccountDAO accountDAO;
    private final CustomerDAO customerDAO;



    @Override
    public ResponseDto create(BudgetReqDto BudgetReqDto) {
        try {
            Budget sentBudget = budgetMapper.reqDtoToEntity(BudgetReqDto);
//            Budget savedBudget = budgetDAO.create(sentBudget);
            Optional<Customer> customerOptional = customerDAO.get(BudgetReqDto.getCustomerId());
            Customer customer;
            if (customerOptional.isEmpty()){
                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                        Map.of("error", "customer not found"));
            }else {
                customer = customerOptional.get();
                if (customer.getBudgets() == null) {
                    customer.setBudgets(new HashSet<>());
                }
                customer.getBudgets().add(sentBudget);
                sentBudget.setCustomer(customer);
//                customerDAO.update(customer);
            }
//            if (savedBudget == null || savedBudget.getId() == null){
//                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
//                        Map.of("error", "savedBudget not found"));
//            }
            associateAccount(BudgetReqDto.getAccountRefNo(), sentBudget, customer);
            Budget savedBudget = budgetDAO.create(sentBudget);
            log.info("created Budget {}", savedBudget);
            customerDAO.update(customer);
            return ResponseDtoBuilder.getCreateResponse(BUDGET, savedBudget.getRefNo(), budgetMapper.entityToRespDto(savedBudget));
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void associateAccount(String accountRefNo, Budget sentBudget, Customer customer) {
        Optional<Account> accountOptional = accountDAO.get(accountRefNo);
        if (accountOptional.isEmpty()){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "account not found"));
        }else {
            Account account = accountOptional.get();
            if (account.getBudgets() == null) {
                account.setBudgets(new HashSet<>());
            }
            account.getBudgets().add(sentBudget);
            sentBudget.setAccount(account);
            if (customer != null) {
                if (customer.getAccounts() == null){
                    customer.setAccounts(new HashSet<>());
                }
                customer.getAccounts().add(account);
            }
//                accountDAO.update(account);
        }
    }

    public Optional<Budget> getEntity(String refNo){
        try {
            Optional<Budget> BudgetOptional = budgetDAO.get(refNo);
            log.info("fetched Budget {}", BudgetOptional);
            return BudgetOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<Budget> BudgetOptional = getEntity(refNo);
            log.info("fetched Budget {}", BudgetOptional);
            if (BudgetOptional.isPresent()){
                Budget Budget = BudgetOptional.get();
                return ResponseDtoBuilder.getFetchResponse(BUDGET, Budget.getRefNo(), budgetMapper.entityToRespDto(Budget));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("Budget with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("Budget with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, BudgetUpdateDto budgetUpdateDto) {
        Optional<Budget> BudgetOptional = getEntity(refNo);
        if (BudgetOptional.isPresent()){
            Budget budget = BudgetOptional.get();
            log.info("fetched Budget {}", budget);
            budgetMapper.update(budget, budgetUpdateDto);
            log.info("updated Budget {}", budget);
            budget.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(BUDGET, budget.getRefNo(), budgetMapper.entityToRespDto(budgetDAO.update(budget)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("Budget with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }
    @Override
    public Budget createDefaultBudget(){
        Budget Budget = new Budget();
        Budget.setName("default Budget");
        Budget.setDetails("default customer Budget");
        Budget.setBudgetType(BudgetType.DEFAULT);
        Budget.setDefaultSender(true);
        Budget.setDefaultReceiver(true);
        return Budget;
    }
    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(BUDGET,budgetDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Budget> BudgetPage = budgetDAO.findAll(pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<BudgetRespDto> BudgetDtos = budgetMapper.entityToRespDto(BudgetPage);
        return ResponseDtoBuilder.getFetchAllResponse(BUDGET, BudgetDtos);
    }

    @Override
    public Set<Budget> updateAll(Set<Budget> Budgets) {
        return budgetDAO.updateAll(Budgets);
    }

    @Override
    public ResponseDto getAllEntitiesWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Budget> BudgetPage = budgetDAO.findAllWithoutAccount(pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<BudgetRespDto> BudgetDtos = budgetMapper.entityToRespDto(BudgetPage);
        return ResponseDtoBuilder.getFetchAllResponse(BUDGET, BudgetDtos);
    }

    @Override
    public Set<BudgetRespDto> entityToRespDto(Set<Budget> Budgets) {
        return budgetMapper.entityToRespDto(Budgets);
    }

    @Override
    public ResponseDto getBudgetByName(String name) {
        if (name == null || name.isBlank()) {
            return ResponseDtoBuilder.getErrorResponse(804, "name cannot be null");
        }
        List<Budget> Budgets = budgetDAO.getByName(name);
        if (!Budgets.isEmpty()){
            return ResponseDtoBuilder.getFetchAllResponse(BUDGET, budgetMapper.entityToRespDto(
                    PageUtil.createPage(1L, (long) Budgets.size(), Budgets, Budgets.size())
            ));
        }else {
            return ResponseDtoBuilder.getErrorResponse(804, "not found");
        }
    }

    @Override
    public Budget update(Budget oldBudget) {
        return budgetDAO.update(oldBudget);
    }

    @Override
    public Set<Budget> getEntities(Set<String> refNos) {
        return budgetDAO.getEntities(refNos);
    }

    @Override
    public boolean addAssociation(Account entity, Models entityModel, String refNo) {
        if (ValidateInputUtils.isValidInput(entity, entity.getBudgets())) {
            Optional<Budget> BudgetOptional = getEntity(refNo);
            if (BudgetOptional.isEmpty()) {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("the Budget with given reference number : %s doesn't exist", refNo)));
            }
            Budget Budget = BudgetOptional.get();
            Long accountId = budgetDAO.checkAccountAssociation(Budget);
            if (accountId != null) {
                if (Objects.equals(accountId, entity.getId())) {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this Budget is already present in the given account!!"));
                }else {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this Budget is already present in another account!!"));
                }
            }

            entity.getBudgets().add(BudgetOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public ResponseDto addAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, BudgetGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        BudgetGetter BudgetGetter = (BudgetGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Budget> BudgetOptional = getEntity(refNo);
            if (BudgetOptional.isPresent()){
                Budget Budget = BudgetOptional.get();
                if (BudgetGetter.getBudgets() == null){
                    BudgetGetter.setBudgets(new HashSet<>());
                }
                if (BudgetGetter.getBudgets().contains(Budget)){
                    associationResponse.getError().put(refNo, "this BudgetGetter already contain this Budget");
                } else if (entityModel == Models.CUSTOMER && customerDAO.existByBudget(Budget)) {
                    associationResponse.getError().put(refNo, "this Budget already present on another customer");
                } else {
                    BudgetGetter.getBudgets().add(BudgetOptional.get());
                    associationResponse.getSuccess().put(refNo, "was added successfully");
                }
            }else {
                associationResponse.getError().put(refNo, "no Budget corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), BudgetGetter.getRefNo(), associationResponse);
    }

    @Override
    public ResponseDto addDtoAssociation(Object entity, Models entityModel, Set<?> associationReqDto) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, BudgetGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        ResponseDto errorResponse = ValidateInputUtils.validateWildCardSet(associationReqDto, BudgetReqDto.class);
        if (errorResponse != null) {
            return errorResponse;
        }
        BudgetGetter BudgetGetter = (BudgetGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        try {
            Set<Budget> Budgets = associationReqDto.stream()
                    .map(reqDto ->{
                        BudgetReqDto BudgetReqDto = (BudgetReqDto) reqDto;
                        Budget Budget = budgetMapper.reqDtoToEntity(BudgetReqDto);
//                        BudgetGetter.getBudgets().add(Budget);
                        if (entityModel == Models.CUSTOMER){
                            Customer customer = (Customer) BudgetGetter;
                            Budget.setCustomer(customer);
                            associateAccount(((BudgetReqDto) reqDto).getAccountRefNo(), Budget, customer);
                            try {
                                customerDAO.update(customer);
                            }catch (Exception ex){
                                log.error("exception occured {}", ex.getMessage());
                            }
                        }else {
                            associateAccount(((BudgetReqDto) reqDto).getAccountRefNo(), Budget, null);
                        }
                        return Budget;
                    })
                    .collect(Collectors.toSet());
            BudgetGetter.getBudgets().addAll(Budgets);
            budgetDAO.saveAll(Budgets);
        }catch (Exception ex){
            throw new IllegalArgumentException(ex);
        }

        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), BudgetGetter.getRefNo(), associationResponse);
    }

//    @Override
//    public ResponseDto addAssociation(Account entity, Set<BudgetUpdateDto> associationsUpdateDto) {
//        return null;
//    }

    @Override
    public boolean removeAssociation(Account entity, Models entityModel, String refNo) {
        if (ValidateInputUtils.isValidInput(entity, entity.getBudgets())) {
            Optional<Budget> BudgetOptional = getEntity(refNo);
            if (BudgetOptional.isEmpty()) {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("the subBudget with given reference number : %s doesn't exist", refNo)));
            }
            Budget Budget = BudgetOptional.get();
            Long accountId = budgetDAO.checkAccountAssociation(Budget);
            if (accountId != null) {
                if (Objects.equals(accountId, entity.getId())) {
                    entity.getBudgets().remove(Budget);
                }else {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this Budget is already present in another account!!"));
                }
            }else {
                throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                        Map.of("error", "this Budget is bot present in any account!!"));
            }
            return true;
        }
        return false;
    }

    @Override
    public ResponseDto removeAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, BudgetGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        BudgetGetter BudgetGetter = (BudgetGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Budget> BudgetOptional = getEntity(refNo);
            if (BudgetOptional.isPresent()){
                Budget Budget = BudgetOptional.get();
                if (BudgetGetter.getBudgets() == null){
                    BudgetGetter.setBudgets(new HashSet<>());
                }
                if (BudgetGetter.getBudgets().contains(Budget)){
                    BudgetGetter.getBudgets().remove(BudgetOptional.get());
                    associationResponse.getSuccess().put(refNo, "was removed successfully");
                }else {
                    associationResponse.getError().put(refNo, "this BudgetGetter doesn't contain this Budget");
                }
            }else {
                associationResponse.getError().put(refNo, "no Budget corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), BudgetGetter.getRefNo(), associationResponse);
    }

    @Override
    public ResponseDto removeDtoAssociation(Object entity, Models entityModel, Set<?> associationsUpdateDto) {
        ResponseDto errorResponse = ValidateInputUtils.validateWildCardSet(associationsUpdateDto, BudgetUpdateDto.class);
        if (errorResponse != null) {
            return errorResponse;
        }
        try {
            Set<Budget> Budgets = new HashSet<>();
            for (Object obj : associationsUpdateDto) {
                BudgetUpdateDto BudgetUpdateDto = (BudgetUpdateDto) obj;
                Optional<Budget> BudgetOptional = getEntity(BudgetUpdateDto.getRefNo());
                if (BudgetOptional.isPresent())
                    Budgets.add(BudgetOptional.get());
                else {
                    Budgets.add(budgetMapper.reqEntityToEntity(BudgetUpdateDto));
                }
            }
        }catch (Exception ex){
            return ResponseDtoBuilder.getErrorResponse(810, "error processing your request");
        }
        return null;
    }


//    @Override
//    public ResponseDto removeAssociation(Account entity, Set<BudgetUpdateDto> associationsUpdateDto) {
//        return null;
//    }

    @Override
    public void updateAssociation(Account entity, AccountUpdateDto entityUpdateDto) {
        if (ValidateInputUtils.isValidInput(entity.getBudgets(), entityUpdateDto.getBudgets())) {
            Set<Budget> existingBudgets = entity.getBudgets();
            Set<BudgetUpdateDto> budgetUpdateDtos = entityUpdateDto.getBudgets();
            //new Budget will contain only the new Budget ie the one not included with refs
            Set<Budget> newBudgets = getBudgets(budgetUpdateDtos, existingBudgets);
            //remove non existent in the new collection
            Set<Budget> removedBudgets = new HashSet<>(existingBudgets);
            List<String> newListRefs = budgetUpdateDtos.stream().map(BudgetUpdateDto::getRefNo).toList();
            removedBudgets.removeIf(Budget -> newListRefs.contains(Budget.getRefNo()));
            entity.getBudgets().removeAll(removedBudgets);
            entity.getBudgets().addAll(newBudgets);
        }
    }
    private Set<Budget> getBudgets (Set<BudgetUpdateDto> BudgetUpdateDtos, Set < Budget > existingBudgets){
        Set<Budget> addedSubCategories = new HashSet<>();
        BudgetUpdateDtos.forEach(newBudget -> {
            if (newBudget.getRefNo() != null) {
                Budget existingBudget = findExistingEntity(existingBudgets, newBudget.getRefNo());
                if (existingBudget != null) {
                    budgetMapper.update(existingBudget, newBudget);
                    existingBudget.setUpdatedAt(LocalDateTime.now());
                }
            } else {
                //if id doesn't have id add it
                addedSubCategories.add(budgetMapper.reqEntityToEntity(newBudget));
            }
        });
        return addedSubCategories;
    }

    private Budget findExistingEntity (Set < Budget > existingBudgets, String refNo){
        return existingBudgets.stream().filter(Budget -> Budget.getRefNo().equals(refNo)).findFirst().orElse(null);
    }
}
