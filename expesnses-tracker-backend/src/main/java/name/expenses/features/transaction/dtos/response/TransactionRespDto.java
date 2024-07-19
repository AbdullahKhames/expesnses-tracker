package name.expenses.features.transaction.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.budget_transfer.dtos.response.BudgetAmountRespDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionRespDto {
    private String name;
    private String details;
    private Double amount;
    private ExpenseRespDto expense;
    @Builder.Default
    private Set<BudgetAmountRespDto> budgetAmounts = new HashSet<>();
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
