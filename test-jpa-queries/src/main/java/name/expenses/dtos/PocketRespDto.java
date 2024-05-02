package name.expenses.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.expenses.models.PocketType;


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
