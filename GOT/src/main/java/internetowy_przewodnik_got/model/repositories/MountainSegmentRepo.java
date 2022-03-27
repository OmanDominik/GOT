package internetowy_przewodnik_got.model.repositories;

import internetowy_przewodnik_got.model.MountainSegment;
import internetowy_przewodnik_got.model.MountainSegmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MountainSegmentRepo extends JpaRepository<MountainSegment, MountainSegmentId> {
    //public MountainSegment findByCompositeIdIs(String startingPoint, String endPoint);
}
