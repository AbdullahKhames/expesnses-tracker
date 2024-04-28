package name.expenses.features.user.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidAuthDto {
    @NotBlank(message = "Invalid Phone number: Empty number")
    @NotNull(message = "Invalid Phone number: Number is NULL")
    private String email;

    @NotBlank(message = "Invalid OTP number: Empty number")
    @NotNull(message = "Invalid OTP number: Number is NULL")
    private String otp;
//    @NotBlank(message = "Invalid refNo : Empty ")
//    @NotNull(message = "Invalid refNo :  is NULL")
    private String refNo;
    @NotBlank(message = "Invalid deviceId : Empty ")
    @NotNull(message = "Invalid deviceId :  is NULL")
    private String deviceId;

}