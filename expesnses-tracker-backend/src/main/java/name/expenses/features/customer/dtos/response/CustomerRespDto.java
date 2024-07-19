package name.expenses.features.customer.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.account.dtos.response.AccountRespDto;
import name.expenses.features.category.dtos.response.CategoryRespDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.features.transaction.dtos.response.TransactionRespDto;
import name.expenses.features.user.dtos.response.UserRespDto;

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
    private Set<BudgetRespDto> budgets = new HashSet<>();
    @Builder.Default
    private Set<CategoryRespDto> categories = new HashSet<>();
    @Builder.Default
    private Set<SubCategoryRespDto> subCategories = new HashSet<>();
    @Builder.Default
    private Set<ExpenseRespDto> expenses = new HashSet<>();
    @Builder.Default
    private Set<TransactionRespDto> transactions = new HashSet<>();
}
