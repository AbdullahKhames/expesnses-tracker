package name.expenses.features.pocket.service.service_impl;

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
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.pocket.dao.PocketDAO;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.dtos.response.PocketRespDto;
import name.expenses.features.pocket.mappers.PocketMapper;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.models.PocketType;
import name.expenses.features.pocket.service.PocketService;

import name.expenses.globals.Page;
import name.expenses.globals.PageReq;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;
import name.expenses.utils.collection_getter.PocketGetter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class PocketServiceImpl implements PocketService {
    public static final String POCKET = "Pocket";
    private final PocketDAO pocketDAO;
    private final PocketMapper pocketMapper;
    private final AccountDAO accountDAO;
    private final CustomerDAO customerDAO;



    @Override
    public ResponseDto create(PocketReqDto pocketReqDto) {
        try {
            Pocket sentPocket = pocketMapper.reqDtoToEntity(pocketReqDto);
//            Pocket savedPocket = pocketDAO.create(sentPocket);
            Optional<Customer> customerOptional = customerDAO.get(pocketReqDto.getCustomerId());
            Customer customer;
            if (customerOptional.isEmpty()){
                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                        Map.of("error", "customer not found"));
            }else {
                customer = customerOptional.get();
                if (customer.getPockets() == null) {
                    customer.setPockets(new HashSet<>());
                }
                customer.getPockets().add(sentPocket);
                sentPocket.setCustomer(customer);
//                customerDAO.update(customer);
            }
//            if (savedPocket == null || savedPocket.getId() == null){
//                throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
//                        Map.of("error", "savedPocket not found"));
//            }
            associateAccount(pocketReqDto.getAccountRefNo(), sentPocket, customer);
            Pocket savedPocket = pocketDAO.create(sentPocket);
            log.info("created pocket {}", savedPocket);
            customerDAO.update(customer);
            return ResponseDtoBuilder.getCreateResponse(POCKET, savedPocket.getRefNo(), pocketMapper.entityToRespDto(savedPocket));
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void associateAccount(String accountRefNo, Pocket sentPocket, Customer customer) {
        Optional<Account> accountOptional = accountDAO.get(accountRefNo);
        if (accountOptional.isEmpty()){
            throw new GeneralFailureException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode(),
                    Map.of("error", "account not found"));
        }else {
            Account account = accountOptional.get();
            if (account.getPockets() == null) {
                account.setPockets(new HashSet<>());
            }
            account.getPockets().add(sentPocket);
            sentPocket.setAccount(account);
            if (customer != null) {
                if (customer.getAccounts() == null){
                    customer.setAccounts(new HashSet<>());
                }
                customer.getAccounts().add(account);
            }
//                accountDAO.update(account);
        }
    }

    public Optional<Pocket> getEntity(String refNo){
        try {
            Optional<Pocket> pocketOptional = pocketDAO.get(refNo);
            log.info("fetched pocket {}", pocketOptional);
            return pocketOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<Pocket> pocketOptional = getEntity(refNo);
            log.info("fetched pocket {}", pocketOptional);
            if (pocketOptional.isPresent()){
                Pocket pocket = pocketOptional.get();
                return ResponseDtoBuilder.getFetchResponse(POCKET, pocket.getRefNo(), pocketMapper.entityToRespDto(pocket));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("pocket with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("pocket with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, PocketUpdateDto pocketUpdateDto) {
        Optional<Pocket> pocketOptional = getEntity(refNo);
        if (pocketOptional.isPresent()){
            Pocket pocket = pocketOptional.get();
            log.info("fetched pocket {}", pocket);
            pocketMapper.update(pocket, pocketUpdateDto);
            log.info("updated pocket {}", pocket);
            pocket.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(POCKET, pocket.getRefNo(), pocketMapper.entityToRespDto(pocketDAO.update(pocket)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("pocket with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }
    @Override
    public Pocket createDefaultPocket(){
        Pocket pocket = new Pocket();
        pocket.setName("default pocket");
        pocket.setDetails("default customer pocket");
        pocket.setPocketType(PocketType.DEFAULT);
        pocket.setDefaultSender(true);
        pocket.setDefaultReceiver(true);
        return pocket;
    }
    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(POCKET,pocketDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Pocket> pocketPage = pocketDAO.findAll(pageReq.getPageNumber(), pageReq.getPageSize(), sortBy, sortDirection);
        Page<PocketRespDto> pocketDtos = pocketMapper.entityToRespDto(pocketPage);
        return ResponseDtoBuilder.getFetchAllResponse(POCKET, pocketDtos);
    }

    @Override
    public Set<Pocket> updateAll(Set<Pocket> pockets) {
        return pocketDAO.updateAll(pockets);
    }

    @Override
    public ResponseDto getAllEntitiesWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Pocket> pocketPage = pocketDAO.findAllWithoutAccount(pageReq.getPageNumber(), pageReq.getPageSize(), sortBy, sortDirection);
        Page<PocketRespDto> pocketDtos = pocketMapper.entityToRespDto(pocketPage);
        return ResponseDtoBuilder.getFetchAllResponse(POCKET, pocketDtos);
    }

    @Override
    public Set<PocketRespDto> entityToRespDto(Set<Pocket> pockets) {
        return pocketMapper.entityToRespDto(pockets);
    }

    @Override
    public ResponseDto getPocketByName(String name) {
        if (name == null || name.isBlank()) {
            return ResponseDtoBuilder.getErrorResponse(804, "name cannot be null");
        }
        List<Pocket> pockets = pocketDAO.getByName(name);
        if (!pockets.isEmpty()){
            return ResponseDtoBuilder.getFetchAllResponse(POCKET, pocketMapper.entityToRespDto(
                    PageUtil.createPage(1L, (long) pockets.size(), pockets, pockets.size())
            ));
        }else {
            return ResponseDtoBuilder.getErrorResponse(804, "not found");
        }
    }

    @Override
    public Pocket update(Pocket oldPocket) {
        return pocketDAO.update(oldPocket);
    }

    @Override
    public Set<Pocket> getEntities(Set<String> refNos) {
        return pocketDAO.getEntities(refNos);
    }

    @Override
    public boolean addAssociation(Account entity, Models entityModel, String refNo) {
        if (ValidateInputUtils.isValidInput(entity, entity.getPockets())) {
            Optional<Pocket> pocketOptional = getEntity(refNo);
            if (pocketOptional.isEmpty()) {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("the pocket with given reference number : %s doesn't exist", refNo)));
            }
            Pocket pocket = pocketOptional.get();
            Long accountId = pocketDAO.checkAccountAssociation(pocket);
            if (accountId != null) {
                if (Objects.equals(accountId, entity.getId())) {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this pocket is already present in the given account!!"));
                }else {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this pocket is already present in another account!!"));
                }
            }

            entity.getPockets().add(pocketOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public ResponseDto addAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, PocketGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        PocketGetter pocketGetter = (PocketGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Pocket> pocketOptional = getEntity(refNo);
            if (pocketOptional.isPresent()){
                Pocket pocket = pocketOptional.get();
                if (pocketGetter.getPockets() == null){
                    pocketGetter.setPockets(new HashSet<>());
                }
                if (pocketGetter.getPockets().contains(pocket)){
                    associationResponse.getError().put(refNo, "this pocketGetter already contain this pocket");
                } else if (entityModel == Models.CUSTOMER && customerDAO.existByPocket(pocket)) {
                    associationResponse.getError().put(refNo, "this pocket already present on another customer");
                } else {
                    pocketGetter.getPockets().add(pocketOptional.get());
                    associationResponse.getSuccess().put(refNo, "was added successfully");
                }
            }else {
                associationResponse.getError().put(refNo, "no pocket corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), pocketGetter.getRefNo(), associationResponse);
    }

    @Override
    public ResponseDto addDtoAssociation(Object entity, Models entityModel, Set<?> associationReqDto) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, PocketGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        ResponseDto errorResponse = ValidateInputUtils.validateWildCardSet(associationReqDto, PocketReqDto.class);
        if (errorResponse != null) {
            return errorResponse;
        }
        PocketGetter pocketGetter = (PocketGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        try {
            Set<Pocket> pockets = associationReqDto.stream()
                    .map(reqDto ->{
                        PocketReqDto pocketReqDto = (PocketReqDto) reqDto;
                        Pocket pocket = pocketMapper.reqDtoToEntity(pocketReqDto);
//                        pocketGetter.getPockets().add(pocket);
                        if (entityModel == Models.CUSTOMER){
                            Customer customer = (Customer) pocketGetter;
                            pocket.setCustomer(customer);
                            associateAccount(((PocketReqDto) reqDto).getAccountRefNo(), pocket, customer);
                            try {
                                customerDAO.update(customer);
                            }catch (Exception ex){
                                log.error("exception occured {}", ex.getMessage());
                            }
                        }else {
                            associateAccount(((PocketReqDto) reqDto).getAccountRefNo(), pocket, null);
                        }
                        return pocket;
                    })
                    .collect(Collectors.toSet());
            pocketGetter.getPockets().addAll(pockets);
            pocketDAO.saveAll(pockets);
        }catch (Exception ex){
            throw new IllegalArgumentException(ex);
        }

        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), pocketGetter.getRefNo(), associationResponse);
    }

//    @Override
//    public ResponseDto addAssociation(Account entity, Set<PocketUpdateDto> associationsUpdateDto) {
//        return null;
//    }

    @Override
    public boolean removeAssociation(Account entity, Models entityModel, String refNo) {
        if (ValidateInputUtils.isValidInput(entity, entity.getPockets())) {
            Optional<Pocket> pocketOptional = getEntity(refNo);
            if (pocketOptional.isEmpty()) {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("the subpocket with given reference number : %s doesn't exist", refNo)));
            }
            Pocket pocket = pocketOptional.get();
            Long accountId = pocketDAO.checkAccountAssociation(pocket);
            if (accountId != null) {
                if (Objects.equals(accountId, entity.getId())) {
                    entity.getPockets().remove(pocket);
                }else {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this pocket is already present in another account!!"));
                }
            }else {
                throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                        Map.of("error", "this pocket is bot present in any account!!"));
            }
            return true;
        }
        return false;
    }

    @Override
    public ResponseDto removeAssociation(Object entity, Models entityModel, Set<String> refNos) {
        ResponseDto entityResponse = ValidateInputUtils.validateEntity(entity, PocketGetter.class);
        if (entityResponse != null) {
            return entityResponse;
        }
        PocketGetter pocketGetter = (PocketGetter) entity;
        AssociationResponse associationResponse = new AssociationResponse();
        for (String refNo: refNos){
            Optional<Pocket> pocketOptional = getEntity(refNo);
            if (pocketOptional.isPresent()){
                Pocket pocket = pocketOptional.get();
                if (pocketGetter.getPockets() == null){
                    pocketGetter.setPockets(new HashSet<>());
                }
                if (pocketGetter.getPockets().contains(pocket)){
                    pocketGetter.getPockets().remove(pocketOptional.get());
                    associationResponse.getSuccess().put(refNo, "was removed successfully");
                }else {
                    associationResponse.getError().put(refNo, "this pocketGetter doesn't contain this pocket");
                }
            }else {
                associationResponse.getError().put(refNo, "no pocket corresponds to this ref no");
            }
        }
        return ResponseDtoBuilder.getUpdateResponse(entityModel.name(), pocketGetter.getRefNo(), associationResponse);
    }

    @Override
    public ResponseDto removeDtoAssociation(Object entity, Models entityModel, Set<?> associationsUpdateDto) {
        ResponseDto errorResponse = ValidateInputUtils.validateWildCardSet(associationsUpdateDto, PocketUpdateDto.class);
        if (errorResponse != null) {
            return errorResponse;
        }
        try {
            Set<Pocket> pockets = new HashSet<>();
            for (Object obj : associationsUpdateDto) {
                PocketUpdateDto pocketUpdateDto = (PocketUpdateDto) obj;
                Optional<Pocket> pocketOptional = getEntity(pocketUpdateDto.getRefNo());
                if (pocketOptional.isPresent())
                    pockets.add(pocketOptional.get());
                else {
                    pockets.add(pocketMapper.reqEntityToEntity(pocketUpdateDto));
                }
            }
        }catch (Exception ex){
            return ResponseDtoBuilder.getErrorResponse(810, "error processing your request");
        }
        return null;
    }


//    @Override
//    public ResponseDto removeAssociation(Account entity, Set<PocketUpdateDto> associationsUpdateDto) {
//        return null;
//    }

    @Override
    public void updateAssociation(Account entity, AccountUpdateDto entityUpdateDto) {
        if (ValidateInputUtils.isValidInput(entity.getPockets(), entityUpdateDto.getPockets())) {
            Set<Pocket> existingPockets = entity.getPockets();
            Set<PocketUpdateDto> pocketUpdateDtos = entityUpdateDto.getPockets();
            //new pocket will contain only the new pocket ie the one not included with refs
            Set<Pocket> newPockets = getPockets(pocketUpdateDtos, existingPockets);
            //remove non existent in the new collection
            Set<Pocket> removedPockets = new HashSet<>(existingPockets);
            List<String> newListRefs = pocketUpdateDtos.stream().map(PocketUpdateDto::getRefNo).toList();
            removedPockets.removeIf(pocket -> newListRefs.contains(pocket.getRefNo()));
            entity.getPockets().removeAll(removedPockets);
            entity.getPockets().addAll(newPockets);
        }
    }
    private Set<Pocket> getPockets (Set<PocketUpdateDto> pocketUpdateDtos, Set < Pocket > existingPockets){
        Set<Pocket> addedSubCategories = new HashSet<>();
        pocketUpdateDtos.forEach(newPocket -> {
            if (newPocket.getRefNo() != null) {
                Pocket existingPocket = findExistingEntity(existingPockets, newPocket.getRefNo());
                if (existingPocket != null) {
                    pocketMapper.update(existingPocket, newPocket);
                    existingPocket.setUpdatedAt(LocalDateTime.now());
                }
            } else {
                //if id doesn't have id add it
                addedSubCategories.add(pocketMapper.reqEntityToEntity(newPocket));
            }
        });
        return addedSubCategories;
    }

    private Pocket findExistingEntity (Set < Pocket > existingPockets, String refNo){
        return existingPockets.stream().filter(pocket -> pocket.getRefNo().equals(refNo)).findFirst().orElse(null);
    }
}
