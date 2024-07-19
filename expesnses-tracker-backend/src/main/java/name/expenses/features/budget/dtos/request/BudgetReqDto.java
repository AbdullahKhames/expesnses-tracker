package name.expenses.features.budget.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@Builder
@Valid
public class BudgetReqDto {
    @NotNull
    private String name;
    private String details;
    @NotNull
    private Double amount;
    @NotNull
    @EnumNamePattern(regexp = "ENTERTAINMENT|SAVINGS|BILLS|ALLOWANCE|MOM|MISC|DONATION|EXTERNAL")
    private BudgetType budgetType;
    @NotNull
    private Long customerId;
    @NotNull
    @NotBlank
    private String accountRefNo;

}

