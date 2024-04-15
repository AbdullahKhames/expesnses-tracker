package name.expenses.features.category.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryRespDto {
    private String name;
    @Builder.Default
    private Set<SubCategoryRespDto> subCategories = new HashSet<>();
    private String details;

    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
