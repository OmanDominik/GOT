package internetowy_przewodnik_got.service;

import internetowy_przewodnik_got.model.MountainSegment;
import internetowy_przewodnik_got.model.repositories.MountainSegmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class that enables the management of data about mountain segments
 */
@Service
public class MountainSegmentService {
    private MountainSegmentRepo mountainSegmentRepo;

    /**
     * Creates Mountain Segment Service
     * @param mountainSegmentRepo repository of mountain segments
     */
    @Autowired
    public MountainSegmentService(MountainSegmentRepo mountainSegmentRepo) {
        this.mountainSegmentRepo = mountainSegmentRepo;
    }

    /**
     * Gets all mountain segments from repository
     * @return list of mountain segments
     */
    public List<MountainSegment> getAll() {
        return mountainSegmentRepo.findAll();
    }

    /**
     * Adds mountain segment into the database
     * @param mountainSegment mountain segment to add
     * @return saved mountain segment
     */
    public MountainSegment addMountainSegment(MountainSegment mountainSegment) {
        return mountainSegmentRepo.save(mountainSegment);
    }
}
