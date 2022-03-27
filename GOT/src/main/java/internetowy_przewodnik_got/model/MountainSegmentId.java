package internetowy_przewodnik_got.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MountainSegmentId implements Serializable {
    @OneToOne
    private MountainPoint startingPoint;
    @OneToOne
    private MountainPoint endPoint;



}
