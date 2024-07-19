package name.expenses.features.budget_transfer.dtos.request;

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
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Valid
@Builder
public class BudgetTransferUpdateDto {
    private String name;
    private String details;
    private String refNo;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Min(value = 0)
    private Double amount;
    @Valid
    @Builder.Default
    @NotNull
    private Set<BudgetAmountUpdateDto> receiverBudgetAmountsUpdateDtos = new HashSet<>();
    @Valid
    @NotNull
    private BudgetAmountUpdateDto senderBudgetAmountUpdateDto;
}
