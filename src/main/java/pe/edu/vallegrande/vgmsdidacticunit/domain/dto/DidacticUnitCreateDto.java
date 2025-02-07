package pe.edu.vallegrande.vgmsdidacticunit.domain.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class DidacticUnitCreateDto {

    @Id
    private String didacticId;
    private String name;
    private String credit;
    private String hours;
    private String condition;
    private String correction;
    private String studyProgramId;
    private String status;

}
