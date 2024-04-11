package name.expenses.features.pocket.dtos.request;

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
public class PocketUpdateDto {
    @NotNull
    private String name;
    private String refNo;
    private String details;
    @NotNull
    private Double amount;
}
