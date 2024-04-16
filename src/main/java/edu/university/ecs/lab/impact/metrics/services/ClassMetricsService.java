package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.impact.models.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class ClassMetricsService {
    public ClassMetricsService() {}

    /**
     * Generate set of metrics pertaining to modified classes as a whole in the system, organized
     * by {@link ClassRole}
     * @return list of class metrics, one of each class role
     */
    public List<ClassMetrics> generateAllClassMetrics(SystemChange systemChange) {
        List<ClassMetrics> classMetricsList = new ArrayList<>();

        classMetricsList.add(generateMetricsForRole(ClassRole.CONTROLLER, systemChange.getControllers()));
        classMetricsList.add(generateMetricsForRole(ClassRole.SERVICE, systemChange.getServices()));
        classMetricsList.add(
                generateMetricsForRole(ClassRole.REPOSITORY, systemChange.getRepositories()));
        classMetricsList.add(generateMetricsForRole(ClassRole.DTO, systemChange.getDtos()));
        classMetricsList.add(generateMetricsForRole(ClassRole.ENTITY, systemChange.getEntities()));

        return classMetricsList;
    }

    /**
     * Generate metrics for a specific class role, helper method for {@link #generateAllClassMetrics(SystemChange)}
     * @param classRole role of the class
     * @param changeList list of changes to classes of that role
     * @return metrics for that class role
     */
    private ClassMetrics generateMetricsForRole(ClassRole classRole, List<Delta> changeList) {
        ClassMetrics classMetrics = new ClassMetrics();
        classMetrics.setClassRole(classRole);

        for (Delta delta : changeList) {
            switch (delta.getChangeType()) {
                case ADD:
                    classMetrics.incrementAddedClassCount();
                    break;
                case MODIFY:
                    classMetrics.incrementModifiedClassCount();
                    break;
                case DELETE:
                    classMetrics.incrementRemovedClassCount();
                    break;
            }
        }
        return classMetrics;
    }
}
