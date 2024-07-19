package name.expenses.features.budget_transfer.dtos.response;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.features.budget_transfer.models.AmountType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class BudgetAmountRespDto {
    private BudgetRespDto budget;
    private Double amount;
    private AmountType amountType;
    private String refNo;
}
