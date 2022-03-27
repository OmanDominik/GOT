package internetowy_przewodnik_got.model.dto;

import internetowy_przewodnik_got.model.Route;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class RouteDTO {
    private List<TrailDTO> trails;
    private LocalDateTime plannedDateOfStart;
    private LocalDateTime plannedDateOfEnd;
    private Integer estimatedTravelTime;
    private Integer pointsToEarn;

    public RouteDTO(Route route) {
        this.trails = route.getTrails().stream().map(t -> new TrailDTO(t)).collect(Collectors.toList());
        this.plannedDateOfStart = route.getPlannedDateOfStart();
        this.plannedDateOfEnd = route.getPlannedDateOfEnd();
        this.estimatedTravelTime = route.getEstimatedTravelTime();
        this.pointsToEarn = route.getPointsToEarn();
    }
}
