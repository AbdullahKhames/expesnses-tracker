//package name.expenses.services.transaction;
//
//
//import jakarta.transaction.Transactional;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import name.expenses.dtos.PocketAmountUpdateDto;
//import name.expenses.dtos.TransactionUpdateDto;
//import name.expenses.exceptions.APIException;
//import name.expenses.exceptions.ErrorCode;
//import name.expenses.exceptions.GeneralFailureException;
//import name.expenses.models.*;
//import name.expenses.repostitories.CustomerDAO;
//import name.expenses.services.utils.ResponseDtoBuilder;
//
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//
//@Transactional
//@RequiredArgsConstructor
//public class TransactionServiceImpl {
//    public static final String TRANSACTION = "Transaction";
//    private final CustomerDAO customerDAO;
//    private final TransactionDAO transactionDAO;
//    private final TransactionMapper transactionMapper;
//    private final PocketAmountMapper pocketAmountMapper;
//
//
//    public Optional<Transaction> getEntity(String refNo){
//        try {
//            Optional<Transaction> transactionOptional = transactionDAO.get(refNo);
//            log.info("fetched transaction {}", transactionOptional);
//            return transactionOptional;
//
//        }catch (Exception ex){
//            return Optional.empty();
//        }
//    }
//
//    public ResponseDto update(String refNo, TransactionUpdateDto transactionUpdateDto) {
//        Optional<Transaction> transactionOptional = getEntity(refNo);
//        if (transactionOptional.isPresent()){
//            Transaction transaction = transactionOptional.get();
//            log.info("fetched transaction {}", transaction);
//            updateAssociation(transaction, transactionUpdateDto);
//            transactionMapper.update(transaction, transactionUpdateDto);
//
//            if (transaction.getAmount() != transaction.getExpense().getAmount()){
//                throw new APIException(ErrorCode.AMOUNT_NOT_EQUAL.getErrorCode());
//            }
//
//            Transaction savedTransaction = transactionDAO.update(transaction);
//
//            log.info("updated transaction {}", savedTransaction);
//            return ResponseDtoBuilder.getUpdateResponse(TRANSACTION, savedTransaction.getRefNo(), transactionMapper.entityToRespDto(savedTransaction));
//        }
//        ResponseError responseError = new ResponseError();
//        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
//        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
//        responseError.setErrorMessage(String.format("transaction with the ref number %s was not found", refNo));
//        return ResponseDtoBuilder.getErrorResponse(804, responseError);
//
//    }
//
//    public void updateAssociation(Transaction entity, TransactionUpdateDto entityUpdateDto) {
//        Set<PocketAmount> transactionPocketAmounts = entity.getPocketAmounts();
//        Set<PocketAmountUpdateDto> pocketAmountUpdateDtos = entityUpdateDto.getPocketAmountUpdateDtos();
//        if (pocketAmountUpdateDtos == null) {
//            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
//        }
//        Set<Pocket> pockets = new HashSet<>();
//        Set<PocketAmount> newPocketAmounts = pocketAmountUpdateDtos
//                .stream()
//                .map(updateDto -> {
//                    PocketAmount pocketAmount;
//                    if (updateDto.getRefNo() == null){
//                        //create the new pocket amount
//                        pocketAmount = pocketAmountMapper.updateDtoToEntity(updateDto);
//                        Optional<Pocket> pocketOptional = pocketService.getEntity(updateDto.getPocketRefNo());
//                        if (pocketOptional.isPresent()){
//                            Pocket pocket = pocketOptional.get();
//                            if (!Objects.equals(pocket.getCustomer(), getCustomer())){
//                                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
//                                        Map.of("error", "pocket customer is not the same as current customer please use your pocket!"));
//                            }
//                            pocketAmount.setPocket(pocket);
//                            pocketAmount.setAmountType(AmountType.DEBIT);
//                            pocket.setAmount(pocket.getAmount() - pocketAmount.getAmount());
//                            pockets.add(pocket);
//                        }else {
//                            return null;
//                        }
//
//                    }else {
//                        // let the pocket amount service update it
//                        pocketAmount = transactionPocketAmounts.stream()
//                                .filter(pocketAmount1 -> pocketAmount1.getRefNo().equals(updateDto.getRefNo()))
//                                .findFirst()
//                                .orElse(null);
//                        // ignore if sent refNo from update dto not present in the pocket amounts in the transaction
//                        if (pocketAmount == null) {
//                            return null;
//                        }
//                        pocketAmountService.updateAssociation(pocketAmount, updateDto);
//                    }
//                    return pocketAmount;
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//
//        if (newPocketAmounts.isEmpty()){
//            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
//        }
//
////        entity.setPocketAmounts(newPocketAmounts);
//        entity.setAmount(newPocketAmounts
//                .stream()
//                .map(PocketAmount::getAmount)
//                .reduce(0.0, (x, y) -> x + y)
//        );
//        pocketService.updateAll(pockets);
//
//        Set<PocketAmount> removedPocketAmounts = new HashSet<>(transactionPocketAmounts);
//        removedPocketAmounts.removeAll(newPocketAmounts);
//        removedPocketAmounts.forEach(pocketAmount -> {
//            pocketAmountService.updateOrResetAmount(pocketAmount, pocketAmount.getPocket(), true, pocketAmount.getAmount());
//        });
//        entity.getPocketAmounts().removeAll(removedPocketAmounts);
//        entity.getPocketAmounts().addAll(newPocketAmounts);
//    }
//
//    private Customer getCustomer() {
//        return
//    }
//}
