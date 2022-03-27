package internetowy_przewodnik_got.service;

import com.google.common.collect.Collections2;
import internetowy_przewodnik_got.model.*;
import internetowy_przewodnik_got.model.repositories.MountainPointRepo;
import internetowy_przewodnik_got.model.repositories.TrailRepo;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Class that mainly enables route generation
 */
@Service
public class RouteService {

    private final MountainPointRepo mountainPointRepo;
    private final TrailRepo trailRepo;
    private final Logger logger = LoggerFactory.getLogger(RouteService.class);

    /**
     * Creates Route Service
     * @param mountainPointRepo repository of mountain points
     * @param trailRepo repository of trails
     */
    @Autowired
    public RouteService(MountainPointRepo mountainPointRepo, TrailRepo trailRepo) {
        this.mountainPointRepo = mountainPointRepo;
        this.trailRepo = trailRepo;
    }

    /**
     * Generates routes based on the given parameters
     * @param startingPointName starting point of the route
     * @param endPointName end point of the route
     * @param intermediatePointsNames possible intermediate points of the route
     * @param plannedStart planned date of passing the route
     * @return generated two routes that match the arguments(the shortest, and the alternative one)
     */
    public Optional<List<Route>> generateRoutes(String startingPointName, String endPointName, Collection<String> intermediatePointsNames,
                                      LocalDateTime plannedStart) {

        MountainPoint startingPoint = mountainPointRepo.getById(startingPointName);
        MountainPoint endPoint = mountainPointRepo.getById(endPointName);
        List<MountainPoint> intermediatePoints = mountainPointRepo.findAllById(intermediatePointsNames);

        Route shortestTimeRoute = generateShortestTimeRoute(startingPoint, endPoint, intermediatePoints, plannedStart);
        Route alternativeRoute = generateAlternativeRoute(startingPoint, endPoint, intermediatePoints,
                plannedStart);
        if(shortestTimeRoute == null || alternativeRoute == null)
            return Optional.empty();
        logger.info("[Generated]: Shortest path: " + shortestTimeRoute);
        logger.info("[Generated]: Alternative path 1: " + alternativeRoute);
        return Optional.of(List.of(shortestTimeRoute, alternativeRoute));
    }

    /**
     * Generates route with the shortest estimated time which meets the conditions(below)
     * @param startingPoint starting point of the route
     * @param endPoint end point of the route
     * @param intermediatePoints possible intermediate points of the route
     * @param plannedStart planned date of passing the route
     * @return generated route
     */
    public Route generateShortestTimeRoute(MountainPoint startingPoint, MountainPoint endPoint,
                                           List<MountainPoint> intermediatePoints, LocalDateTime plannedStart) {
        // creating graph
        DirectedWeightedMultigraph<MountainPoint, Trail> trailGraph = createTrailGraphAndFillWithVertices();
        addEdgesWithTimeWeight(trailGraph);
        List<Trail> scheduledTrails = findScheduledTrails(trailGraph, startingPoint, endPoint, intermediatePoints);
        if (scheduledTrails == null)
            return null;
        return new Route(plannedStart, scheduledTrails);
    }

    /**
     * Generates alternative route(usually allows to get more points)
     * @param startingPoint starting point of the route
     * @param endPoint end point of the route
     * @param intermediatePoints possible intermediate points of the route
     * @param plannedStart planned date of passing the route
     * @return generated route
     */
    public Route generateAlternativeRoute(MountainPoint startingPoint, MountainPoint endPoint, List<MountainPoint> intermediatePoints,
                                          LocalDateTime plannedStart) {
        // creating graph
        DirectedWeightedMultigraph<MountainPoint, Trail> trailGraph = createTrailGraphAndFillWithVertices();
        addEdgesWithPointsWeight(trailGraph);
        List<Trail> scheduledTrails = findScheduledTrails(trailGraph, startingPoint, endPoint, intermediatePoints);
        if (scheduledTrails == null)
            return null;
        return new Route(plannedStart, scheduledTrails);
    }

    /**
     * Creates a graph which vertices are all mountain points available in db
     * @return created graph
     */
    DirectedWeightedMultigraph createTrailGraphAndFillWithVertices() {
        DirectedWeightedMultigraph<MountainPoint, Trail> graph = new DirectedWeightedMultigraph<>(Trail.class);
        mountainPointRepo.findAll().forEach(mp -> graph.addVertex(mp));
        return graph;
    }

    /**
     * Adds edges with weight(estimated time of covering the distance) to graph
     * @param graph the graph to which edges will be added
     */
    void addEdgesWithTimeWeight(Graph graph) {
        if(graph == null)
            throw new IllegalArgumentException("Tried to insert edges to null graph.");
        trailRepo.findAll().forEach(t -> {
            var compositeId = t.getMountainSegment().getCompositeId();
            var startingPoint = compositeId.getStartingPoint();
            var endPoint = compositeId.getEndPoint();
            try {
                graph.addEdge(startingPoint, endPoint, t);
                graph.setEdgeWeight(t, t.getEstimatedTime());
            } catch(IllegalArgumentException e) {

            }

            if(!t.getOneWay()) {
                var mountainSegment = t.getMountainSegment();
                var reversedStartingPoint = t.getMountainSegment().getCompositeId().getStartingPoint();
                var reversedEndPoint = t.getMountainSegment().getCompositeId().getEndPoint();
                Trail reversedWayTrail = new Trail(new MountainSegment(new MountainSegmentId(reversedEndPoint, reversedStartingPoint),
                        -1 * mountainSegment.getHeightDifference()), t.getColor(), t.getPointsForDescent(),
                        t.getPointsForReaching(), t.getOneWay(), t.getEstimatedTime(), t.getMountainRange());

                graph.addEdge(reversedEndPoint, reversedStartingPoint, reversedWayTrail);
                graph.setEdgeWeight(reversedWayTrail, reversedWayTrail.getEstimatedTime());
            }

        });
    }

    /**
     * Adds edges with weight(points to earn by covering the distance) to graph
     * @param graph the graph to which edges will be added
     */
    private void addEdgesWithPointsWeight(Graph graph) {
        trailRepo.findAll().forEach(t -> {
            var compositeId = t.getMountainSegment().getCompositeId();
            var startingPoint = compositeId.getStartingPoint();
            var endPoint = compositeId.getEndPoint();
            graph.addEdge(startingPoint, endPoint, t);
            graph.setEdgeWeight(t, t.getPointsForReaching());

            if(!t.getOneWay()) {
                var mountainSegment = t.getMountainSegment();
                var reversedStartingPoint = t.getMountainSegment().getCompositeId().getStartingPoint();
                var reversedEndPoint = t.getMountainSegment().getCompositeId().getEndPoint();
                Trail reversedWayTrail = new Trail(new MountainSegment(new MountainSegmentId(reversedEndPoint, reversedStartingPoint),
                        -1 * mountainSegment.getHeightDifference()), t.getColor(), t.getPointsForDescent(),
                        t.getPointsForReaching(), t.getOneWay(), t.getEstimatedTime(), t.getMountainRange());

                graph.addEdge(reversedEndPoint, reversedStartingPoint, reversedWayTrail);
                graph.setEdgeWeight(reversedWayTrail, mapPointsForReaching(reversedWayTrail.getPointsForReaching()));
            }

        });
    }

    /**
     * Maps points for reaching to prepare a graph for route generation
     * @param pointsForReaching number of points to earn for covering the trail from starting to ending point
     * @return mapped value
     */
    private Integer mapPointsForReaching(Integer pointsForReaching) {
        return TrailService.MAXIMAL_POINTS_TO_REACH - pointsForReaching;
    }

    /**
     * Finds list of trails ordered by the order in which they will be traversed
     * @param graph graph in which the route will be searched
     * @param startingPoint starting point of the route
     * @param endPoint end point of the route
     * @param intermediatePoints possible intermediate points of the route
     * @return List of trails ordered by the order in which they will be traversed
     */
    private List<Trail> findScheduledTrails(Graph graph, MountainPoint startingPoint, MountainPoint endPoint, List<MountainPoint> intermediatePoints) {
        DijkstraShortestPath<MountainPoint, Trail> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<MountainPoint, Trail> paths = dijkstraShortestPath.getPaths(startingPoint);

        if(intermediatePoints.isEmpty()) {
            if(paths.getPath(endPoint) == null)
                return null;
            else
                return paths.getPath(endPoint).getEdgeList();
        }

        // checking possible permutations https://www.baeldung.com/java-combinations-algorithm
        Collection<List<MountainPoint>> permutations = Collections2.permutations(intermediatePoints);

        Double shortestRouteTime = Double.MAX_VALUE;
        List<MountainPoint> bestIntermediatePointsPermutation = null;

        for (List<MountainPoint> permutation: permutations) {
            Double permutationRouteTime = 0.0;
            permutationRouteTime += paths.getWeight(permutation.get(0)); // time of travelling from starting point to first intermediate one
            for(int i = 0; i < permutation.size() - 1; i++) {
                paths = dijkstraShortestPath.getPaths(permutation.get(i));
                permutationRouteTime += paths.getWeight(permutation.get(i + 1));
            }
            // time of travelling from last intermediate one end point
            paths = dijkstraShortestPath.getPaths(permutation.get(permutation.size() - 1));
            permutationRouteTime += paths.getWeight(endPoint);
            if(permutationRouteTime < shortestRouteTime) {
                bestIntermediatePointsPermutation = permutation;
                shortestRouteTime = permutationRouteTime;
            }
        }
        if(bestIntermediatePointsPermutation == null || bestIntermediatePointsPermutation.isEmpty())
            return null;

        paths = dijkstraShortestPath.getPaths(startingPoint);

        List<Trail> bestRoute = new ArrayList<>();
        bestRoute.addAll(paths.getPath(bestIntermediatePointsPermutation.get(0)).getEdgeList());
        for(int i = 0; i < bestIntermediatePointsPermutation.size() - 1; i++) {
            paths = dijkstraShortestPath.getPaths(bestIntermediatePointsPermutation.get(i));
            bestRoute.addAll(paths.getPath(bestIntermediatePointsPermutation.get(i + 1)).getEdgeList());
        }
        paths = dijkstraShortestPath.getPaths(bestIntermediatePointsPermutation.get(bestIntermediatePointsPermutation.size() - 1));
        bestRoute.addAll(paths.getPath(endPoint).getEdgeList());

        return bestRoute;
    }
}


