package internetowy_przewodnik_got.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Mountain_Segments")
@NoArgsConstructor
@Getter
@Setter
public class MountainSegment {

    @EmbeddedId
    private MountainSegmentId compositeId;
    @Transient
    private Integer heightDifference;
    @OneToMany(mappedBy = "mountainSegment")
    @JsonIgnore
    private Set<Trail> availableTrails;


    @SuppressWarnings("unused")
    public Integer getHeightDifference() {
        return compositeId.getEndPoint().getMaxASLHeight() - compositeId.getStartingPoint().getMaxASLHeight();
    }

    @SuppressWarnings("unused")
    @JsonIgnore
    public Set<Trail> getAvailableTrails() {
        return availableTrails;
    }

    @SuppressWarnings("unused")
    @JsonIgnore
    public void setAvailableTrails(Set<Trail> availableTrails) {
        this.availableTrails = availableTrails;
    }

    public MountainSegment(MountainSegmentId compositeId, Integer heightDifference) {
        this.compositeId = compositeId;
        this.heightDifference = heightDifference;
    }

    public MountainSegment(MountainSegmentId compositeId) {
        this.compositeId = compositeId;
    }

    @Override
    public String toString() {
        return compositeId.getStartingPoint() + "------->" + compositeId.getEndPoint();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MountainSegment that = (MountainSegment) o;
        return compositeId.equals(that.compositeId) && Objects.equals(heightDifference, that.heightDifference) && availableTrails.equals(that.availableTrails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeId, heightDifference);
    }
}
