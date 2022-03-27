package internetowy_przewodnik_got.service;

import internetowy_przewodnik_got.model.MountainChain;
import internetowy_przewodnik_got.model.repositories.MountainChainRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class that enables the management of data about mountain chains
 */
@Service
public class MountainChainService {
    private MountainChainRepo mountainChainRepo;

    /**
     * Creates Mountain Chain Service
     * @param mountainChainRepo repository of mountain chains
     */
    @Autowired
    public MountainChainService(MountainChainRepo mountainChainRepo) {
        this.mountainChainRepo = mountainChainRepo;
    }

    /**
     * Gets all mountain chains from repository
     * @return list of mountain chains
     */
    public List<MountainChain> getAllChains() {
        return mountainChainRepo.findAll();
    }

    /**
     * Adds mountain chain into the database
     * @param mountainChain mountain chain to add
     * @return saved mountain chain
     */
    public MountainChain addMountainChain(MountainChain mountainChain) {
        return mountainChainRepo.save(mountainChain);
    }
}
