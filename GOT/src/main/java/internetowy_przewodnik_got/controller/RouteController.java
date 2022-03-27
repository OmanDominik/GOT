package internetowy_przewodnik_got.controller;

import internetowy_przewodnik_got.model.dto.RouteDTO;
import internetowy_przewodnik_got.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that allows receiving and sending requests about routes
 */
@RestController
public class RouteController {
    private RouteService routeService;

    /**
     * Creating route controller
     * @param routeService route service
     */
    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Handling HTTP GET request of getting route based on given parameters
     * @param startingPointName starting point of the route
     * @param endPointName end point of the route
     * @param intermediatePointsNames possible intermediate points of the route
     * @param plannedStart planned date of passing the route
     * @return Response entity containing list of two generated routes with 200 status code,
     *          if those routes don't exist list contains nulls.
     */
    @GetMapping("/generate")
    public ResponseEntity<List<RouteDTO>> getRoutes(@RequestParam("start") String startingPointName, @RequestParam("end") String endPointName,
                                                    @RequestParam("stops") Collection<String> intermediatePointsNames,
                                                    @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime plannedStart) {

        var generatedRoutes = routeService.generateRoutes(startingPointName, endPointName,
                intermediatePointsNames, plannedStart);

        if(generatedRoutes.isPresent())
            return new ResponseEntity<>(generatedRoutes.get().stream()
                    .map(route -> new RouteDTO(route)).collect(Collectors.toList()), HttpStatus.OK);

        ArrayList<RouteDTO> dummyResult = new ArrayList<>(2); dummyResult.add(null); dummyResult.add(null);

        return new ResponseEntity(dummyResult, HttpStatus.OK);
    }
}
