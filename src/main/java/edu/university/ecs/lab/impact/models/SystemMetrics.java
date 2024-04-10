package edu.university.ecs.lab.impact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetrics {

    private int brokenApiDependencies;
    private int newApiDependencies;
    private int affectedEntityDependencies;
    private int newEntityDependencies;
    private int newSystems;
    private int modifiedClasses;

    private List<ClassMetrics> classMetrics;
    private FlowMetrics flowMetrics;
    private DependencyMetrics dependencyMetrics;

    //private double systemCoupling;

}
