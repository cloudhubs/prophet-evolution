package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.common.models.Microservice;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

import static edu.university.ecs.lab.impact.metrics.services.MicroserviceMetricsService.*;

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
    private boolean inCycle;
    private DependencyMetrics dependencyMetrics;

    private Microservice oldMicroservice;
    private Microservice newMicroservice;


    public MicroserviceMetrics(Microservice oldMicroservice, Microservice newMicroservice) {
        this.oldMicroservice = oldMicroservice;
        this.newMicroservice = newMicroservice;

        if (oldMicroservice != null && newMicroservice != null) {
            if (!Objects.equals(oldMicroservice.getId(), newMicroservice.getId())) {
                throw new IllegalArgumentException("Old and new microservices must have the same id");
            }
            this.id = oldMicroservice.getId();
        } else if (oldMicroservice != null) {
            // TODO not yet implemented
            this.id = oldMicroservice.getId();
        } else if (newMicroservice != null) {
            // TODO not yet implemented
            this.id = newMicroservice.getId();
        } else {
            throw new IllegalArgumentException("Both old and new microservices cannot be null");
        }

        generateNumericMetrics();
    }

    private void generateNumericMetrics() {
        this.oldAdsScore = oldMicroservice == null ? 0 : calculateADS(oldMicroservice);
        this.newAdsScore = newMicroservice == null ? 0 : calculateADS(newMicroservice);
        this.oldSidc2Score = oldMicroservice == null ? 0 : calculateSIDC2Score(oldMicroservice);
        this.newSidc2Score = oldMicroservice == null ? 0 : calculateSIDC2Score(newMicroservice);

        // TODO this probably needs to be changed, we dont only want to check old service. Also should write threshold/how much it exceeds by
        // this.highCoupling = oldAdsScore > THRESHOLD;

        // TODO causing infinite loop rn
        //microserviceMetrics.setInCycle(callChangeService.isInCycle(microservice));
    }

    public void generateSiucMetrics(Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap) {
        this.oldSiucScore = oldMicroservice == null ? 0 : calculateSIUCScore(oldMicroserviceMap, oldMicroservice);
        this.newSidc2Score = newMicroservice == null ? 0 : calculateSIUCScore(newMicroserviceMap, newMicroservice);
    }
}
