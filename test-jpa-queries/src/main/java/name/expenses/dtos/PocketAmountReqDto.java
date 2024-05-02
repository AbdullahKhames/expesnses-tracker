package name.expenses.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PocketAmountReqDto {

    private String pocketRefNo;

    private Double amount;
}
