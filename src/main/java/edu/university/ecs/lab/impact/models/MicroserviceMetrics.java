package edu.university.ecs.lab.impact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class MicroserviceMetrics {

    private String name;
    private double sidc2Score;
    private double siucScore;
    private double adsScore;
    private DependencyMetrics dependencyMetrics;


}
