package internetowy_przewodnik_got.controller;

import internetowy_przewodnik_got.model.MountainPoint;
import internetowy_przewodnik_got.model.dto.MountainPointDTO;
import internetowy_przewodnik_got.service.MountainPointService;
import internetowy_przewodnik_got.service.MountainRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Class that allows receiving and sending requests about mountain points
 */
@RestController
public class MountainPointController {
    private final MountainPointService mountainPointService;
    private final MountainRangeService mountainRangeService;

    /**
     * Creating mountain range controller
     * @param mountainPointService mountain point service
     * @param mountainRangeService mountain range service
     */
    @Autowired
    public MountainPointController(MountainPointService mountainPointService, MountainRangeService mountainRangeService) {
        this.mountainPointService = mountainPointService;
        this.mountainRangeService = mountainRangeService;
    }

    /**
     * Handling HTTP GET request of getting all mountain points
     * @return Response entity containing list of mountain points with 200 status code.
     */
    @GetMapping(value = "/points")
    public ResponseEntity<List<MountainPointDTO>> getMountainPoints() {
        return new ResponseEntity<>(mountainPointService.getAllPoints().stream().map(MountainPointDTO::new).collect(toList()), HttpStatus.OK);
    }

    /**
     * Handling HTTP GET request of getting mountain point located in given mountain range
     * @param range mountain range
     * @return Response entity containing list of mountain points located in given mountain range with 200 status code.
     */
    @GetMapping(value = "/points", params = "range")
    public ResponseEntity<List<MountainPointDTO>> getMountainPoints(@RequestParam("range") String range) {
        var mountainRange = mountainRangeService.getRange(range);
        if(mountainRange.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        else {
            return new ResponseEntity<>(mountainPointService.getAllPoints(mountainRange.get()).stream().map(MountainPointDTO::new).collect(toList()), HttpStatus.OK);
        }

    }

    /**
     * Handling HTTP POST request of adding new mountain point into the database
     * @param mountainPoint mountain point to add
     * @return Response entity containing added mountain point with 201 status code.
     */
    @PostMapping("/points")
    public ResponseEntity<MountainPoint> addMountainPoint(@RequestBody MountainPoint mountainPoint) {
        return new ResponseEntity<>(mountainPointService.addMountainPoint(mountainPoint), HttpStatus.CREATED);
    }

    /**
     * Handling HTTP PUT request of partially updating mountain point
     * @param name name of mountain point to update
     * @param newMountainPoint trail containing data needed to update mountain point
     * @return Response entity containing updated mountain point with 200 status code.
     */
    @PutMapping("/points/{name}")
    public ResponseEntity<MountainPoint> updateMountainPoint(@PathVariable(value = "name") String name,
                                                             @RequestBody MountainPoint newMountainPoint) {
        return new ResponseEntity<>(mountainPointService.updateMountainPoint(newMountainPoint, name), HttpStatus.OK);
    }

}
