package internetowy_przewodnik_got.service;

import internetowy_przewodnik_got.model.Color;
import internetowy_przewodnik_got.model.MountainSegment;
import internetowy_przewodnik_got.model.MountainSegmentId;
import internetowy_przewodnik_got.model.Trail;
import internetowy_przewodnik_got.model.dto.TrailDTO;
import internetowy_przewodnik_got.model.repositories.MountainPointRepo;
import internetowy_przewodnik_got.model.repositories.MountainRangeRepo;
import internetowy_przewodnik_got.model.repositories.MountainSegmentRepo;
import internetowy_przewodnik_got.model.repositories.TrailRepo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Class that enables the management of data about trails
 */
@Service
public class TrailService {
    private TrailRepo trailRepo;
    private MountainRangeRepo mountainRangeRepo;
    private MountainPointRepo mountainPointRepo;
    private MountainSegmentRepo mountainSegmentRepo;
    public static final Integer MINIMAL_POINTS_TO_REACH = 0;
    public static final Integer MAXIMAL_POINTS_TO_REACH = 50;
    public static final Integer MINIMAL_ESTIMATED_TIME = 0;
    public static final Integer MAXIMAL_ESTIMATED_TIME = 6000;

    /**
     * Creates Trail Service
     * @param trailRepo repository of trails
     * @param mountainRangeRepo repository of mountain ranges
     * @param mountainPointRepo repository of mountain points
     * @param mountainSegmentRepo repository of mountain segments
     */
    @Autowired
    public TrailService(TrailRepo trailRepo, MountainRangeRepo mountainRangeRepo, MountainPointRepo mountainPointRepo,
                        MountainSegmentRepo mountainSegmentRepo) {
        this.trailRepo = trailRepo;
        this.mountainRangeRepo = mountainRangeRepo;
        this.mountainPointRepo = mountainPointRepo;
        this.mountainSegmentRepo = mountainSegmentRepo;
    }

    /**
     * Gets all trails from repository sorted by trail's starting point name
     * @return Ordered list of trails
     */
    public List<Trail> getAllTrails() {
        return trailRepo.findAll(Sort.by(Sort.Direction.ASC, "mountainSegment.compositeId.startingPoint.name"));
    }

    /**
     * Gets all trails from repository that match the conditions given in the function arguments
     * @param searchPhrase phrase containing a fragment of the name of the starting or end point
     * @param startingPoint trail starting point
     * @param endPoint trail end point
     * @param mountainRange mountain range in which the trail is located
     * @param color color of the trail
     * @param minPoints the minimum number of points to earn for covering the trail
     * @param maxPoints the maximum number of points to earn for covering the trail
     * @param minEstimatedTime minimum estimated time to cover the trail
     * @param maxEstimatedTime maximum estimated time to cover the trail
     * @return list of trails that match the arguments
     */
    public List<Trail> getAllTrails(String searchPhrase, String startingPoint, String endPoint, String mountainRange,
                       Color color, Integer minPoints, Integer maxPoints, Integer minEstimatedTime, Integer maxEstimatedTime) {
        LoggerFactory.getLogger(TrailService.class).info("[Searching for]: " + searchPhrase + " " + startingPoint
                + " " + endPoint + " " + mountainRange + " " + color  + " " + minPoints  + " " +
                maxPoints  + " " + minEstimatedTime + " " + maxEstimatedTime);
        return trailRepo.findAll(searchPhrase, startingPoint, endPoint, mountainRange, color, minPoints, maxPoints, minEstimatedTime, maxEstimatedTime);
    }

    /**
     * Gets all trails from repository that match the conditions given in the function arguments,
     * works in the same way as the previous method, but has no parameter: trail color
     * @param searchPhrase phrase containing a fragment of the name of the starting or end point
     * @param startingPoint trail starting point
     * @param endPoint trail end point
     * @param mountainRange mountain range in which the trail is located
     * @param minPoints the minimum number of points to earn for covering the trail
     * @param maxPoints the maximum number of points to earn for covering the trail
     * @param minEstimatedTime minimum estimated time to cover the trail
     * @param maxEstimatedTime maximum estimated time to cover the trail
     * @return list of trails that match the arguments
     */
    public List<Trail> getAllTrails(String searchPhrase, String startingPoint, String endPoint, String mountainRange,
                                    Integer minPoints, Integer maxPoints, Integer minEstimatedTime, Integer maxEstimatedTime) {
        LoggerFactory.getLogger(TrailService.class).info("[Searching for]: " + searchPhrase + " " + startingPoint
                + " " + endPoint + " " + mountainRange + " " + minPoints  + " " +
                maxPoints  + " " + minEstimatedTime + " " + maxEstimatedTime);
        return trailRepo.findAll(searchPhrase, startingPoint, endPoint, mountainRange, minPoints, maxPoints, minEstimatedTime, maxEstimatedTime);
    }

    /**
     * Creates a trail and saving it into the database
     * @param startingPointName trail starting point
     * @param endPointName trail end point
     * @param rangeName mountain range in which the trail is located
     * @param color color of the trail
     * @param pointsForReaching number of points to earn for covering the trail from starting to ending point
     * @param pointsForDescent number of points to earn for covering the trail from end to starting point
     * @param estimatedTime estimated time to cover the trail
     * @param oneWay parameter whether the trail is unidirectional
     * @return saving created trail into the database
     */
    public Trail createTrail(String startingPointName, String endPointName, String rangeName, Color color, Integer pointsForReaching,
                               Optional<Integer> pointsForDescent, Integer estimatedTime, Boolean oneWay) {
        // it is certain that points exist
        var startingPoint = mountainPointRepo.findById(startingPointName).get();
        var endPoint = mountainPointRepo.findById(endPointName).get();
        var mountainSegment = mountainSegmentRepo.findById(new MountainSegmentId(startingPoint, endPoint));
        MountainSegment createdMountainSegment;
        if(mountainSegment.isEmpty()) {
            createdMountainSegment = mountainSegmentRepo.save(new MountainSegment(new MountainSegmentId(startingPoint, endPoint)));
        } else {
            createdMountainSegment = mountainSegment.get();
        }
        Integer pointsForDesc = pointsForDescent.isEmpty() ? null : pointsForDescent.get();
        var mountainRange = mountainRangeRepo.getById(rangeName);

        return trailRepo.save(new Trail(createdMountainSegment, color, pointsForReaching,
                pointsForDesc, oneWay, estimatedTime, mountainRange));
    }

    /**
     * Gets a trail with the identifier given in argument
     * @param id trail id number
     * @return trail with the given id number, if exists
     */
    public Optional<Trail> get(Long id) {
        return trailRepo.findById(id);
    }

    /**
     * Saves prepared trail into the database
     * @param trail trail to save
     * @return saved trail
     */
    public Trail addTrail(Trail trail) {
        return trailRepo.save(trail);
    }

    /**
     * Updates trail data
     * @param toUpdate trail to update
     * @param newTrail trail with properties that need to be changed in toUpdate trail
     * @return updated trail
     */
    public Trail updateTo(Trail toUpdate, TrailDTO newTrail) {
        if(toUpdate == null)
            throw new IllegalArgumentException("Trail to update cannot be null.");
        if(newTrail == null)
            return toUpdate;
        if (newTrail.color != null)
            toUpdate.setColor(newTrail.color);
        if(newTrail.estimatedTime != null)
            toUpdate.setEstimatedTime(newTrail.estimatedTime);
        if(newTrail.pointsForReaching != null)
            toUpdate.setPointsForReaching(newTrail.pointsForReaching);
        if(newTrail.pointsForDescent != null)
            toUpdate.setPointsForDescent(newTrail.pointsForDescent);
        if(newTrail.oneWay != null) {
            toUpdate.setOneWay(newTrail.oneWay);
            if(newTrail.oneWay)
                toUpdate.setPointsForDescent(null);
        }

        return toUpdate;
    }

    /**
     * Saves prepared trail(given in argument) into the database
     * @param trail trail to save
     * @return saved trail
     */
    public Trail save(Trail trail) {
        return trailRepo.save(trail);
    }
}
