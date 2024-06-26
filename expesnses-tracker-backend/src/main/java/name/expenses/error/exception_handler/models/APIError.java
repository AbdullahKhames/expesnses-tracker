package name.expenses.error.exception_handler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "api_error")
public class APIError {
    @Id
    @Column(name = "ERROR_CODE")
    private String errorCode;
    @Column(name = "ERROR_DESCRIPTION")
    private String errorDescription;
}