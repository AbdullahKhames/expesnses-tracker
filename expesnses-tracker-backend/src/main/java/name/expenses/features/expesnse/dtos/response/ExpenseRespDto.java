package name.expenses.features.expesnse.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExpenseRespDto {
    private String name;
    private double amount;
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

