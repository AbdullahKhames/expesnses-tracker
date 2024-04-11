package name.expenses.features.category.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class CategoryReqDto {
    @NotNull
    private String name;
    @Builder.Default
    @Valid
    private Set<SubCategoryReqDto> subCategories = new HashSet<>();}
