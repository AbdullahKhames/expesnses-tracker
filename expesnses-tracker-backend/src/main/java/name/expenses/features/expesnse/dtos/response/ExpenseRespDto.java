package name.expenses.features.expesnse.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.customer.dtos.response.CustomerRespDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExpenseRespDto {
    private String name;
    private double amount;
    private String refNo;
    private String details;
    private CustomerRespDto customer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

