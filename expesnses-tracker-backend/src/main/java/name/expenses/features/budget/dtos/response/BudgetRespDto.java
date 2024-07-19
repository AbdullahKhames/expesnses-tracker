package name.expenses.features.budget.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.budget.models.BudgetType;
import name.expenses.globals.CurrentUserReg;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BudgetRespDto implements CurrentUserReg {
    private String name;
    private String details;
    private Double amount;
    private String accountName;
    private String accountRefNo;
//    private CustomerRespDto customer;
    private String customerName;
    private boolean currentCustomerRegistered;
    private BudgetType budgetType;

    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
