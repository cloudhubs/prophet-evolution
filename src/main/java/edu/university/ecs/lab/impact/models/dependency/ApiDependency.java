package edu.university.ecs.lab.impact.models.dependency;

import edu.university.ecs.lab.impact.models.enums.Status;

public class ApiDependency {
    private Status status;
    private String sourceClass;
    private String destClass;
    private String sourceService;
    private String destService;
    private boolean isDirectDependency;
}
