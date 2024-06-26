package name.expenses.features.category.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class CategoryUpdateDto {
    @NotNull
    private String name;
    private String details;

    private String refNo;
    @Builder.Default
//    @Valid
    private Set<SubCategoryUpdateDto> subCategories = new HashSet<>();
}
