package name.expenses.features.pocket.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.models.Account;
import name.expenses.features.pocket.dao.PocketDAO;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.dtos.response.PocketRespDto;
import name.expenses.features.pocket.mappers.PocketMapper;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.service.PocketService;

import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
//@Transactional
public class PocketServiceImpl implements PocketService {
    public static final String POCKET = "Pocket";
    private final PocketDAO pocketDAO;
    private final PocketMapper pocketMapper;



    @Override
    public ResponseDto create(PocketReqDto pocket) {
        Pocket sentPocket = pocketMapper.reqDtoToEntity(pocket);
        Pocket savedPocket = pocketDAO.create(sentPocket);
        log.info("created pocket {}", savedPocket);
        return ResponseDtoBuilder.getCreateResponse(POCKET, savedPocket.getRefNo(), pocketMapper.entityToRespDto(savedPocket));
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
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(POCKET,pocketDAO.delete(refNo));
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
        Page<Pocket> pocketPage = pocketDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);
        Page<PocketRespDto> pocketDtos = pocketMapper.entityToRespDto(pocketPage);
        return ResponseDtoBuilder.getFetchAllResponse(POCKET, pocketDtos);
    }

    @Override
    public boolean addAssociation(Account entity, String refNo) {
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
    public boolean removeAssociation(Account entity, String refNo) {
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
