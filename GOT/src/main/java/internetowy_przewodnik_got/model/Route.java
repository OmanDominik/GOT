package internetowy_przewodnik_got.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Route {
    private List<Trail> trails;
    private LocalDateTime plannedDateOfStart;
    private LocalDateTime plannedDateOfEnd;
    private Integer estimatedTravelTime;
    private Integer pointsToEarn;


    public Route(LocalDateTime plannedDateOfStart, List<Trail> trails) {
        this.trails = trails;
        this.estimatedTravelTime = calcTravelTime();
        this.pointsToEarn = calcPointsToEarn();
        this.plannedDateOfStart = plannedDateOfStart;
        this.plannedDateOfEnd = plannedDateOfStart.plusMinutes(estimatedTravelTime);
    }

    private Integer calcTravelTime() {
        Integer sum = 0;
        for (Trail t: this.trails) {
            sum += t.getEstimatedTime();
        }
        return sum;
    }

    private Integer calcPointsToEarn() {
        Integer sum = 0;
        for (Trail t: this.trails) {
            sum += t.getPointsForReaching();
        }
        return sum;
    }

    @Override
    public String toString() {
        return this.trails + "      " + this.pointsToEarn + "points  " + this.estimatedTravelTime + "mins";
    }
}
