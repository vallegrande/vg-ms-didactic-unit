package pe.edu.vallegrande.vgmsdidacticunit.domain.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.vgmsdidacticunit.domain.model.DidacticUnit;
import reactor.core.publisher.Flux;

public interface DidacticUnitRepository extends ReactiveMongoRepository<DidacticUnit , String> {
    Flux<DidacticUnit> findByStatus(String status);
    Flux<DidacticUnit> findByDidacticIdAndStatus(String didacticId, String status);
    Flux<DidacticUnit> findByStudyProgramIdAndStatus(String studyProgramId, String status);

}
