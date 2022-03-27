package internetowy_przewodnik_got.controller;

import internetowy_przewodnik_got.model.Color;
import internetowy_przewodnik_got.model.Trail;
import internetowy_przewodnik_got.model.dto.TrailDTO;
import internetowy_przewodnik_got.service.TrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Class that allows receiving and sending requests about trails
 */
@RestController
public class TrailController {
    private final TrailService service;

    /**
     * Creating trail controller
     * @param service trail service
     */
    @Autowired
    public TrailController(TrailService service) {
        this.service = service;
    }

    /**
     * Handling HTTP GET request of getting all trails
     * @return Response entity containing list of trails with 200 status code.
     */
    @GetMapping("/trails")
    public ResponseEntity<List<TrailDTO>> getAllTrails() {
        return new ResponseEntity<>(service.getAllTrails().stream().map(TrailDTO::new).collect(toList()), HttpStatus.OK);
    }

    /**
     * Handling HTTP GET request of getting trail with given id
     * @param id trail id number
     * @return Response entity containing trails containing given id with 200 status code.
     */
    @GetMapping("/trails/{id}")
    public ResponseEntity<TrailDTO> getAllTrails(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<>(new TrailDTO(service.get(id).get()), HttpStatus.OK);
    }

    /**
     * Handling HTTP GET request of getting trails matching given parameters
     * @param searchPhrase phrase containing a fragment of the name of the starting or end point
     * @param startingPoint trail starting point
     * @param endPoint trail end point
     * @param mountainRange mountain range in which the trail is located
     * @param color color of the trail
     * @param minPoints the minimum number of points to earn for covering the trail
     * @param maxPoints the maximum number of points to earn for covering the trail
     * @param minEstimatedTime minimum estimated time to cover the trail
     * @param maxEstimatedTime maximum estimated time to cover the trail
     * @return trails matching given parameters
     */
    @GetMapping("/trails/filtered")
    public List<TrailDTO> getFilteredTrails(@RequestParam("search") String searchPhrase, @RequestParam("start") String startingPoint,
           @RequestParam("end") String endPoint, @RequestParam("range") String mountainRange, @RequestParam("color") String color,
           @RequestParam("minPoints") Optional<Integer> minPoints, @RequestParam("maxPoints") Optional<Integer> maxPoints,
           @RequestParam("minTime") Optional<Integer> minEstimatedTime, @RequestParam("maxTime") Optional<Integer> maxEstimatedTime) {

        if(color.equals("-"))
            return service.getAllTrails(searchPhrase, startingPoint, endPoint, mountainRange,
                            minPoints.orElse(TrailService.MINIMAL_POINTS_TO_REACH), maxPoints.orElse(TrailService.MAXIMAL_POINTS_TO_REACH),
                            minEstimatedTime.orElse(TrailService.MINIMAL_ESTIMATED_TIME), maxEstimatedTime.orElse(TrailService.MAXIMAL_ESTIMATED_TIME))
                    .stream().map(TrailDTO::new).collect(toList());
        else
            return service.getAllTrails(searchPhrase, startingPoint, endPoint, mountainRange, Color.valueOf(color),
                        minPoints.orElse(TrailService.MINIMAL_POINTS_TO_REACH), maxPoints.orElse(TrailService.MAXIMAL_POINTS_TO_REACH),
                        minEstimatedTime.orElse(TrailService.MINIMAL_ESTIMATED_TIME), maxEstimatedTime.orElse(TrailService.MAXIMAL_ESTIMATED_TIME))
                .stream().map(TrailDTO::new).collect(toList());
    }

    /**
     * Handling HTTP POST request of adding new trail into the database
     * @param startingPointName trail starting point
     * @param endPointName trail end point
     * @param mountainRangeName mountain range in which the trail is located
     * @param color color of the trail
     * @param pointsForReaching number of points to earn for covering the trail from starting to ending point
     * @param pointsForDescent number of points to earn for covering the trail from end to starting point
     * @param estimatedTime estimated time to cover the trail
     * @param oneWay parameter whether the trail is unidirectional
     * @return Response entity containing added trail with 201 status code.
     */
    @PostMapping("/trails")
    public ResponseEntity<TrailDTO> addTrail(@RequestParam("start") String startingPointName, @RequestParam("end") String endPointName,
                                          @RequestParam("range") String mountainRangeName, @RequestParam("color") Color color,
                                          @RequestParam("pointsReaching") Integer pointsForReaching,
                                          @RequestParam("pointsDescent") Optional<Integer> pointsForDescent,
                                          @RequestParam("time") Integer estimatedTime, @RequestParam("oneWay") Boolean oneWay) {
        Trail createdTrail = service.createTrail(startingPointName, endPointName, mountainRangeName, color,
                pointsForReaching, pointsForDescent, estimatedTime, oneWay);

        return new ResponseEntity<>(new TrailDTO(createdTrail), HttpStatus.CREATED);
    }

    /**
     * Handling HTTP PUT request of partially updating trail
     * @param id id number of trail to update
     * @param trail trail containing data needed to update trail
     * @return Response entity containing updated trail with 200 status code.
     */
    @PutMapping("/trails/{id}")
    public ResponseEntity<TrailDTO> updateTrail(@PathVariable(value = "id") Long id, @RequestBody TrailDTO trail) {
        if (service.get(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Trail toUpdate = service.get(id).get();
        return new ResponseEntity<>(new TrailDTO(service.save(service.updateTo(toUpdate, trail))), HttpStatus.OK);
    }
}
