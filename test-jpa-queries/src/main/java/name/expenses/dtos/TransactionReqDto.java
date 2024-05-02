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
public class TransactionReqDto {
    private String name;
    private String details;
    @Builder.Default
    private Set<PocketAmountReqDto> pocketAmountReqDtos = new HashSet<>();

    private ExpenseReqDto expense;
}

