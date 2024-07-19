package name.expenses.features.transaction.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;
import name.expenses.features.budget_transfer.mappers.BudgetAmountMapper;
import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.features.budget_transfer.service.BudgetAmountService;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.budget_transfer.models.AmountType;
import name.expenses.features.transaction.dao.TransactionDAO;
import name.expenses.features.transaction.dtos.request.TransactionReqDto;
import name.expenses.features.transaction.dtos.request.TransactionUpdateDto;
import name.expenses.features.transaction.dtos.response.TransactionRespDto;
import name.expenses.features.transaction.mappers.TransactionMapper;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.features.transaction.service.TransactionService;
import name.expenses.features.user.models.User;
import name.expenses.globals.Page;
import name.expenses.globals.PageReq;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class TransactionServiceImpl implements TransactionService {
    public static final String TRANSACTION = "Transaction";
    private final TransactionDAO transactionDAO;
    private final TransactionMapper transactionMapper;
    private final BudgetAmountMapper budgetAmountMapper;
    private final CustomerService customerService;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final BudgetAmountService budgetAmountService;

    @Setter
    @Context
    private SecurityContext securityContext;


    private Customer getCustomer(){
        try{
            return ((User) securityContext.getUserPrincipal()).getCustomer();
        }catch (Exception ex){
            log.error("error occurred {}" , ex.getMessage());
            return null;
        }
    }
    @Override
    public ResponseDto create(TransactionReqDto transactionReqDto) {
        Customer customer = getCustomer();
        if (customer == null) {
            throw new GeneralFailureException(ErrorCode.CUSTOMER_IS_BLOCKED.getErrorCode(),
                    Map.of("error", "customer not found in the context please log in !"));
        }

        // ready the expense object
        Expense expense = expenseService.reqDtoToEntity(transactionReqDto.getExpense());
        expenseService.associateSubCategory(transactionReqDto.getExpense().getSubCategoryRefNo(), expense, customer);
        expense.setCustomer(customer);

        //        Expense savedExpense = expenseService.save(expense);
//        if (savedExpense == null || savedExpense.getId() == null){
//            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
//                    Map.of("error", "expense not found"));
//        }
        Set<Budget> budgets = new HashSet<>();
        Set<BudgetAmount> budgetAmounts = transactionReqDto
                .getBudgetAmountReqDtos()
                .stream()
                .map(reqDto -> {
                    BudgetAmount budgetAmount = budgetAmountMapper.reqDtoToEntity(reqDto);
                    Optional<Budget> budgetOptional = budgetService.getEntity(reqDto.getBudgetRefNo());
                    if (budgetOptional.isPresent()){
                        Budget budget = budgetOptional.get();
                        if (!Objects.equals(budget.getCustomer(), customer)){
                            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                    Map.of("error", "Budget customer is not the same as current customer please use your Budget!"));
                        }
                        budgetAmount.setBudget(budget);
                        budgetAmount.setAmountType(AmountType.DEBIT);
                        budget.setAmount(budget.getAmount() - budgetAmount.getAmount());
                        budgets.add(budget);
                    }else {
                        return null;
                    }
//                    budgetOptional.ifPresent(budgetAmount::setBudget);
                    return budgetAmount;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (budgetAmounts.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }

        Transaction sentTransaction = transactionMapper.reqDtoToEntity(transactionReqDto);

        sentTransaction.setExpense(expense);
        sentTransaction.setCustomer(customer);
        sentTransaction.setBudgetAmounts(budgetAmounts);
        sentTransaction.setAmount(budgetAmounts
                .stream()
                .map(BudgetAmount::getAmount)
                .reduce(0.0, Double::sum)
        );

        if (sentTransaction.getAmount() != sentTransaction.getExpense().getAmount()){
            throw new APIException(ErrorCode.AMOUNT_NOT_EQUAL.getErrorCode());
        }

        Transaction savedTransaction = transactionDAO.create(sentTransaction);
        customerService.update(customer);
        budgetService.updateAll(budgets);
        log.info("created transaction {}", savedTransaction);
        return ResponseDtoBuilder.getCreateResponse(TRANSACTION, savedTransaction.getRefNo(), transactionMapper.entityToRespDto(savedTransaction));
    }

    public Optional<Transaction> getEntity(String refNo){
        try {
            Optional<Transaction> transactionOptional = transactionDAO.get(refNo);
            log.info("fetched transaction {}", transactionOptional);
            return transactionOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<Transaction> transactionOptional = getEntity(refNo);
            log.info("fetched transaction {}", transactionOptional);
            if (transactionOptional.isPresent()){
                Transaction transaction = transactionOptional.get();
                return ResponseDtoBuilder.getFetchResponse(TRANSACTION, transaction.getRefNo(), transactionMapper.entityToRespDto(transaction));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("transaction with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("transaction with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, TransactionUpdateDto transactionUpdateDto) {
        Optional<Transaction> transactionOptional = getEntity(refNo);
        if (transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            log.info("fetched transaction {}", transaction);
            updateAssociation(transaction, transactionUpdateDto);
            transactionMapper.update(transaction, transactionUpdateDto);

            if (transaction.getAmount() != transaction.getExpense().getAmount()){
                throw new APIException(ErrorCode.AMOUNT_NOT_EQUAL.getErrorCode());
            }

            Transaction savedTransaction = transactionDAO.update(transaction);

            log.info("updated transaction {}", savedTransaction);
            return ResponseDtoBuilder.getUpdateResponse(TRANSACTION, savedTransaction.getRefNo(), transactionMapper.entityToRespDto(savedTransaction));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("transaction with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(TRANSACTION,transactionDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Transaction> transactionPage = transactionDAO.findAll(pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<TransactionRespDto> transactionDtos = transactionMapper.entityToRespDto(transactionPage);
        return ResponseDtoBuilder.getFetchAllResponse(TRANSACTION, transactionDtos);
    }

    @Override
    public Set<Transaction> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }
        return transactionDAO.getEntities(refNos);
    }


    @Override
    public void updateAssociation(Transaction entity, TransactionUpdateDto entityUpdateDto) {
        Set<BudgetAmount> transactionbudgetAmounts = entity.getBudgetAmounts();
        Set<BudgetAmountUpdateDto> budgetAmountUpdateDtos = entityUpdateDto.getBudgetAmountUpdateDtos();
        if (budgetAmountUpdateDtos == null) {
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Set<Budget> budgets = new HashSet<>();
        Set<BudgetAmount> newbudgetAmounts = budgetAmountUpdateDtos
                .stream()
                .map(updateDto -> {
                    BudgetAmount budgetAmount;
                    if (updateDto.getRefNo() == null){
                        //create the new Budget amount
                        budgetAmount = budgetAmountMapper.updateDtoToEntity(updateDto);
                        Optional<Budget> budgetOptional = budgetService.getEntity(updateDto.getBudgetRefNo());
                        if (budgetOptional.isPresent()){
                            Budget budget = budgetOptional.get();
                            if (!Objects.equals(budget.getCustomer(), getCustomer())){
                                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                        Map.of("error", "Budget customer is not the same as current customer please use your Budget!"));
                            }
                            budgetAmount.setBudget(budget);
                            budgetAmount.setAmountType(AmountType.DEBIT);
                            budget.setAmount(budget.getAmount() - budgetAmount.getAmount());
                            budgets.add(budget);
                        }else {
                            return null;
                        }

                    }else {
                        // let the Budget amount service update it
                        budgetAmount = transactionbudgetAmounts.stream()
                                .filter(budgetAmount1 -> budgetAmount1.getRefNo().equals(updateDto.getRefNo()))
                                .findFirst()
                                .orElse(null);
                        // ignore if sent refNo from update dto not present in the Budget amounts in the transaction
                        if (budgetAmount == null) {
                            return null;
                        }
                        budgetAmountService.updateAssociation(budgetAmount, updateDto);
                    }
                    return budgetAmount;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (newbudgetAmounts.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }

//        entity.setBudgetAmounts(newbudgetAmounts);
        entity.setAmount(newbudgetAmounts
                .stream()
                .map(BudgetAmount::getAmount)
                .reduce(0.0, Double::sum)
        );
        budgetService.updateAll(budgets);

        Set<BudgetAmount> removedbudgetAmounts = new HashSet<>(transactionbudgetAmounts);
        removedbudgetAmounts.removeAll(newbudgetAmounts);
        removedbudgetAmounts.forEach(budgetAmount -> budgetAmountService.updateOrResetAmount(budgetAmount, budgetAmount.getBudget(), true, budgetAmount.getAmount()));
        entity.getBudgetAmounts().removeAll(removedbudgetAmounts);
        entity.getBudgetAmounts().addAll(newbudgetAmounts);
    }
}
