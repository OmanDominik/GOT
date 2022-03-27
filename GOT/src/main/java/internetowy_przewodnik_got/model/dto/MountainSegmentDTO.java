package internetowy_przewodnik_got.model.dto;

import internetowy_przewodnik_got.model.MountainSegment;

public class MountainSegmentDTO {
    public MountainPointDTO startingPoint;
    public MountainPointDTO endPoint;
    public Integer heightDifference;

    public MountainSegmentDTO(MountainSegment mountainSegment) {
        this.startingPoint = new MountainPointDTO(mountainSegment.getCompositeId().getStartingPoint());
        this.endPoint = new MountainPointDTO(mountainSegment.getCompositeId().getEndPoint());
        this.heightDifference = mountainSegment.getHeightDifference();
    }
}
