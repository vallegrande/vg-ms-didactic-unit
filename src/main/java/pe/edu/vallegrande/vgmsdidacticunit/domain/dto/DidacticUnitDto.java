package pe.edu.vallegrande.vgmsdidacticunit.domain.dto;

import lombok.Data;

@Data
public class DidacticUnitDto {

    private String didacticId;
    private String name;
    private String credit;
    private String hours;
    private String condition;
    private String correction;
    private String status;
    private StudyProgram studyProgramId;

}
