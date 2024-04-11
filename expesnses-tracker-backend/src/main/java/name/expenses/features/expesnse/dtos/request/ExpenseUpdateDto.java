package name.expenses.features.expesnse.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
public class ExpenseUpdateDto {
    private String name;
    @Min(value = 1, message = "amount cannot be less than 1")
    private Double amount;
}
