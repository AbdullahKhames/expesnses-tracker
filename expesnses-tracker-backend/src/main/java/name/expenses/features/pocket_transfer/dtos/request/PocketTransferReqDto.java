package name.expenses.features.pocket_transfer.dtos.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class PocketTransferReqDto {
    private String name;
    private String details;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Valid
    @Builder.Default
    @NotNull
    private Set<PocketAmountReqDto> receiverPocketAmountsReqDtos = new HashSet<>();
    @Valid
    @NotNull
    private PocketAmountReqDto senderPocketAmountReqDto;
}

