package name.expenses.features.category.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.globals.CurrentUserReg;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryRespDto implements CurrentUserReg {
    private String name;
    @Builder.Default
    private Set<SubCategoryRespDto> subCategories = new HashSet<>();
    private String details;
    private Double totalSpent;
    private String refNo;
    private boolean currentCustomerRegistered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
