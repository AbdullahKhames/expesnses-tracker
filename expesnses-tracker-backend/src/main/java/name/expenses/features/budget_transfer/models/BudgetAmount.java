package name.expenses.features.budget_transfer.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import name.expenses.features.base.models.BaseModel;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.transaction.models.Transaction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Valid
@Entity
public class BudgetAmount extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private Budget budget;
    private Double amount;
    @Column(name = "trans", columnDefinition = "boolean default false")
    private boolean trans = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Transaction transaction;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(55) default 'DEBIT'")
    private AmountType amountType = AmountType.DEBIT;
}
