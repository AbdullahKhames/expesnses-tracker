package name.expenses.features.budget.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.budget.models.BudgetType;
import name.expenses.utils.validators.validatoranootations.EnumNamePattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class BudgetUpdateDto {
    @NotNull
    private String name;
    private String refNo;
    private String details;
    @NotNull
    @EnumNamePattern(regexp = "ENTERTAINMENT|SAVINGS|BILLS|ALLOWANCE|MOM|MISC|DONATION|EXTERNAL")
    private BudgetType budgetType;
    @NotNull
    private Double amount;
}
