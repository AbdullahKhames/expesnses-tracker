package name.expenses.features.sub_category.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubCategoryRespDto {
    private String name;
//    @Builder.Default
//    private Set<ExpenseRespDto> expenses = new HashSet<>();
    private String refNo;
    private String details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double totalSpent;
}
