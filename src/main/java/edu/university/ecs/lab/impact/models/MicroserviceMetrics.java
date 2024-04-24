package edu.university.ecs.lab.impact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MicroserviceMetrics {

    private double sidc2Score;
    private double siucScore;
    private double adsScore;

}
