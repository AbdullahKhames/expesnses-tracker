package name.expenses.features.transaction.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class TransactionUpdateDto {
    private String name;
    private String details;
    private String refNo;
    @Min(value = 0)
    private Double amount;
    @Valid
    @Builder.Default
    private Set<BudgetAmountUpdateDto> budgetAmountUpdateDtos = new HashSet<>();
    @Valid
    private ExpenseUpdateDto expense;
}
