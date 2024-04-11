package name.expenses.features.pocket.dtos.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PocketRespDto {
    private String name;
    private String details;
    private Double amount;
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
