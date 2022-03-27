package internetowy_przewodnik_got.model.repositories;

import internetowy_przewodnik_got.model.Color;
import internetowy_przewodnik_got.model.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrailRepo extends JpaRepository<Trail, Long> {
    @Query("SELECT trail FROM Trail trail WHERE " +
    "(LOWER(trail.mountainSegment.compositeId.startingPoint.name) LIKE CONCAT('%', LOWER(:sp), '%') " +
    "OR LOWER(trail.mountainSegment.compositeId.endPoint.name) LIKE CONCAT('%', LOWER(:sp), '%'))" +
    "AND (trail.mountainSegment.compositeId.startingPoint.name = :sPoint OR :sPoint = '-') " +
    "AND (trail.mountainSegment.compositeId.endPoint.name = :ePoint OR :ePoint = '-') " +
    "AND (trail.mountainRange.name = :range OR :range = '-') AND trail.color = :color AND (trail.pointsForReaching BETWEEN :minP AND :maxP) " +
    "AND (trail.estimatedTime BETWEEN :minTime AND :maxTime) ORDER BY trail.mountainSegment.compositeId.startingPoint")
    List<Trail> findAll(@Param("sp") String searchPhrase, @Param("sPoint") String startingPoint, @Param("ePoint") String endPoint,
                        @Param("range") String mountainRange, @Param("color") Color color, @Param("minP") Integer minPoints,
                        @Param("maxP") Integer maxPoints, @Param("minTime") Integer minEstimatedTime,
                        @Param("maxTime") Integer maxEstimatedTime);

    // witout color parameter
    @Query("SELECT trail FROM Trail trail WHERE " +
            "(LOWER(trail.mountainSegment.compositeId.startingPoint.name) LIKE CONCAT('%', LOWER(:sp), '%') " +
            "OR LOWER(trail.mountainSegment.compositeId.endPoint.name) LIKE CONCAT('%', LOWER(:sp), '%'))" +
            "AND (trail.mountainSegment.compositeId.startingPoint.name = :sPoint OR :sPoint = '-') " +
            "AND (trail.mountainSegment.compositeId.endPoint.name = :ePoint OR :ePoint = '-') " +
            "AND (trail.mountainRange.name = :range OR :range = '-') AND (trail.pointsForReaching BETWEEN :minP AND :maxP) " +
            "AND (trail.estimatedTime BETWEEN :minTime AND :maxTime) ORDER BY trail.mountainSegment.compositeId.startingPoint")
    List<Trail> findAll(@Param("sp") String searchPhrase, @Param("sPoint") String startingPoint, @Param("ePoint") String endPoint,
                        @Param("range") String mountainRange, @Param("minP") Integer minPoints,
                        @Param("maxP") Integer maxPoints, @Param("minTime") Integer minEstimatedTime,
                        @Param("maxTime") Integer maxEstimatedTime);

}
