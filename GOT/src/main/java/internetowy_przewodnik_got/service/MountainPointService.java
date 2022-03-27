package internetowy_przewodnik_got.service;

import internetowy_przewodnik_got.model.MountainPoint;
import internetowy_przewodnik_got.model.MountainRange;
import internetowy_przewodnik_got.model.repositories.MountainPointRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Class that enables the management of data about mountain points
 */
@Service
public class MountainPointService {
    private final MountainPointRepo mountainPointRepo;

    /**
     * Creates Mountain Point Service
     * @param mountainPointRepo repository of mountain points
     */
    @Autowired
    public MountainPointService(MountainPointRepo mountainPointRepo) {
        this.mountainPointRepo = mountainPointRepo;
    }

    /**
     * Gets all mountain points from repository
     * @return list of mountain points
     */
    public List<MountainPoint> getAllPoints() {
        return mountainPointRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * Gets all mountain points that matches arguments
     * @param mountainRange mountain range in which the point is located
     * @return list of mountain points located in given mountain range
     */
    public List<MountainPoint> getAllPoints(MountainRange mountainRange) {
        return mountainPointRepo.findAllByMountainRangesContainingOrderByName(mountainRange);
    }

    /**
     * Adds mountain point into the database
     * @param mountainPoint mountain point to add
     * @return saved mountain point
     */
    public MountainPoint addMountainPoint(MountainPoint mountainPoint) {
        return mountainPointRepo.save(mountainPoint);
    }

    /**
     * Updates mountain point data
     * @param newPoint mountain point to update
     * @param id mountain point id number
     * @return updated mountain point
     */
    public MountainPoint updateMountainPoint(MountainPoint newPoint, String id) {
        var pointToUpdate = mountainPointRepo.findById(id);
        pointToUpdate.get().setName(newPoint.getName());
        pointToUpdate.get().setMinASLHeight(newPoint.getMinASLHeight());
        pointToUpdate.get().setMaxASLHeight(newPoint.getMaxASLHeight());
        pointToUpdate.get().setMountainRanges(newPoint.getMountainRanges());
        mountainPointRepo.deleteById(id);
        return mountainPointRepo.save(pointToUpdate.get());
    }

    /**
     * Gets a mountain point with the identifier given in argument
     * @param id mountain point id number
     * @return mountain point with the given id number, if exists
     */
    public Optional<MountainPoint> getMountainPoint(String id) {
        return mountainPointRepo.findById(id);
    }
}
