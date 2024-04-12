package name.expenses.features.user.dtos.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtpResponseDto {

    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String otp;
    @NotNull
    @NotBlank
    private String refNo;

}