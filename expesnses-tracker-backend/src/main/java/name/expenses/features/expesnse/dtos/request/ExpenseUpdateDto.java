package name.expenses.features.expesnse.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class ExpenseUpdateDto {
    @NotNull
    private String name;
    private String refNo;
    private String details;
    private String receiptBase64;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Min(value = 1, message = "amount cannot be less than 1")
    @NotNull
    private Double amount;
}
