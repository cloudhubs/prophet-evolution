package edu.university.ecs.lab.impact.models.dependency;

import edu.university.ecs.lab.impact.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDependency {
    private Status status;
    private String service;
    private String entityClass;
    private String endpointClass;

}
