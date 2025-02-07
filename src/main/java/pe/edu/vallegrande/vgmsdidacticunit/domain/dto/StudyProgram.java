package pe.edu.vallegrande.vgmsdidacticunit.domain.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class StudyProgram {

    @Id
    private String programId;
    private String name;
    private String module;
    private String trainingLevel;
    private String studyPlanType;
    private String credits;
    private String hours;
    private String status;
    private String cetproId;

}
