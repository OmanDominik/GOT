package internetowy_przewodnik_got.model.dto;


import internetowy_przewodnik_got.model.MountainPoint;

public class MountainPointDTO {
    public String name;
    public Integer minASLHeight;
    public Integer maxASLHeight;

    public MountainPointDTO(MountainPoint mountainPoint) {
        this.name = mountainPoint.getName();
        this.minASLHeight = mountainPoint.getMinASLHeight();
        this.maxASLHeight = mountainPoint.getMaxASLHeight();
    }
}
