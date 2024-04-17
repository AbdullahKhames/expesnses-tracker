package name.expenses.features.pocket_transfer.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class PocketTransferUpdateDto {
    private String name;
    private String details;
    private String refNo;
    @Min(value = 0)
    private Double amount;
    @Valid
    @Builder.Default
    @NotNull
    private Set<PocketAmountUpdateDto> receiverPocketAmountsUpdateDtos = new HashSet<>();
    @Valid
    @NotNull
    private PocketAmountUpdateDto senderPocketAmountUpdateDto;
}
