package edu.university.ecs.lab.impact.models.dependency;

import edu.university.ecs.lab.impact.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiDependency {
    private Status status;
    private String sourceClass;
    private String destClass;
    private String sourceService;
    private String destService;
    private boolean isDirectDependency;
}
