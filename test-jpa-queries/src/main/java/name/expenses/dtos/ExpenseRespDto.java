package name.expenses.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExpenseRespDto implements CurrentUserReg {
    private String name;
    private double amount;
    private String refNo;
    private String details;
    private CustomerRespDto customer;
    private boolean currentCustomerRegistered;
    private String subCategoryRefNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

