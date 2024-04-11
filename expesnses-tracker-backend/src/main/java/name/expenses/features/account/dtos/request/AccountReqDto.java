package name.expenses.features.account.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.models.Pocket;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class AccountReqDto {
    @NotNull
    private String name;
    private String details;
    private Set<PocketReqDto> pockets = new HashSet<>();
}

