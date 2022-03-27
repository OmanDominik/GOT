package internetowy_przewodnik_got.service;

import internetowy_przewodnik_got.model.*;
import internetowy_przewodnik_got.model.repositories.MountainPointRepo;
import internetowy_przewodnik_got.model.repositories.TrailRepo;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @InjectMocks
    RouteService routeService;

    @Mock
    TrailRepo trailRepo;

    @Mock
    MountainPointRepo mountainPointRepo;

    @Test
    void createTrailGraphAndFillWithVertices_MountainPointRepoReturnsAFewPoints() {
        var points = List.of(
                new MountainPoint("point1", null, 1100, Set.of()),
                new MountainPoint("point2", null, 2300, Set.of()),
                new MountainPoint("point3", null, 2400, Set.of()));

        when(mountainPointRepo.findAll()).thenReturn(points);

        DirectedWeightedMultigraph graph = new DirectedWeightedMultigraph(Trail.class);
        points.forEach(p -> graph.addVertex(p));

        DirectedWeightedMultigraph result = routeService.createTrailGraphAndFillWithVertices();

        assertEquals(graph, result);
    }

    @Test
    void createTrailGraphAndFillWithVertices_MountainPointRepoReturnsEmptyList() {

        when(mountainPointRepo.findAll()).thenReturn(List.of());

        DirectedWeightedMultigraph graph = new DirectedWeightedMultigraph(Trail.class);

        DirectedWeightedMultigraph result = routeService.createTrailGraphAndFillWithVertices();

        assertEquals(graph, result);
    }


    @Test
    void addEdgesWithTimeWeight() {
        var mp1 = new MountainPoint("point1", null, 1100, Set.of());
        var mp2 = new MountainPoint("point2", null, 2300, Set.of());
        var mp3 = new MountainPoint("point3", null, 2400, Set.of());
        var mp4 = new MountainPoint("point4", null, 2400, Set.of());
        var mp5 = new MountainPoint("point5", null, 2400, Set.of());
        var mp6 = new MountainPoint("point6", null, 2400, Set.of());

        var points = List.of(mp1, mp2, mp3, mp4, mp5, mp6);
        when(mountainPointRepo.findAll()).thenReturn(points);


        DirectedWeightedMultigraph graph = routeService.createTrailGraphAndFillWithVertices();
        int beforeInsert = graph.edgeSet().size();

        List<Trail> trails = List.of(
                new Trail(new MountainSegment(new MountainSegmentId(mp1, mp2)), Color.BLACK, 5, 3,
                        false, 30, new MountainRange()),
                new Trail(new MountainSegment(new MountainSegmentId(mp3, mp2)), Color.RED, 5, 3,
                        false, 30, new MountainRange()),
                new Trail(new MountainSegment(new MountainSegmentId(mp5, mp6)), Color.GREEN, 5, 3,
                        true, 30, new MountainRange()),
                new Trail(new MountainSegment(new MountainSegmentId(mp1, mp6)), Color.YELLOW, 5, 3,
                        true, 30, new MountainRange()));

        when(trailRepo.findAll()).thenReturn(trails);
        routeService.addEdgesWithTimeWeight(graph);
        int afterInsert = graph.edgeSet().size();


        assertEquals(beforeInsert + 6, afterInsert);
    }



    @Test
    void addEdgesWithTimeWeight_NoExistingTrailsInRepo() {
        var mp1 = new MountainPoint("point1", null, 1100, Set.of());
        var mp2 = new MountainPoint("point2", null, 2300, Set.of());
        var mp3 = new MountainPoint("point3", null, 2400, Set.of());
        var mp4 = new MountainPoint("point4", null, 2400, Set.of());
        var mp5 = new MountainPoint("point5", null, 2400, Set.of());
        var mp6 = new MountainPoint("point6", null, 2400, Set.of());

        var points = List.of(mp1, mp2, mp3, mp4, mp5, mp6);
        when(mountainPointRepo.findAll()).thenReturn(points);


        DirectedWeightedMultigraph graph = routeService.createTrailGraphAndFillWithVertices();
        int beforeInsert = graph.edgeSet().size();

        List<Trail> trails = List.of();

        when(trailRepo.findAll()).thenReturn(trails);
        routeService.addEdgesWithTimeWeight(graph);
        int afterInsert = graph.edgeSet().size();


        assertEquals(beforeInsert, afterInsert);
    }

    @Test
    void addEdgesWithTimeWeight_TrailsWithPointsWhichNotInRepo() {
        var mp1 = new MountainPoint("point1", null, 1100, Set.of());
        var mp2 = new MountainPoint("point2", null, 2300, Set.of());
        var mp3 = new MountainPoint("point3", null, 2400, Set.of());
        var mp4 = new MountainPoint("point4", null, 2400, Set.of());
        var mp5 = new MountainPoint("point5", null, 2400, Set.of());
        var mp6 = new MountainPoint("point6", null, 2400, Set.of()); // not in repo

        var points = List.of(mp1, mp2, mp3, mp4, mp5);
        when(mountainPointRepo.findAll()).thenReturn(points);


        DirectedWeightedMultigraph graph = routeService.createTrailGraphAndFillWithVertices();
        int beforeInsert = graph.edgeSet().size();

        List<Trail> trails = List.of(
                new Trail(new MountainSegment(new MountainSegmentId(mp1, mp2)), Color.BLACK, 5, 3,
                        false, 30, new MountainRange()),
                new Trail(new MountainSegment(new MountainSegmentId(mp3, mp2)), Color.RED, 5, 3,
                        false, 30, new MountainRange()),
                new Trail(new MountainSegment(new MountainSegmentId(mp5, mp6)), Color.GREEN, 5, 3,
                        true, 30, new MountainRange()),
                new Trail(new MountainSegment(new MountainSegmentId(mp1, mp6)), Color.YELLOW, 5, 3,
                        true, 30, new MountainRange()));

        when(trailRepo.findAll()).thenReturn(trails);
        routeService.addEdgesWithTimeWeight(graph);
        int afterInsert = graph.edgeSet().size();


        assertEquals(beforeInsert + 4, afterInsert);
    }

    @Test
    void generateShortestTimeRoute_WithoutIntermediatePoints_RouteExists() {
        var mp1 = new MountainPoint("point1", null, 1100, Set.of());
        var mp2 = new MountainPoint("point2", null, 2300, Set.of());
        var mp3 = new MountainPoint("point3", null, 2400, Set.of());
        var mp4 = new MountainPoint("point4", null, 2400, Set.of());
        var mp5 = new MountainPoint("point5", null, 2400, Set.of());
        var mp6 = new MountainPoint("point6", null, 2400, Set.of());

        var points = List.of(mp1, mp2, mp3, mp4, mp5, mp6);
        when(mountainPointRepo.findAll()).thenReturn(points);

        var t1 = new Trail(new MountainSegment(new MountainSegmentId(mp1, mp2)), Color.BLACK, 5, 3,
                false, 30, new MountainRange());
        var t2 = new Trail(new MountainSegment(new MountainSegmentId(mp3, mp2)), Color.RED, 5, 3,
                        false, 30, new MountainRange());
        var t3 = new Trail(new MountainSegment(new MountainSegmentId(mp5, mp6)), Color.GREEN, 5, 3,
                        true, 30, new MountainRange());
        var t4 = new Trail(new MountainSegment(new MountainSegmentId(mp1, mp6)), Color.YELLOW, 5, 3,
                        true, 30, new MountainRange());
        var t5 = new Trail(new MountainSegment(new MountainSegmentId(mp6, mp4)), Color.YELLOW, 5, 3,
                        true, 30, new MountainRange());
        var t6 = new Trail(new MountainSegment(new MountainSegmentId(mp4, mp5)), Color.YELLOW, 5, 3,
                        true, 30, new MountainRange());

        List<Trail> trails = List.of(t1, t2, t3, t4, t5, t6);

        when(trailRepo.findAll()).thenReturn(trails);
        Route result = routeService.generateShortestTimeRoute(mp1, mp5, List.of(), LocalDateTime.of(2022, 1, 24, 15, 0, 0));

        assertEquals(new Route(LocalDateTime.of(2022, 1, 24, 15, 0, 0),
                List.of(t4, t5, t6)), result);
    }

    @Test
    void generateShortestTimeRoute_WithoutIntermediatePoints_RouteNotFound() {
        var mp1 = new MountainPoint("point1", null, 1100, Set.of());
        var mp2 = new MountainPoint("point2", null, 2300, Set.of());
        var mp3 = new MountainPoint("point3", null, 2400, Set.of());
        var mp4 = new MountainPoint("point4", null, 2400, Set.of());
        var mp5 = new MountainPoint("point5", null, 2400, Set.of());
        var mp6 = new MountainPoint("point6", null, 2400, Set.of());

        var points = List.of(mp1, mp2, mp3, mp4, mp5, mp6);
        when(mountainPointRepo.findAll()).thenReturn(points);

        var t1 = new Trail(new MountainSegment(new MountainSegmentId(mp1, mp2)), Color.BLACK, 5, 3,
                false, 30, new MountainRange());
        var t2 = new Trail(new MountainSegment(new MountainSegmentId(mp3, mp2)), Color.RED, 5, 3,
                false, 30, new MountainRange());
        var t3 = new Trail(new MountainSegment(new MountainSegmentId(mp5, mp6)), Color.GREEN, 5, 3,
                true, 30, new MountainRange());
        var t4 = new Trail(new MountainSegment(new MountainSegmentId(mp1, mp6)), Color.YELLOW, 5, 3,
                true, 30, new MountainRange());
        var t5 = new Trail(new MountainSegment(new MountainSegmentId(mp6, mp4)), Color.YELLOW, 5, 3,
                true, 30, new MountainRange());
        var t6 = new Trail(new MountainSegment(new MountainSegmentId(mp4, mp5)), Color.YELLOW, 5, 3,
                true, 30, new MountainRange());

        List<Trail> trails = List.of(t1, t2, t3, t4, t5, t6);

        when(trailRepo.findAll()).thenReturn(trails);
        Route result = routeService.generateShortestTimeRoute(mp6, mp3, List.of(), LocalDateTime.of(2022, 1, 24, 15, 0, 0));

        assertEquals(null, result);
    }
    
    @Test
    void generateShortestTimeRoute_WithIntermediatePoint_RouteNotExists() {
        var mp1 = new MountainPoint("point1", null, 1100, Set.of());
        var mp2 = new MountainPoint("point2", null, 2300, Set.of());
        var mp3 = new MountainPoint("point3", null, 2400, Set.of());
        var mp4 = new MountainPoint("point4", null, 2400, Set.of());
        var mp5 = new MountainPoint("point5", null, 2400, Set.of());
        var mp6 = new MountainPoint("point6", null, 2400, Set.of());

        var points = List.of(mp1, mp2, mp3, mp4, mp5, mp6);
        when(mountainPointRepo.findAll()).thenReturn(points);

        var t1 = new Trail(new MountainSegment(new MountainSegmentId(mp1, mp2)), Color.BLACK, 5, 3,
                false, 30, null);
        var t2 = new Trail(new MountainSegment(new MountainSegmentId(mp3, mp2)), Color.RED, 5, 3,
                false, 30, null);
        var t3 = new Trail(new MountainSegment(new MountainSegmentId(mp5, mp6)), Color.GREEN, 5, 3,
                true, 30, null);
        var t4 = new Trail(new MountainSegment(new MountainSegmentId(mp1, mp6)), Color.YELLOW, 5, 3,
                true, 30, null);
        var t5 = new Trail(new MountainSegment(new MountainSegmentId(mp6, mp4)), Color.YELLOW, 5, 3,
                true, 30, null);
        var t6 = new Trail(new MountainSegment(new MountainSegmentId(mp4, mp5)), Color.YELLOW, 5, 3,
                true, 30, null);

        List<Trail> trails = List.of(t1, t2, t3, t4, t5, t6);

        when(trailRepo.findAll()).thenReturn(trails);
        Route result = routeService.generateShortestTimeRoute(mp6, mp1, List.of(mp5), LocalDateTime.of(2022, 1, 24, 15, 0, 0));

        assertEquals(null, result);
    }


}