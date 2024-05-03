package name.expenses.features.pocket_transfer.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.pocket_transfer.dao.PocketAmountDAO;
import name.expenses.features.pocket_transfer.dtos.request.PocketAmountUpdateDto;
import name.expenses.features.pocket_transfer.mappers.PocketAmountMapper;
import name.expenses.features.pocket_transfer.models.AmountType;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.features.pocket_transfer.service.PocketAmountService;

import java.util.Optional;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class PocketAmountServiceImpl implements PocketAmountService {
    private final PocketAmountMapper pocketAmountMapper;
    private final PocketService pocketService;
    private final PocketAmountDAO pocketAmountDAO;
    @Override
    public void updateAssociation(PocketAmount entity, PocketAmountUpdateDto entityUpdateDto) {
        if (!entity.getRefNo().equals(entityUpdateDto.getRefNo())){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Optional<Pocket> pocketOptional = pocketService.getEntity(entityUpdateDto.getPocketRefNo());
        if (pocketOptional.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Pocket pocket = pocketOptional.get();
        if (pocket.equals(entity.getPocket())){
            handleSamePocketFlow(entity, entityUpdateDto);
        }else {
            handleDifferrentPocketFlow(entity, entityUpdateDto, pocket);
        }
        pocketAmountDAO.update(entity);
        log.info("updated the pocket amount {}", entity);
    }

    private void handleDifferrentPocketFlow(PocketAmount entity, PocketAmountUpdateDto entityUpdateDto, Pocket pocket) {
        // reset old pocket amount
        Pocket oldPocket = entity.getPocket();
        updateOrResetAmount(entity, oldPocket, true, entity.getAmount());

        //update the pocket amount
        pocketAmountMapper.update(entity, entityUpdateDto);
        entity.setPocket(pocket);

        // update the new pocket amount
        updateOrResetAmount(entity, pocket, false, entity.getAmount());


    }

    @Override
    public void updateOrResetAmount(PocketAmount entity, Pocket pocket, boolean reversed, double diff) {
        if (entity.getAmountType().equals(AmountType.CREDIT)){
            if (reversed){
                pocket.setAmount(pocket.getAmount() - diff);
            }else {
                pocket.setAmount(pocket.getAmount() + diff);
            }
        }else if (entity.getAmountType().equals(AmountType.DEBIT)){
            if (reversed){
                pocket.setAmount(pocket.getAmount() + diff);
            }else {
                pocket.setAmount(pocket.getAmount() - diff);
            }
        }
        pocketService.update(pocket);
    }

    private void handleSamePocketFlow(PocketAmount entity, PocketAmountUpdateDto entityUpdateDto) {
        Pocket pocket = entity.getPocket();
        double diff = entityUpdateDto.getAmount() - entity.getAmount();
        updateOrResetAmount(entity, pocket, false, diff);
        pocketAmountMapper.update(entity, entityUpdateDto);
    }
}
