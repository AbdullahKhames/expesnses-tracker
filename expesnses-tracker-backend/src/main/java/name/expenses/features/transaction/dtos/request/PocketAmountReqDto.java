package name.expenses.features.transaction.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class PocketAmountReqDto {
    @NotNull
    @NotBlank
    private String refNo;
    @NotNull
    @Min(0)
    private Double amount;
}
