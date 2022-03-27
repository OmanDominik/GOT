package internetowy_przewodnik_got.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Trails")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({@JoinColumn(referencedColumnName = "STARTING_POINT_NAME"), @JoinColumn(referencedColumnName = "END_POINT_NAME")})
    private MountainSegment mountainSegment;

    @NotNull
    private Color color;

    @NotNull
    @Min(1)
    @Max(50)
    private Integer pointsForReaching;

    @Min(1)
    @Max(50)
    private Integer pointsForDescent;

    @NotNull
    private Boolean oneWay = false;

    @Min(1)
    @Max(6000) // 100hrs
    private Integer estimatedTime;

    @ManyToOne
    @JsonBackReference
    private MountainRange mountainRange;

    public Trail(MountainSegment mountainSegment, Color color, Integer pointsForReaching, Integer pointsForDescent,
                 Boolean oneWay, Integer estimatedTime, MountainRange mountainRange) {
        this.mountainSegment = mountainSegment;
        this.color = color;
        this.pointsForReaching = pointsForReaching;
        this.pointsForDescent = pointsForDescent;
        this.oneWay = oneWay;
        this.estimatedTime = estimatedTime;
        this.mountainRange = mountainRange;
    }

    @Override
    public String toString() {
        return this.id + " " + mountainSegment.toString() + ", " + color + " " + this.pointsForReaching;
    }
}
