package name.expenses.features.expesnse.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExpenseReqDto {
    private String name;
    private Double amount;
}
