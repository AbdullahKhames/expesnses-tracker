package name.expenses.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.models.AmountType;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PocketAmountRespDto {
    private PocketRespDto pocket;
    private Double amount;
    private AmountType amountType;
}
