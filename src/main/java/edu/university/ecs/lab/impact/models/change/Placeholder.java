package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Placeholder {
    String filePath;
    String microserviceName;
    ChangeType changeType;
    ClassRole classRole;
    List<CallChange> callChangeList;

}
