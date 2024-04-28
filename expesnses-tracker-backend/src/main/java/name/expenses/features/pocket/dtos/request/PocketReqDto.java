package name.expenses.features.pocket.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.pocket.models.PocketType;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.utils.validators.validatoranootations.EnumNamePattern;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class PocketReqDto {
    @NotNull
    private String name;
    private String details;
    @NotNull
    private Double amount;
    @NotNull
    @EnumNamePattern(regexp = "ENTERTAINMENT|SAVINGS|BILLS|ALLOWANCE|MOM|MISC|DONATION")
    private PocketType pocketType;
    @NotNull
    private Long customerId;
    @NotNull
    @NotBlank
    private String accountRefNo;

}

