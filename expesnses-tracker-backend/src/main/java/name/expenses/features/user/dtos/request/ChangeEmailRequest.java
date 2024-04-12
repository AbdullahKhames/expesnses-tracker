package name.expenses.features.user.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.PathParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangeEmailRequest {
    @NotNull
    private String token;
    @NotNull
    private String refNo;
    private String newEmail;
}
