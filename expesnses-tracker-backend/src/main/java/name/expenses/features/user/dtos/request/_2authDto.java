package name.expenses.features.user.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import name.expenses.features.user.models.Type;
import name.expenses.utils.validators.validatoranootations.EnumNamePattern;

@ToString
@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class _2authDto {
        @NotNull
        private String email;
        @NotNull
        private String deviceId;
        @NotNull
        @EnumNamePattern(regexp = "LOGIN|REGISTER|RESET_ACCOUNT|CHANGE_EMAIL")
        private Type type;
}