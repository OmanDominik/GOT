package internetowy_przewodnik_got.model.repositories;

import internetowy_przewodnik_got.model.MountainPoint;
import internetowy_przewodnik_got.model.MountainRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MountainPointRepo extends JpaRepository<MountainPoint, String> {
    @Override
    List<MountainPoint> findAllById(Iterable<String> strings);
    List<MountainPoint> findAllByMountainRangesContainingOrderByName(MountainRange mountainRange);
    /*@Query("SELECT mp FROM MountainPoint mp WHERE :rangeName = mp.")
    List<MountainPoint> findAllByMountainRangeName(@Param("rangeName") String rangeName);*/
}
