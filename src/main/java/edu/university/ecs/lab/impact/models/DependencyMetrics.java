package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.impact.models.dependency.ApiDependency;
import edu.university.ecs.lab.impact.models.dependency.EntityDependency;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DependencyMetrics {
    List<ApiDependency> apiDependencyList;
    List<EntityDependency> entityDependencyList;

}
