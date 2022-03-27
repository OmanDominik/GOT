package internetowy_przewodnik_got.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@Table(name = "Employees_PTTK")
public class EmployeePTTK {
    @Id
    @Email
    private String email;
    @NotBlank
    private String password;
}
