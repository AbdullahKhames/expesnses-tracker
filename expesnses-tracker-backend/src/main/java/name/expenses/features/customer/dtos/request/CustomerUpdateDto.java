package name.expenses.features.customer.dtos.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.user.dtos.request.UserUpdateDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class CustomerUpdateDto {
    @Valid
    private UserUpdateDto user;
    @Builder.Default
    private Set<String> accountRefs = new HashSet<>();
    @Valid
    @Builder.Default
    private Set<PocketUpdateDto> pockets = new HashSet<>();
    @Builder.Default
    private Set<String> categoriesRefs = new HashSet<>();
    @Builder.Default
    private Set<String> subCategoriesRefs = new HashSet<>();
    @Valid
    @Builder.Default
    private Set<ExpenseUpdateDto> expenses = new HashSet<>();
}
