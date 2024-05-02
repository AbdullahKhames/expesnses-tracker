package name.expenses.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubCategoryReqDto {
    private String name;
    private String details;
    private String categoryRefNo;
}
