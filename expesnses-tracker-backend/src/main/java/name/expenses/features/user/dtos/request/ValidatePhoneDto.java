package name.expenses.features.user.dtos.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.user.dtos.response.OtpResponseDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidatePhoneDto {
    @Valid
    private OtpResponseDto otpResponseDto;
    private String oldToken;
}