package name.expenses.features.budget_transfer.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BudgetTransferRespDto {
    private String name;
    private String details;
    private Double amount;
    private BudgetAmountRespDto senderBudgetAmount;
    @Builder.Default
    private Set<BudgetAmountRespDto> receiverBudgetAmounts = new HashSet<>();
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
