package name.expenses.dtos;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExpenseReqDto {
    private String name;
    private String details;

    private Double amount;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String receiptBase64;
    private Long customerId;

    private String subCategoryRefNo;
}
