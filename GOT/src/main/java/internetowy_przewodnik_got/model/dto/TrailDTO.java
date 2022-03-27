package internetowy_przewodnik_got.model.dto;

import internetowy_przewodnik_got.model.Color;
import internetowy_przewodnik_got.model.Trail;


public class TrailDTO {
    public Long id;
    public MountainPointDTO startingPoint;
    public MountainPointDTO endPoint;
    public Color color;
    public Integer heightDifference;
    public Integer pointsForReaching;
    public Integer pointsForDescent;
    public Boolean oneWay;
    public Integer estimatedTime;
    public String mountainRange;

    public TrailDTO() {
    }

    public TrailDTO(Trail trail) {
        this.id = trail.getId();
        this.startingPoint = new MountainPointDTO(trail.getMountainSegment().getCompositeId().getStartingPoint());
        this.endPoint = new MountainPointDTO(trail.getMountainSegment().getCompositeId().getEndPoint());
        this.color = trail.getColor();
        this.heightDifference = trail.getMountainSegment().getHeightDifference();
        this.pointsForReaching = trail.getPointsForReaching();
        this.pointsForDescent = trail.getPointsForDescent();
        this.oneWay = trail.getOneWay();
        this.estimatedTime = trail.getEstimatedTime();
        this.mountainRange = trail.getMountainRange().getName();
    }
}
