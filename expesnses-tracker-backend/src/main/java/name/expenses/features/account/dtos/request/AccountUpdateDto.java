package name.expenses.features.account.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class AccountUpdateDto {
    @NotNull
    private String name;
    private String refNo;
    private String details;
    @Builder.Default
    private Set<PocketUpdateDto> pockets = new HashSet<>();
}
