package name.expenses.features.pocket_transfer.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class PocketTransferReqDto {
    private String name;
    private String details;
    @Valid
    @Builder.Default
    @NotNull
    private Set<PocketAmountReqDto> receiverPocketAmountsDtos = new HashSet<>();
    @Valid
    @NotNull
    private PocketAmountReqDto senderPocketAmount;
}

