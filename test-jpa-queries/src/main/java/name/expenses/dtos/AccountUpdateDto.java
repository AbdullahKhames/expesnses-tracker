package name.expenses.dtos;


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
public class AccountUpdateDto {
    private String name;
    private String refNo;
    private String details;
    @Builder.Default
    private Set<PocketUpdateDto> pockets = new HashSet<>();
}
