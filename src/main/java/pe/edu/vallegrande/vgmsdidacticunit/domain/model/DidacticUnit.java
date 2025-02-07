package pe.edu.vallegrande.vgmsdidacticunit.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "didactic_units")
public class DidacticUnit {

    @Id
    private String didacticId;
    private String name;
    private String credit;
    private String hours;
    private String condition;
    private String correction;
    private String status;
    private String studyProgramId;

}
