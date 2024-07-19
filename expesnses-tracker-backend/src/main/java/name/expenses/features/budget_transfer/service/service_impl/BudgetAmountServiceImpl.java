package name.expenses.features.budget_transfer.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;

import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.budget_transfer.dao.BudgetAmountDAO;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;
import name.expenses.features.budget_transfer.mappers.BudgetAmountMapper;
import name.expenses.features.budget_transfer.models.AmountType;
import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.features.budget_transfer.service.BudgetAmountService;


import java.util.Optional;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class BudgetAmountServiceImpl implements BudgetAmountService {
    private final BudgetAmountMapper budgetAmountMapper;
    private final BudgetService budgetService;
    private final BudgetAmountDAO budgetAmountDAO;
    @Override
    public void updateAssociation(BudgetAmount entity, BudgetAmountUpdateDto entityUpdateDto) {
        if (!entity.getRefNo().equals(entityUpdateDto.getRefNo())){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Optional<Budget> budgetOptional = budgetService.getEntity(entityUpdateDto.getBudgetRefNo());
        if (budgetOptional.isEmpty()){
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Budget budget = budgetOptional.get();
        if (budget.equals(entity.getBudget())){
            handleSamebudgetFlow(entity, entityUpdateDto);
        }else {
            handleDifferrentbudgetFlow(entity, entityUpdateDto, budget);
        }
        budgetAmountDAO.update(entity);
        log.info("updated the Budget amount {}", entity);
    }

    private void handleDifferrentbudgetFlow(BudgetAmount entity, BudgetAmountUpdateDto entityUpdateDto, Budget budget) {
        // reset old Budget amount
        Budget oldbudget = entity.getBudget();
        updateOrResetAmount(entity, oldbudget, true, entity.getAmount());

        //update the Budget amount
        budgetAmountMapper.update(entity, entityUpdateDto);
        entity.setBudget(budget);

        // update the new Budget amount
        updateOrResetAmount(entity, budget, false, entity.getAmount());


    }

    @Override
    public void updateOrResetAmount(BudgetAmount entity, Budget budget, boolean reversed, double diff) {
        if (entity.getAmountType().equals(AmountType.CREDIT)){
            if (reversed){
                budget.setAmount(budget.getAmount() - diff);
            }else {
                budget.setAmount(budget.getAmount() + diff);
            }
        }else if (entity.getAmountType().equals(AmountType.DEBIT)){
            if (reversed){
                budget.setAmount(budget.getAmount() + diff);
            }else {
                budget.setAmount(budget.getAmount() - diff);
            }
        }
        budgetService.update(budget);
    }

    private void handleSamebudgetFlow(BudgetAmount entity, BudgetAmountUpdateDto entityUpdateDto) {
        Budget budget = entity.getBudget();
        double diff = entityUpdateDto.getAmount() - entity.getAmount();
        updateOrResetAmount(entity, budget, false, diff);
        budgetAmountMapper.update(entity, entityUpdateDto);
    }
}
