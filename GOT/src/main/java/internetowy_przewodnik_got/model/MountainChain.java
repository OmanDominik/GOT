package internetowy_przewodnik_got.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "Mountain_Chains")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MountainChain {
    @Id
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "mountainChain")
    private Set<MountainRange> mountainRanges;

    @JsonBackReference
    @SuppressWarnings("unused")
    public Set<MountainRange> getMountainRanges() {
        return mountainRanges;
    }
}
