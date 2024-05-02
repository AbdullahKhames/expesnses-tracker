package name.expenses.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionUpdateDto {
    private String name;
    private String details;
    private String refNo;
    private Double amount;
    @Builder.Default
    private Set<PocketAmountUpdateDto> pocketAmountUpdateDtos = new HashSet<>();
    private ExpenseUpdateDto expense;
}
