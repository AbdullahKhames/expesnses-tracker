package name.expenses.features.expesnse.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
public class ExpenseUpdateDto {
    @NotNull
    private String name;
    private Double amount;
}
