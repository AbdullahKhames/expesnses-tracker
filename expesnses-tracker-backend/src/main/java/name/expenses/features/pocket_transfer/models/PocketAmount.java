package name.expenses.features.pocket_transfer.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import name.expenses.features.base.models.BaseModel;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.transaction.models.Transaction;
import org.hibernate.annotations.ColumnDefault;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Valid
@Entity
public class PocketAmount extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private Pocket pocket;
    private Double amount;
    @Column(name = "trans", columnDefinition = "boolean default false")
    private boolean trans = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Transaction transaction;
}
