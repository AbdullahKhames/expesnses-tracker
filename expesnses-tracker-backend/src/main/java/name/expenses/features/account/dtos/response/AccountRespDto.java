package name.expenses.features.account.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.globals.CurrentUserReg;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountRespDto implements CurrentUserReg {
    private String name;
    private String details;
    @Builder.Default
    private Set<BudgetRespDto> budgets = new HashSet<>();
    private String refNo;
    private boolean currentCustomerRegistered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
