package pe.edu.vallegrande.vgmsdidacticunit.application.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitCreateDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitIdsDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitUpdateDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.StudyProgram;
import pe.edu.vallegrande.vgmsdidacticunit.domain.model.DidacticUnit;
import pe.edu.vallegrande.vgmsdidacticunit.domain.repository.DidacticUnitRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DidacticUnitService {

    private final DidacticUnitRepository didacticUnitRepository;
    private final ExternalService externalService;
    private final ModelMapper modelMapper = new ModelMapper();

    public DidacticUnitService(DidacticUnitRepository didacticUnitRepository, ExternalService externalService) {
        this.didacticUnitRepository = didacticUnitRepository;
        this.externalService = externalService;
    }

    public Flux<DidacticUnitDto> getByStatus(String status) {
        log.info("LISTADO DE UNIDADES DIDACTICAS POR ESTADO: {}", status);
        return didacticUnitRepository.findByStatus(status)
                .flatMap(this::converTo)
                .collectList()
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<DidacticUnit> getById(String id) {
        log.info("OBTENER UNIDAD DIDACTICA POR ID: {}", id);
        return didacticUnitRepository.findById(id);
    }

    public Mono<DidacticUnit> create(DidacticUnitCreateDto didacticUnitCreateDto) {
        didacticUnitCreateDto.setStatus("A");
        DidacticUnit didacticUnit = modelMapper.map(didacticUnitCreateDto, DidacticUnit.class);
        log.info("CREACION DE UNIDAD DIDACTICA: {}", didacticUnit);
        return didacticUnitRepository.save(didacticUnit);
    }

    
    public Mono<DidacticUnit> update(String id, DidacticUnitUpdateDto didacticUnitUpdateDto) {
        return didacticUnitRepository.findByDidacticIdAndStatus(id, "A")
                .next()
                .flatMap(sp -> {
                    modelMapper.map(didacticUnitUpdateDto, sp);
                    sp.setDidacticId(id); 
                    log.info("ACTUALIZACION DE UNIDAD DIDACTICA: {}", sp);
                    return didacticUnitRepository.save(sp);
                });
    }

    
    public Mono<DidacticUnit> changeStatus(String id, String status) {
        return didacticUnitRepository.findById(id)
                .flatMap(sp -> {
                    sp.setStatus(status);
                    log.info("CAMBIO DE ESTADO DE UNIDAD DIDACTICA: {}", sp);
                    return didacticUnitRepository.save(sp);
                });
    }
    
    public Mono<StudyProgram> getProgramById(String id) {
        log.info("OBTENER PROGRAMA DE ESTUDIO POR ID: {}", id);
        return externalService.getByIdStudyProgram(id);
    }

    
    public Flux<StudyProgram> listActive() {
        log.info("LISTADO DE PROGRAMAS DE ESTUDIO ACTIVOS");
        return externalService.getStudyProgram();
    }

    
    public Flux<DidacticUnit> assignDidacticUnitsToStudyProgram(String studyProgramId, DidacticUnitIdsDto didacticUnitIdsDto) {
        return Flux.fromIterable(didacticUnitIdsDto.getDidacticUnitIds())
                .flatMap(didacticUnitId -> didacticUnitRepository.findById(didacticUnitId)
                        .flatMap(didacticUnit -> {
                            didacticUnit.setStudyProgramId(studyProgramId);
                            didacticUnit.setStatus("A");
                            log.info("ASIGNACION DE UNIDAD DIDACTICA A PROGRAMA DE ESTUDIO: {}", didacticUnit);
                            return didacticUnitRepository.save(didacticUnit);
                        }));
    }

   
    public Flux<DidacticUnit> getByStudyProgramId(String studyProgramId) {
        log.info("LISTADO DE UNIDADES DIDACTICAS POR PROGRAMA DE ESTUDIO: {}", studyProgramId);
        return didacticUnitRepository.findByStudyProgramIdAndStatus(studyProgramId, "A");
    }

    private Mono<DidacticUnitDto> converTo(DidacticUnit didacticUnit) {
        DidacticUnitDto dto = new DidacticUnitDto();
        dto.setDidacticId(didacticUnit.getDidacticId());
        dto.setName(didacticUnit.getName());
        dto.setCredit(didacticUnit.getCredit());
        dto.setHours(didacticUnit.getHours());
        dto.setCondition(didacticUnit.getCondition());
        dto.setCorrection(didacticUnit.getCorrection());
        dto.setStatus(didacticUnit.getStatus());

        Mono<StudyProgram> studyProgram = externalService.getByIdStudyProgram(didacticUnit.getStudyProgramId());
        
        return Mono.zip(studyProgram , Mono.just(dto))
                .map(tuple -> {

                    StudyProgram studyPrograms = tuple.getT1();

                    dto.setStudyProgramId(studyPrograms);
                    return dto;
                });
    }
}
