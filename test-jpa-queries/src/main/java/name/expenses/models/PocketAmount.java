package name.expenses.models;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
