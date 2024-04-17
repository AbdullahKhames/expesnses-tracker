package name.expenses.features.pocket_transfer.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PocketTransferRespDto {
    private String name;
    private String details;
    private Double amount;
    private PocketAmountRespDto senderPocketAmount;
    @Builder.Default
    private Set<PocketAmountRespDto> receiverPocketAmounts = new HashSet<>();
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
