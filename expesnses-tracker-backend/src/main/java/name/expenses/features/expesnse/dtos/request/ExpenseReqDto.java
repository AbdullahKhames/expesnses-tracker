package name.expenses.features.expesnse.dtos.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class ExpenseReqDto {
    @NotNull
    private String name;
    private String details;
    @NotNull
    @Min(value = 1, message = "amount cannot be less than 1")
    private Double amount;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String receiptBase64;
    @NotNull
    private Long customerId;
    @NotNull
    @NotBlank
    private String subCategoryRefNo;
}
