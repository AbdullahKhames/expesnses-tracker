package name.expenses.features.pocket.dtos.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.features.customer.dtos.response.CustomerRespDto;
import name.expenses.features.pocket.models.PocketType;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.globals.CurrentUserReg;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PocketRespDto implements CurrentUserReg {
    private String name;
    private String details;
    private Double amount;
    private String accountName;
    private String accountRefNo;
//    private CustomerRespDto customer;
    private String customerName;
    private boolean currentCustomerRegistered;
    private PocketType pocketType;

    private String refNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
