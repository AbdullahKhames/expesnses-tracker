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
public class PocketUpdateDto {
    private String name;
    private String refNo;
    private String details;

    private PocketType pocketType;
    private Double amount;
}
