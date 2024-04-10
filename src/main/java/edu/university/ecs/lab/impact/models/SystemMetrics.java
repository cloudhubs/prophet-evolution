package edu.university.ecs.lab.impact.models;

import java.util.List;

public class SystemMetrics {

    private int brokenApiDependencies;
    private int newApiDependencies;
    private int affectedEntityDependencies;
    private int newEntityDependencies;
    private int newSystems;
    private int modifiedClasses;

    private List<ClassMetrics> classMetrics;
    private FlowMetrics flowMetrics;

    //private double systemCoupling;

}
