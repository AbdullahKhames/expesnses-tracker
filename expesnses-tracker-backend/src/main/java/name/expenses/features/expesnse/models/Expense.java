package name.expenses.features.expesnse.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import name.expenses.features.base.models.BaseModel;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper=true)
@Entity
public class Expense extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double amount;
}
