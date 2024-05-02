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
public class CustomerRespDto {
    private Long id;
    private UserRespDto user;

    @Builder.Default
    private Set<AccountRespDto> account = new HashSet<>();
    @Builder.Default
    private Set<PocketRespDto> pockets = new HashSet<>();
    @Builder.Default
    private Set<CategoryRespDto> categories = new HashSet<>();
    @Builder.Default
    private Set<SubCategoryRespDto> subCategories = new HashSet<>();
    @Builder.Default
    private Set<ExpenseRespDto> expenses = new HashSet<>();
    @Builder.Default
    private Set<TransactionRespDto> transactions = new HashSet<>();
}
