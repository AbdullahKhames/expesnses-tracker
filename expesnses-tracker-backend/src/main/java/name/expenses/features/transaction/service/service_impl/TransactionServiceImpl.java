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
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.pocket_transfer.dtos.request.PocketAmountUpdateDto;
import name.expenses.features.pocket_transfer.mappers.PocketAmountMapper;
import name.expenses.features.pocket_transfer.models.AmountType;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.features.pocket_transfer.service.PocketAmountService;
import name.expenses.features.sub_category.service.SubService;
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
    private final PocketAmountMapper pocketAmountMapper;
    private final CustomerService customerService;
    private final PocketService pocketService;
    private final ExpenseService expenseService;
    private final PocketAmountService pocketAmountService;

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
        Set<Pocket> pockets = new HashSet<>();
        Set<PocketAmount> pocketAmounts = transactionReqDto
                .getPocketAmountReqDtos()
                .stream()
                .map(reqDto -> {
                    PocketAmount pocketAmount = pocketAmountMapper.reqDtoToEntity(reqDto);
                    Optional<Pocket> pocketOptional = pocketService.getEntity(reqDto.getPocketRefNo());
                    if (pocketOptional.isPresent()){
                        Pocket pocket = pocketOptional.get();
                        if (!Objects.equals(pocket.getCustomer(), customer)){
                            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                    Map.of("error", "pocket customer is not the same as current customer please use your pocket!"));
                        }
                        pocketAmount.setPocket(pocket);
                        pocketAmount.setAmountType(AmountType.DEBIT);
                        pocket.setAmount(pocket.getAmount() - pocketAmount.getAmount());
                        pockets.add(pocket);
                    }else {
                        return null;
                    }
//                    pocketOptional.ifPresent(pocketAmount::setPocket);
                    return pocketAmount;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (pocketAmounts.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }

        Transaction sentTransaction = transactionMapper.reqDtoToEntity(transactionReqDto);

        sentTransaction.setExpense(expense);
        sentTransaction.setCustomer(customer);
        sentTransaction.setPocketAmounts(pocketAmounts);
        sentTransaction.setAmount(pocketAmounts
                .stream()
                .map(PocketAmount::getAmount)
                .reduce(0.0, Double::sum)
        );

        if (sentTransaction.getAmount() != sentTransaction.getExpense().getAmount()){
            throw new APIException(ErrorCode.AMOUNT_NOT_EQUAL.getErrorCode());
        }

        Transaction savedTransaction = transactionDAO.create(sentTransaction);
        customerService.update(customer);
        pocketService.updateAll(pockets);
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
        Set<PocketAmount> transactionPocketAmounts = entity.getPocketAmounts();
        Set<PocketAmountUpdateDto> pocketAmountUpdateDtos = entityUpdateDto.getPocketAmountUpdateDtos();
        if (pocketAmountUpdateDtos == null) {
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Set<Pocket> pockets = new HashSet<>();
        Set<PocketAmount> newPocketAmounts = pocketAmountUpdateDtos
                .stream()
                .map(updateDto -> {
                    PocketAmount pocketAmount;
                    if (updateDto.getRefNo() == null){
                        //create the new pocket amount
                        pocketAmount = pocketAmountMapper.updateDtoToEntity(updateDto);
                        Optional<Pocket> pocketOptional = pocketService.getEntity(updateDto.getPocketRefNo());
                        if (pocketOptional.isPresent()){
                            Pocket pocket = pocketOptional.get();
                            if (!Objects.equals(pocket.getCustomer(), getCustomer())){
                                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                        Map.of("error", "pocket customer is not the same as current customer please use your pocket!"));
                            }
                            pocketAmount.setPocket(pocket);
                            pocketAmount.setAmountType(AmountType.DEBIT);
                            pocket.setAmount(pocket.getAmount() - pocketAmount.getAmount());
                            pockets.add(pocket);
                        }else {
                            return null;
                        }

                    }else {
                        // let the pocket amount service update it
                        pocketAmount = transactionPocketAmounts.stream()
                                .filter(pocketAmount1 -> pocketAmount1.getRefNo().equals(updateDto.getRefNo()))
                                .findFirst()
                                .orElse(null);
                        // ignore if sent refNo from update dto not present in the pocket amounts in the transaction
                        if (pocketAmount == null) {
                            return null;
                        }
                        pocketAmountService.updateAssociation(pocketAmount, updateDto);
                    }
                    return pocketAmount;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (newPocketAmounts.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }

//        entity.setPocketAmounts(newPocketAmounts);
        entity.setAmount(newPocketAmounts
                .stream()
                .map(PocketAmount::getAmount)
                .reduce(0.0, Double::sum)
        );
        pocketService.updateAll(pockets);

        Set<PocketAmount> removedPocketAmounts = new HashSet<>(transactionPocketAmounts);
        removedPocketAmounts.removeAll(newPocketAmounts);
        removedPocketAmounts.forEach(pocketAmount -> pocketAmountService.updateOrResetAmount(pocketAmount, pocketAmount.getPocket(), true, pocketAmount.getAmount()));
        entity.getPocketAmounts().removeAll(removedPocketAmounts);
        entity.getPocketAmounts().addAll(newPocketAmounts);
    }
}
