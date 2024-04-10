package name.expenses.features.expesnse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import name.expenses.features.base.models.BaseModel;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper=true)
@Entity
public class Expense extends BaseModel {
    private String name;
    private double amount;
}
