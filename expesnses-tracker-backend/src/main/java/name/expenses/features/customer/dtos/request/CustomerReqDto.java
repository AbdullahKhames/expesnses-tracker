package name.expenses.features.customer.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.user.dtos.request.UserReqDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class CustomerReqDto {
    @Valid
    @NotNull
    private UserReqDto user;
    @Builder.Default
    private Set<String> accountRefs = new HashSet<>();
    @Valid
    @Builder.Default
    private Set<PocketReqDto> pockets = new HashSet<>();
    @Builder.Default
    private Set<String> categoriesRefs = new HashSet<>();
    @Builder.Default
    private Set<String> subCategoriesRefs = new HashSet<>();
    @Valid
    @Builder.Default
    private Set<ExpenseReqDto> expenses = new HashSet<>();
}

