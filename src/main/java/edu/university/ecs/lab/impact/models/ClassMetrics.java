package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassMetrics {

    private ClassRole classRole;

    private int classCount;

    private int newClassCount;

    private int removedClassCount;

    private int modifiedClassCount;

}
