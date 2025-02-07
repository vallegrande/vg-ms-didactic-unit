package pe.edu.vallegrande.vgmsdidacticunit.domain.dto;

import lombok.Data;
@Data
public class DidacticUnitUpdateDto {

    private String name;
    private String credit;
    private String hours;
    private String condition;
    private String correction;
    private String studyProgramId;

}
