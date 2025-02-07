package pe.edu.vallegrande.vgmsdidacticunit.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import pe.edu.vallegrande.vgmsdidacticunit.application.service.DidacticUnitService;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitCreateDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitIdsDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitUpdateDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.StudyProgram;
import pe.edu.vallegrande.vgmsdidacticunit.domain.model.DidacticUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/common/${api.version}/didactic-unit")
public class DidacticUnitController {

    private final DidacticUnitService didacticUnitService;

    public DidacticUnitController(DidacticUnitService didacticUnitService) {
        this.didacticUnitService = didacticUnitService;
    }

    @GetMapping("/list/active")
    public Flux<DidacticUnitDto> listActive() {
        return didacticUnitService.getByStatus("A");
    }

    @GetMapping("/list/inactive")
    public Flux<DidacticUnitDto> listInactive() {
        return didacticUnitService.getByStatus("I");
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DidacticUnit> create(@RequestBody DidacticUnitCreateDto didacticUnitCreateDto) {
        return didacticUnitService.create(didacticUnitCreateDto);
    }

    @PutMapping("/update/{id}")
    public Mono<DidacticUnit> update(@PathVariable String id, @RequestBody DidacticUnitUpdateDto didacticUnitUpdateDto) {
        return didacticUnitService.update(id, didacticUnitUpdateDto);
    }

    @PutMapping("/activate/{id}")
    public Mono<DidacticUnit> activate(@PathVariable String id) {
        return didacticUnitService.changeStatus(id, "A");
    }

    @PutMapping("/inactive/{id}")
    public Mono<DidacticUnit> deactivate(@PathVariable String id) {
        return didacticUnitService.changeStatus(id, "I");
    }

    @GetMapping("/{id}")
    public Mono<DidacticUnit> getProgramById(@PathVariable String id) {
        return didacticUnitService.getById(id);
    }

    @GetMapping("/program/{id}")
    public Mono<StudyProgram> getByStudyProgram(@PathVariable String id) {
        return didacticUnitService.getProgramById(id);
    }

    @GetMapping("/program/list")
    public Flux<StudyProgram> testProgramClient() {
        return didacticUnitService.listActive();
    }

    @PostMapping("/{studyProgramId}/units")
    public Flux<DidacticUnit> assignDidacticUnitsToStudyProgram(@PathVariable String studyProgramId, @RequestBody DidacticUnitIdsDto didacticUnitIdsDto) {
        return didacticUnitService.assignDidacticUnitsToStudyProgram(studyProgramId, didacticUnitIdsDto);
    }

    @GetMapping("/program/{studyProgramId}/units")
    public Flux<DidacticUnit> getDidacticUnitsByStudyProgramId(@PathVariable String studyProgramId) {
        return didacticUnitService.getByStudyProgramId(studyProgramId);
    }
}
