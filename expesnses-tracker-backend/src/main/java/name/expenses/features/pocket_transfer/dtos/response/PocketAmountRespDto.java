package name.expenses.features.pocket_transfer.dtos.response;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.pocket.dtos.response.PocketRespDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class PocketAmountRespDto {
    private PocketRespDto pocket;
    private Double amount;
}
