package name.expenses.features.pocket_transfer.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import name.expenses.features.base.models.BaseModel;
import name.expenses.features.pocket.models.Pocket;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Transaction transaction;
}
