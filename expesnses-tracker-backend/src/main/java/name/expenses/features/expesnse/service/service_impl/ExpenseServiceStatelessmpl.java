package name.expenses.features.expesnse.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.association.AssociationResponse;
import name.expenses.features.association.Models;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.sub_category.dao.SubCategoryDAO;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;
import name.expenses.utils.collection_getter.ExpenseGetter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ExpenseServiceStatelessmpl implements ExpenseService {
    public static final String EXPENSE = "Expense";
    private final ExpenseDAO expenseDAO;
    private final SubCategoryDAO subCategoryDAO;
    private final CustomerDAO customerDAO;
    private final ExpenseMapper expenseMapper;
    @Override
    public ResponseDto create(ExpenseReqDto expenseReqDto) {
        Expense sentExpense = expenseMapper.reqDtoToEntity(expenseReqDto);
        Optional<Customer> customerOptional = customerDAO.get(expenseReqDto.getCustomerId());
        if (customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            if (customer.getExpenses() == null) {
                customer.setExpenses(new HashSet<>());
            }
            customer.getExpenses().add(sentExpense);
            sentExpense.setCustomer(customer);
//            customerDAO.update(customer);
        }else {
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "customer not found"));
        }

        associateSubCategory(expenseReqDto, sentExpense);
        Expense savedExpense = expenseDAO.createExpense(sentExpense);

        if (savedExpense == null || savedExpense.getId() == null){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "expense not found"));
        }
        log.info("created expense {}", savedExpense);
        return ResponseDtoBuilder.getCreateResponse(EXPENSE, savedExpense.getRefNo(), expenseMapper.entityToRespDto(savedExpense));
    }

    private void associateSubCategory(ExpenseReqDto expenseReqDto, Expense sentExpense) {
        Optional<SubCategory> subCategoryOptional = subCategoryDAO.get(expenseReqDto.getSubCategoryRefNo());
        if (subCategoryOptional.isPresent()){
            SubCategory subCategory = subCategoryOptional.get();
            if (subCategory.getExpenses() == null) {
                subCategory.setExpenses(new HashSet<>());
            }
            subCategory.getExpenses().add(sentExpense);
            sentExpense.setSubCategory(subCategory);
//            subCategoryDAO.update(subCategory);
        }else {
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "sub category not found"));
        }
    }

    public Optional<Expense> getEntity(String refNo){
        try {
            Optional<Expense> expenseOptional = expenseDAO.getExpense(refNo);
            log.info("fetched expense {}", expenseOptional);
            return expenseOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    public Optional<Expense> getEntity(String refNo, SubCategory existingSubCategory){
        try {
            Optional<Expense> expenseOptional = existingSubCategory.getExpenses().stream().filter(expense1 -> expense1.getRefNo().equals(refNo)).findFirst();
            log.info("fetched expense {}", expenseOptional);
            return expenseOptional;
        }catch (Exception ex){
            return Optional.empty();
        }
    }

    @Override
    public ResponseDto get(String refNo) {
        Optional<Expense> expenseOptional = getEntity(refNo);
        if (expenseOptional.isPresent()){
            Expense expense = expenseOptional.get();
            log.info("fetched expense {}", expense);
            return ResponseDtoBuilder.getFetchResponse(EXPENSE, expense.getRefNo(), expenseMapper.entityToRespDto(expense));
        }else {
            ResponseError responseError = new ResponseError();
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("expense with reference number %s was not found", refNo));
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, ExpenseUpdateDto expenseUpdateDto) {
        Optional<Expense> expenseOptional = getEntity(refNo);
        if (expenseOptional.isPresent()){
            Expense expense = expenseOptional.get();
            log.info("fetched expense {}", expense);
            expenseMapper.update(expense, expenseUpdateDto);
            log.info("updated expense {}", expense);
            expense.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(EXPENSE, expense.getRefNo(), expenseMapper.entityToRespDto(expenseDAO.updateExpense(expense)));
        }else {
            ResponseError responseError = new ResponseError();
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("expense with reference number %s was not found", refNo));
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }
    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(EXPENSE,expenseDAO.deleteExpense(refNo));
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
        Page<Expense> expensePage = expenseDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);

//        List<ExpenseRespDto> expenseDtos = expensePage.getContent().stream()
//                .map(expenseMapper::entityToRespDto)
//                .collect(Collectors.toList());
        Page<ExpenseRespDto> expenseDtos = expenseMapper.entityToRespDto(expensePage);
        return ResponseDtoBuilder.getFetchAllResponse(EXPENSE, expenseDtos);
    }

    @Override
    public Set<Expense> getEntities(Set<String> refNos) {
        return expenseDAO.getEntities(refNos);
    }

    @Override
    public void updateExpensesAssociation(SubCategory existingSubCategory, SubCategoryUpdateDto newSubCategory) {
        if (existingSubCategory.getExpenses() != null && newSubCategory.getExpenses() != null) {
            Set<Expense> expenses = new HashSet<>();
            newSubCategory.getExpenses().forEach(newExpense ->{
                Optional<Expense> expenseOptional = getEntity(newExpense.getRefNo(), existingSubCategory);
                if (expenseOptional.isPresent()) {
                    Expense expense = expenseOptional.get();
                    expenseMapper.update(expense, newExpense);
                    expenses.add(expense);
                }else {
                    expenses.add(expenseMapper.reqEntityToEntity(newExpense));
                }
            });
            existingSubCategory.getExpenses().retainAll(expenses);
            existingSubCategory.getExpenses().addAll(expenses);
        }
    }

    @Override
    public boolean addAssociation(SubCategory entity, Models entityModel, String refNo) {
        return false;
    }



    @Override
    public ResponseDto addDtoAssociation(Object entity, Models entityModel, Set<?> associationReqDto) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, ExpenseGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        ResponseDto errorResponse = ValidateInputUtils.validateWildCardSet(associationReqDto, ExpenseReqDto.class);
        if (errorResponse != null) {
            return errorResponse;
        }
        ExpenseGetter expensesGetter = (ExpenseGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        try {
            Set<Expense> expenses = associationReqDto.stream()
                    .map(reqDto ->{
                        ExpenseReqDto expenseReqDto = (ExpenseReqDto) reqDto;
                        Expense expense = expenseMapper.reqDtoToEntity(expenseReqDto);
//                        expensesGetter.getExpenses().add(expense);
                        if (entityModel == Models.CUSTOMER){
                            expense.setCustomer((Customer) expensesGetter);
                        }
                        associateSubCategory(expenseReqDto, expense);
                        return expense;
                    })
                    .collect(Collectors.toSet());
            expensesGetter.getExpenses().addAll(expenses);
            expenseDAO.saveAll(expenses);
        }catch (Exception ex){
            throw new IllegalArgumentException(ex);
        }

        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), expensesGetter.getRefNo(), associationResponse);
    }

    @Override
    public boolean removeAssociation(SubCategory entity, Models entityModel, String refNo) {
        return false;
    }



    @Override
    public ResponseDto removeDtoAssociation(Object entity, Models entityModel, Set<?> associationsUpdateDto) {
        return null;
    }

    @Override
    public ResponseDto addAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, ExpenseGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        ExpenseGetter expensesGetter = (ExpenseGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Expense> expenseOptional = getEntity(refNo);
            if (expenseOptional.isPresent()){
                Expense expense = expenseOptional.get();
                if (expensesGetter.getExpenses() == null){
                    expensesGetter.setExpenses(new HashSet<>());
                }
                if (expensesGetter.getExpenses().contains(expense)){
                    associationResponse.getError().put(refNo, "this expensesGetter already contain this expense");
                }else if (customerDAO.existByExpense(expense)) {
                    associationResponse.getError().put(refNo, "this expense already present on another customer");
                } else {
                    expensesGetter.getExpenses().add(expense);
                    associationResponse.getSuccess().put(refNo, "was added successfully");
                }
            }else {
                associationResponse.getError().put(refNo, "no expense corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), expensesGetter.getRefNo(), associationResponse);
    }
    @Override
    public ResponseDto removeAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, ExpenseGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        ExpenseGetter expensesGetter = (ExpenseGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Expense> expenseOptional = getEntity(refNo);
            if (expenseOptional.isPresent()){
                Expense expense = expenseOptional.get();
                if (expensesGetter.getExpenses() == null){
                    expensesGetter.setExpenses(new HashSet<>());
                }
                if (expensesGetter.getExpenses().contains(expense)){
                    expensesGetter.getExpenses().remove(expenseOptional.get());
                    associationResponse.getSuccess().put(refNo, "was removed successfully");
                }else {
                    associationResponse.getError().put(refNo, "this expensesGetter doesn't contain this expense");
                }
            }else {
                associationResponse.getError().put(refNo, "no expense corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), expensesGetter.getRefNo(), associationResponse);
    }
}
