package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.common.models.Microservice;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MicroserviceMetrics {

    private String id;
    private double oldSidc2Score;
    private double oldSiucScore;
    private double oldAdsScore;
    private double newSidc2Score;
    private double newSiucScore;
    private double newAdsScore;
    private boolean highCoupling;
    private DependencyMetrics dependencyMetrics;


    public MicroserviceMetrics(Microservice microservice) {
        this.id = microservice.getId();
    }
}
