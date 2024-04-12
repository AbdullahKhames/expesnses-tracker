package name.expenses.features.user.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDto {
    @NotNull
    @NotBlank
    private String fullName;
    @NotNull
    @NotBlank
    private String email;
    private int age;
    @NotNull
    @NotBlank
    private String password;
    @NotNull
    @NotBlank
    private String deviceId;
    @NotNull
    @NotBlank
    private String role;
    private String verificationToken;
}
