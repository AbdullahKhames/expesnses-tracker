package name.expenses.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class _2auth {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String email;
    private String otp;
    private String refNo = String.valueOf(UUID.randomUUID());
    private boolean expired = false;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(columnDefinition = "TEXT")
    private String token;
    private String deviceId;

    private Date createdAt = new Date();

}