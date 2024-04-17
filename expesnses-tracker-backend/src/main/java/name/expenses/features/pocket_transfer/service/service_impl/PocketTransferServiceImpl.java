package name.expenses.features.pocket_transfer.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.pocket_transfer.dao.PocketTransferDAO;
import name.expenses.features.pocket_transfer.dtos.request.PocketTransferReqDto;
import name.expenses.features.pocket_transfer.dtos.request.PocketTransferUpdateDto;
import name.expenses.features.pocket_transfer.dtos.response.PocketTransferRespDto;
import name.expenses.features.pocket_transfer.mappers.PocketAmountMapper;
import name.expenses.features.pocket_transfer.mappers.PocketTransferMapper;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.features.pocket_transfer.service.PocketTransferService;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.features.user.models.User;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
//@Transactional
public class PocketTransferServiceImpl implements PocketTransferService {
    public static final String TRANSACTION = "PocketTransfer";
    private final PocketTransferDAO pocketTransferDAO;
    private final PocketTransferMapper pocketTransferMapper;
    private final PocketAmountMapper pocketAmountMapper;
    private final CustomerService customerService;
    private final PocketService pocketService;

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
    public ResponseDto create(PocketTransferReqDto pocketTransferReqDto) {
        Customer customer = getCustomer();
        if (customer == null) {
            throw new GeneralFailureException(ErrorCode.CUSTOMER_IS_BLOCKED.getErrorCode(),
                    Map.of("error", "customer not found in the context please log in !"));
        }
        PocketTransfer sentPocketTransfer = pocketTransferMapper.reqDtoToEntity(pocketTransferReqDto);

//      ready the sender pocket amount object
        Optional<Pocket> senderPocketOptional = pocketService.getEntity(pocketTransferReqDto.getSenderPocketAmountReqDto().getPocketRefNo());
        if (senderPocketOptional.isEmpty()){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "pocket not found with given refNo!"));
        }
        Pocket senderPocket = senderPocketOptional.get();
        if (!Objects.equals(senderPocket.getCustomer(), customer)){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "pocket customer the same as current customer please use your pocket!"));
        }
        PocketAmount senderPocketAmount = pocketAmountMapper.reqDtoToEntity(pocketTransferReqDto.getSenderPocketAmountReqDto());
        senderPocketAmount.setPocket(senderPocket);
        senderPocketAmount.setTrans(true);

        if (senderPocketAmount.getAmount() > senderPocket.getAmount()){
            throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                    Map.of("error", String.format("Amount to send by sender %s is greater that already in wallet %s",
                            senderPocketAmount.getAmount(), senderPocket.getAmount())));
        }
        Set<Pocket> pockets = new HashSet<>();
        Set<PocketAmount> receiversPocketAmounts = pocketTransferReqDto
                .getReceiverPocketAmountsReqDtos()
                .stream()
                .map(reqDto -> {
                    PocketAmount pocketAmount = pocketAmountMapper.reqDtoToEntity(reqDto);
                    Optional<Pocket> pocketOptional = pocketService.getEntity(reqDto.getPocketRefNo());
                    if (pocketOptional.isPresent()){
                        Pocket pocket = pocketOptional.get();
                        if (pocket.equals(senderPocket)){
                            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                    Map.of("error", "the sender pocket cannot be one of the receivers"));
                        }
                        if (!Objects.equals(pocket.getCustomer(), customer)){
                            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                                    Map.of("error", "pocket customer the same as current customer please use your pocket!"));
                        }
                        pocketAmount.setPocket(pocket);
                        pocketAmount.setTrans(true);
                        pocket.setAmount(pocket.getAmount() + pocketAmount.getAmount());
                        pockets.add(pocket);
                    }else {
                        return null;
                    }
                    return pocketAmount;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (receiversPocketAmounts.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Double receiversAmount = receiversPocketAmounts.stream().map(PocketAmount::getAmount).reduce(0.0, (x, y) -> x + y);

        if (!Objects.equals(senderPocketAmount.getAmount(), receiversAmount)){
            throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                    Map.of("error", String.format("Amount received by receivers %s doesn't match amount sent by sender %s",
                            receiversAmount, senderPocketAmount.getAmount())));
        }
        senderPocket.setAmount(senderPocket.getAmount() - receiversAmount);
        pockets.add(senderPocket);

        sentPocketTransfer.setAmount(receiversAmount);
        sentPocketTransfer.setCustomer(customer);
        sentPocketTransfer.setSenderPocketAmount(senderPocketAmount);
        sentPocketTransfer.setReceiverPocketAmounts(receiversPocketAmounts);

        PocketTransfer savedPocketTransfer = pocketTransferDAO.create(sentPocketTransfer);
        customerService.update(customer);
        pocketService.updateAll(pockets);
        log.info("created pocketTransfer {}", savedPocketTransfer);
        return ResponseDtoBuilder.getCreateResponse(TRANSACTION, savedPocketTransfer.getRefNo(), pocketTransferMapper.entityToRespDto(savedPocketTransfer));
    }

    public Optional<PocketTransfer> getEntity(String refNo){
        try {
            Optional<PocketTransfer> pocketTransferOptional = pocketTransferDAO.get(refNo);
            log.info("fetched pocketTransfer {}", pocketTransferOptional);
            return pocketTransferOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<PocketTransfer> pocketTransferOptional = getEntity(refNo);
            log.info("fetched pocketTransfer {}", pocketTransferOptional);
            if (pocketTransferOptional.isPresent()){
                PocketTransfer pocketTransfer = pocketTransferOptional.get();
                return ResponseDtoBuilder.getFetchResponse(TRANSACTION, pocketTransfer.getRefNo(), pocketTransferMapper.entityToRespDto(pocketTransfer));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("pocketTransfer with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("pocketTransfer with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, PocketTransferUpdateDto pocketTransferUpdateDto) {
        Optional<PocketTransfer> pocketTransferOptional = getEntity(refNo);
        if (pocketTransferOptional.isPresent()){
            PocketTransfer pocketTransfer = pocketTransferOptional.get();
            log.info("fetched pocketTransfer {}", pocketTransfer);
            pocketTransferMapper.update(pocketTransfer, pocketTransferUpdateDto);
//            updatePocketTransferService.updateCategoryAssociations(pocketTransfer, pocketTransferUpdateDto);
            log.info("updated pocketTransfer {}", pocketTransfer);
            pocketTransfer.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(TRANSACTION, pocketTransfer.getRefNo(), pocketTransferMapper.entityToRespDto(pocketTransferDAO.update(pocketTransfer)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("pocketTransfer with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(TRANSACTION,pocketTransferDAO.delete(refNo));
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
        Page<PocketTransfer> pocketTransferPage = pocketTransferDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);
        Page<PocketTransferRespDto> pocketTransferDtos = pocketTransferMapper.entityToRespDto(pocketTransferPage);
        return ResponseDtoBuilder.getFetchAllResponse(TRANSACTION, pocketTransferDtos);
    }

    @Override
    public Set<PocketTransfer> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }
        return pocketTransferDAO.getEntities(refNos);
    }


}
