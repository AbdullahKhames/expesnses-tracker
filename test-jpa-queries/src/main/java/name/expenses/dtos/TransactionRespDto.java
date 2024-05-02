package name.expenses.dtos;

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
public class TransactionRespDto {
    private String name;
    private String details;
    private Double amount;
    private ExpenseRespDto expense;
    @Builder.Default
    private Set<PocketAmountRespDto> pocketAmounts = new HashSet<>();
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
