package internetowy_przewodnik_got.service;

import internetowy_przewodnik_got.model.*;
import internetowy_przewodnik_got.model.dto.TrailDTO;
import internetowy_przewodnik_got.model.repositories.MountainPointRepo;
import internetowy_przewodnik_got.model.repositories.MountainRangeRepo;
import internetowy_przewodnik_got.model.repositories.MountainSegmentRepo;
import internetowy_przewodnik_got.model.repositories.TrailRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TrailServiceTest {

    @InjectMocks
    TrailService trailService;

    @Mock
    TrailRepo trailRepo;
    @Mock
    MountainRangeRepo mountainRangeRepo;
    @Mock
    MountainPointRepo mountainPointRepo;
    @Mock
    MountainSegmentRepo mountainSegmentRepo;

    @Test
    void updateTo() {
        Trail toUpdate = new Trail(new MountainSegment(new MountainSegmentId(new MountainPoint("Startowy Test",
                null, 1100, Set.of()), new MountainPoint("Końcowy Test", null,
                1100, Set.of()))), Color.YELLOW, 5, 5, false,
                30, new MountainRange());

        TrailDTO newTrail = new TrailDTO();
        newTrail.color = Color.BLACK;
        newTrail.estimatedTime = 40;

        Trail result = trailService.updateTo(toUpdate, newTrail);

        assertEquals(newTrail.color, result.getColor());
        assertEquals(toUpdate.getPointsForReaching(), result.getPointsForReaching());
        assertEquals(toUpdate.getPointsForDescent(), result.getPointsForDescent());
        assertEquals(toUpdate.getOneWay(), result.getOneWay());
        assertEquals(newTrail.estimatedTime, result.getEstimatedTime());
    }

    @Test
    void updateTo_NewTrailIsNull() {
        Trail toUpdate = new Trail(new MountainSegment(new MountainSegmentId(new MountainPoint("Startowy Test",
                null, 1100, Set.of()), new MountainPoint("Końcowy Test", null,
                1100, Set.of()))), Color.YELLOW, 5, 5, false,
                30, new MountainRange());

        TrailDTO newTrail = null;

        Trail result = trailService.updateTo(toUpdate, newTrail);

        assertEquals(toUpdate.getColor(), result.getColor());
        assertEquals(toUpdate.getPointsForReaching(), result.getPointsForReaching());
        assertEquals(toUpdate.getPointsForDescent(), result.getPointsForDescent());
        assertEquals(toUpdate.getOneWay(), result.getOneWay());
        assertEquals(toUpdate.getEstimatedTime(), result.getEstimatedTime());
    }

}