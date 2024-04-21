package name.expenses.features.sub_category.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.models.Expense;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class SubCategoryUpdateDto {
    @NotNull
    private String name;
    private String refNo;
    private String details;

    @Builder.Default
//    @Valid
    private Set<ExpenseUpdateDto> expenses = new HashSet<>();
}
