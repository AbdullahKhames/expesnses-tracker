package name.expenses.features.transaction.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class TransactionReqDto {
    private String name;
    private String details;
    @Valid
    @Builder.Default
    @NotNull
    private Set<PocketAmountReqDto> pocketAmountReqDtos = new HashSet<>();
    @Valid
    @NotNull
    private ExpenseReqDto expense;
}

