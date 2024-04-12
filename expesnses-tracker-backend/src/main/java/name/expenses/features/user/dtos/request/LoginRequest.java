package name.expenses.features.user.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "email Field cannot be Null")
    @NotBlank(message = "email Field cannot be Empty")
    @Size(min = 2 ,message = "email must be 2 chars or more")
    private  String email;
    @Size(min = 4 , message = "password length must be more than 4 or more")
    private String password;
    @NotNull(message = "deviceId Field cannot be Null")
    @NotBlank(message = "deviceId Field cannot be Empty")
    private String deviceId;
    private String token;
}