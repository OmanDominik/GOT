package internetowy_przewodnik_got.controller;

import internetowy_przewodnik_got.model.MountainRange;
import internetowy_przewodnik_got.service.MountainRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Class that allows receiving and sending requests about mountain ranges
 */
@RestController
public class MountainRangeController {
    private MountainRangeService service;

    /**
     * Creating mountain range controller
     * @param service mountain range service
     */
    @Autowired
    public MountainRangeController(MountainRangeService service) {
        this.service = service;
    }

    /**
     * Handling HTTP GET request of getting all mountain ranges
     * @return Response entity containing list of mountain ranges with 200 status code.
     */
    @GetMapping("/ranges")
    public ResponseEntity<List<MountainRange>> getAllRanges() {
        return new ResponseEntity<>(service.getAllRanges(), HttpStatus.OK);
    }

    /**
     * Handling HTTP POST request of adding new mountain range into the database
     * @param mountainRange mountain range to add
     * @return Response entity containing added mountain range with 201 status code.
     */
    @PostMapping("/ranges")
    public ResponseEntity<MountainRange> addMountainRange(@RequestBody MountainRange mountainRange) {
        return new ResponseEntity<>(service.addMountainRange(mountainRange), HttpStatus.CREATED);
    }
}
