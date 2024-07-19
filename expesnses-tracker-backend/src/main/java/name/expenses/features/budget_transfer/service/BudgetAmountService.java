package name.expenses.features.budget_transfer.service;

import name.expenses.features.association.UpdateAssociation;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;
import name.expenses.features.budget_transfer.models.BudgetAmount;

public interface BudgetAmountService extends UpdateAssociation<BudgetAmount, BudgetAmountUpdateDto> {
    void updateOrResetAmount(BudgetAmount entity, Budget budget, boolean reversed, double diff);
}
