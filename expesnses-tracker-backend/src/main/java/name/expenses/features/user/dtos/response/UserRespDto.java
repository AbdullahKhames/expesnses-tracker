package name.expenses.features.user.dtos.response;

import lombok.*;
import name.expenses.features.user.models.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRespDto {
    private String fullName;
    private String email;
    private int age;
    private String deviceId;
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}