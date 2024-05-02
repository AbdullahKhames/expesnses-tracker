package name.expenses.features.pocket_transfer.service;

import name.expenses.features.association.UpdateAssociation;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket_transfer.dtos.request.PocketAmountUpdateDto;
import name.expenses.features.pocket_transfer.models.PocketAmount;

public interface PocketAmountService extends UpdateAssociation<PocketAmount, PocketAmountUpdateDto> {
    void updateOrResetAmount(PocketAmount entity, Pocket pocket, boolean reversed, double diff);
}
