package name.expenses.features.budget_transfer.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.models.BudgetType;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.budget_transfer.dao.BudgetTransferDAO;
import name.expenses.features.budget_transfer.dtos.request.BudgetTransferReqDto;
import name.expenses.features.budget_transfer.dtos.request.BudgetTransferUpdateDto;
import name.expenses.features.budget_transfer.dtos.response.BudgetTransferRespDto;
import name.expenses.features.budget_transfer.mappers.BudgetAmountMapper;
import name.expenses.features.budget_transfer.mappers.BudgetTransferMapper;
import name.expenses.features.budget_transfer.models.AmountType;

import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
import name.expenses.features.budget_transfer.service.BudgetAmountService;
import name.expenses.features.budget_transfer.service.BudgetTransferService;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.user.models.User;
import name.expenses.globals.Page;
import name.expenses.globals.PageReq;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class BudgetTransferServiceImpl implements BudgetTransferService {
    public static final String BUDGET_TRANSFER = "budgetTransfer";
    private final BudgetTransferDAO budgetTransferDAO;
    private final BudgetTransferMapper budgetTransferMapper;
    private final BudgetAmountMapper budgetAmountMapper;
    private final CustomerService customerService;
    private final BudgetService budgetService;
    private final BudgetAmountService budgetAmountService;

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
    public ResponseDto create(BudgetTransferReqDto budgetTransferReqDto) {
        Customer customer = getCustomer();
        if (customer == null) {
            throw new GeneralFailureException(ErrorCode.CUSTOMER_IS_BLOCKED.getErrorCode(),
                    Map.of("error", "customer not found in the context please log in !"));
        }
        BudgetTransfer sentbudgetTransfer = budgetTransferMapper.reqDtoToEntity(budgetTransferReqDto);

//      ready the sender Budget amount object
        Optional<Budget> senderbudgetOptional = budgetService.getEntity(budgetTransferReqDto.getSenderBudgetAmountReqDto().getBudgetRefNo());
        if (senderbudgetOptional.isEmpty()){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "Budget not found with given refNo!"));
        }
        Budget senderbudget = senderbudgetOptional.get();
        if ((senderbudget.getBudgetType() == BudgetType.EXTERNAL)){
            sentbudgetTransfer.setLending(true);
        }else if (!Objects.equals(senderbudget.getCustomer(), customer)){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "sender Budget customer is not the same as current customer please use your Budget!"));
        }
        BudgetAmount senderbudgetAmount = budgetAmountMapper.reqDtoToEntity(budgetTransferReqDto.getSenderBudgetAmountReqDto());
        senderbudgetAmount.setBudget(senderbudget);
        senderbudgetAmount.setTrans(true);
        senderbudgetAmount.setAmountType(AmountType.DEBIT);

        if (senderbudgetAmount.getAmount() > senderbudget.getAmount() && !(senderbudget.getBudgetType() == BudgetType.EXTERNAL)){
            throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                    Map.of("error", String.format("Amount to send by sender %s is greater that already in wallet %s",
                            senderbudgetAmount.getAmount(), senderbudget.getAmount())));
        }
        Set<Budget> budgets = new HashSet<>();
        Set<BudgetAmount> receiversbudgetAmounts = budgetTransferReqDto
                .getReceiverBudgetAmountsReqDtos()
                .stream()
                .map(reqDto -> {
                    BudgetAmount budgetAmount = budgetAmountMapper.reqDtoToEntity(reqDto);
                    Optional<Budget> budgetOptional = budgetService.getEntity(reqDto.getBudgetRefNo());
                    if (budgetOptional.isPresent()){
                        Budget budget = budgetOptional.get();
                        if (budget.equals(senderbudget)){
                            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                    Map.of("error", "the sender Budget cannot be one of the receivers"));
                        }
                        if ((budget.getBudgetType() == BudgetType.EXTERNAL)){
                            sentbudgetTransfer.setLending(true);
                        }
//                        if (!Objects.equals(Budget.getCustomer(), customer)){
//                            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
//                                    Map.of("error", "Budget customer the same as current customer please use your Budget!"));
//                        }
                        budgetAmount.setBudget(budget);
                        budgetAmount.setTrans(true);
                        budgetAmount.setAmountType(AmountType.CREDIT);
                        budget.setAmount(budget.getAmount() + budgetAmount.getAmount());
                        budgets.add(budget);
                    }else {
                        return null;
                    }
                    return budgetAmount;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (receiversbudgetAmounts.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Double receiversAmount = receiversbudgetAmounts.stream().map(BudgetAmount::getAmount).reduce(0.0, Double::sum);

        if (!Objects.equals(senderbudgetAmount.getAmount(), receiversAmount)){
            throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                    Map.of("error", String.format("Amount received by receivers %s doesn't match amount sent by sender %s",
                            receiversAmount, senderbudgetAmount.getAmount())));
        }
        senderbudget.setAmount(senderbudget.getAmount() - receiversAmount);
        budgets.add(senderbudget);

        sentbudgetTransfer.setAmount(receiversAmount);
        sentbudgetTransfer.setCustomer(customer);
        sentbudgetTransfer.setSenderBudgetAmount(senderbudgetAmount);
        sentbudgetTransfer.setReceiverBudgetAmounts(receiversbudgetAmounts);

        BudgetTransfer savedbudgetTransfer = budgetTransferDAO.create(sentbudgetTransfer);
        customerService.update(customer);
        budgetService.updateAll(budgets);
        log.info("created budgetTransfer {}", savedbudgetTransfer);
        return ResponseDtoBuilder.getCreateResponse(BUDGET_TRANSFER, savedbudgetTransfer.getRefNo(), budgetTransferMapper.entityToRespDto(savedbudgetTransfer));
    }

    public Optional<BudgetTransfer> getEntity(String refNo){
        try {
            Optional<BudgetTransfer> budgetTransferOptional = budgetTransferDAO.get(refNo);
            log.info("fetched budgetTransfer {}", budgetTransferOptional);
            return budgetTransferOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<BudgetTransfer> budgetTransferOptional = getEntity(refNo);
            log.info("fetched budgetTransfer {}", budgetTransferOptional);
            if (budgetTransferOptional.isPresent()){
                BudgetTransfer budgetTransfer = budgetTransferOptional.get();
                return ResponseDtoBuilder.getFetchResponse(BUDGET_TRANSFER, budgetTransfer.getRefNo(), budgetTransferMapper.entityToRespDto(budgetTransfer));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("budgetTransfer with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("budgetTransfer with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, BudgetTransferUpdateDto budgetTransferUpdateDto) {
        Optional<BudgetTransfer> budgetTransferOptional = getEntity(refNo);
        if (budgetTransferOptional.isPresent()){
            BudgetTransfer budgetTransfer = budgetTransferOptional.get();
            log.info("fetched budgetTransfer {}", budgetTransfer);
//            budgetTransferMapper.update(budgetTransfer, budgetTransferUpdateDto);
            updateAssociation(budgetTransfer, budgetTransferUpdateDto);
            log.info("updated budgetTransfer {}", budgetTransfer);
            budgetTransfer.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(BUDGET_TRANSFER, budgetTransfer.getRefNo(), budgetTransferMapper.entityToRespDto(budgetTransferDAO.update(budgetTransfer)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("budgetTransfer with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(BUDGET_TRANSFER,budgetTransferDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<BudgetTransfer> budgetTransferPage = budgetTransferDAO.findAll(pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<BudgetTransferRespDto> budgetTransferDtos = budgetTransferMapper.entityToRespDto(budgetTransferPage);
        return ResponseDtoBuilder.getFetchAllResponse(BUDGET_TRANSFER, budgetTransferDtos);
    }

    @Override
    public Set<BudgetTransfer> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }
        return budgetTransferDAO.getEntities(refNos);
    }


    @Override
    public void updateAssociation(BudgetTransfer entity, BudgetTransferUpdateDto entityUpdateDto) {
        budgetTransferMapper.update(entity, entityUpdateDto);

    }
}
