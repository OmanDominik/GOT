package internetowy_przewodnik_got.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Check;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Mountain_Points")
@Check(constraints = "minASLHeight < maxASLHeight")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class MountainPoint {
    @Id
    @NotBlank
    private String name;
    @Min(1)
    @Max(10000)
    private Integer minASLHeight;
    @NotNull
    @Min(1)
    @Max(10000)
    private Integer maxASLHeight;
    /*@OneToMany(mappedBy = "startingPoint")
    private Set<MountainSegment> segmentsWhichItStarts;
    @OneToMany(mappedBy = "endPoint")
    private Set<MountainSegment> segmentsWhichItEnds;*/
    @ManyToMany
    private Set<MountainRange> mountainRanges;

    @Override
    public String toString() {
        return name;
    }
}
