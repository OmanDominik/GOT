package internetowy_przewodnik_got.controller;

import internetowy_przewodnik_got.model.MountainSegment;
import internetowy_przewodnik_got.model.dto.MountainSegmentDTO;
import internetowy_przewodnik_got.service.MountainSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class MountainSegmentController {
    private MountainSegmentService service;

    @Autowired
    public MountainSegmentController(MountainSegmentService service) {
        this.service = service;
    }

    @GetMapping("/segments")
    public ResponseEntity<List<MountainSegmentDTO>> getAllMountainSegments() {
        return new ResponseEntity<>(service.getAll().stream().map(MountainSegmentDTO::new).collect(toList()), HttpStatus.OK);
    }

    @PostMapping("/segments")
    public ResponseEntity<MountainSegment> addMountainSegment(@RequestBody MountainSegment mountainSegment) {
        return new ResponseEntity<>(service.addMountainSegment(mountainSegment), HttpStatus.CREATED);
    }
}
