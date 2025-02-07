package pe.edu.vallegrande.vgmsdidacticunit.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.StudyProgram;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ExternalService {

    @Value("${services.study-program.url}")
    private String studyProgramUrl;

    private final WebClient.Builder webClientBuilder;
    
    public ExternalService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<StudyProgram> getByIdStudyProgram(String programId) {
        return fetchData(studyProgramUrl + "/", 
                    programId, StudyProgram.class);
    }

    public Flux<StudyProgram> getStudyProgram() {
        return fetchDataList(studyProgramUrl + "/list/active", 
                StudyProgram.class);
    }


    private <T> Mono<T> fetchData(String baseUrl, String id, Class<T> responseType) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + id)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(e -> {
                    log.error("Error fetching data: ", e);
                    return Mono.empty();
                });
    }

    private <T> Flux<T> fetchDataList(String baseUrl, Class<T> responseType) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl) 
                .retrieve()
                .bodyToFlux(responseType) 
                .onErrorResume(e -> {
                    log.error("Error fetching data: ", e);
                    return Flux.empty(); 
                });
    }

}
