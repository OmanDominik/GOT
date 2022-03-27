package internetowy_przewodnik_got.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Mountain_Ranges")
@NoArgsConstructor
@Getter
@Setter
public class MountainRange {
    @Id
    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "mountainRanges")
    @JsonBackReference
    private Set<MountainPoint> mountainPoints;

    @OneToMany(mappedBy = "mountainRange")
    @JsonIgnore
    private Set<Trail> trails;

    @ManyToOne
    @NotNull
    private MountainChain mountainChain;
}
