package edu.university.ecs.lab.impact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class MicroserviceMetrics {

    private String name;
    private double oldSidc2Score;
    private double oldSiucScore;
    private double oldAdsScore;
    private double newSidc2Score;
    private double newSiucScore;
    private double newAdsScore;
    private boolean highCoupling;
    private DependencyMetrics dependencyMetrics;


}
