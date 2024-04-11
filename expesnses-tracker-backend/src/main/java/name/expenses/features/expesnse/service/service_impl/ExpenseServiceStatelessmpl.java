package name.expenses.features.expesnse.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ExpenseServiceStatelessmpl implements ExpenseService {
    public static final String EXPENSE = "Expense";
    private final ExpenseDAO expenseDAO;
    private final ExpenseMapper expenseMapper;
    @Override
    public ResponseDto createExpense(ExpenseReqDto expense) {
        Expense sentExpense = expenseMapper.reqDtoToEntity(expense);
        Expense savedExpense = expenseDAO.createExpense(sentExpense);
        log.info("created expense {}", savedExpense);
        return ResponseDtoBuilder.getCreateResponse(EXPENSE, savedExpense.getRefNo(), expenseMapper.entityToRespDto(savedExpense));
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
    public ResponseDto getExpense(String refNo) {
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
    public ResponseDto updateExpense(String refNo, ExpenseUpdateDto expenseUpdateDto) {
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
    public ResponseDto deleteExpense(String refNo) {
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
}
