package name.expenses.features.user.dtos.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRespDto {
    private String name;
    private int age;
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}