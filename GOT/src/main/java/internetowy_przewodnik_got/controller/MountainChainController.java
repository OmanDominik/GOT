package internetowy_przewodnik_got.controller;

import internetowy_przewodnik_got.model.MountainChain;
import internetowy_przewodnik_got.service.MountainChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Class that allows receiving and sending requests about mountain chains
 */
@RestController
public class MountainChainController {
    private MountainChainService service;

    /**
     * Creating mountain chain controller
     * @param service mountain chain service
     */
    @Autowired
    public MountainChainController(MountainChainService service) {
        this.service = service;
    }

    /**
     * Handling HTTP GET request of getting all mountain chains
     * @return Response entity containing list of mountain chains with 200 status code.
     */
    @GetMapping("/chains")
    public ResponseEntity<List<MountainChain>> getAllChains() {
        return new ResponseEntity<>(service.getAllChains(), HttpStatus.OK);
    }

    /**
     * Handling HTTP POST request of adding new mountain chain into the database
     * @param mountainChain mountain chain to add
     * @return Response entity containing added mountain chain with 201 status code.
     */
    @PostMapping("/chains")
    public ResponseEntity<MountainChain> addMountainChain(@RequestBody MountainChain mountainChain) {
        return new ResponseEntity<>(service.addMountainChain(mountainChain), HttpStatus.CREATED);
    }
}
