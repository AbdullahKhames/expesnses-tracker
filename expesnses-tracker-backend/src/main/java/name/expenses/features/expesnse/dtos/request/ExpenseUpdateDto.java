package name.expenses.features.expesnse.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class ExpenseUpdateDto {
    @NotNull
    private String name;
    private String refNo;
    @Min(value = 1, message = "amount cannot be less than 1")
    @NotNull
    private Double amount;
}
