package name.expenses.features.user.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    public Role(String roleUser) {
        this.name = roleUser;
    }


}