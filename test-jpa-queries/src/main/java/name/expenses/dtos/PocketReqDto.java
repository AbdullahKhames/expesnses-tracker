package name.expenses.dtos;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.models.PocketType;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PocketReqDto {
    private String name;
    private String details;
    private Double amount;

    private PocketType pocketType;
    private Long customerId;

    private String accountRefNo;

}

