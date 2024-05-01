package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.impact.models.ClassMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Service to generate metrics pertaining to classes in the system */
public class ClassMetricsService {
  /** System change object containing all changes to the system */
  private final SystemChange systemChange;

  /**
   * Constructor for ClassMetricsService
   *
   * @param systemChange delta of the system
   */
  public ClassMetricsService(SystemChange systemChange) {
    this.systemChange = systemChange;
  }

  /**
   * Generate set of metrics pertaining to modified classes as a whole in the system, organized by
   * {@link ClassRole}
   *
   * @return list of class metrics, one of each class role
   */
  public List<ClassMetrics> generateAllClassMetrics() {
    List<ClassMetrics> classMetricsList = new ArrayList<>();

    classMetricsList.add(
        generateMetricsForRole(ClassRole.CONTROLLER, systemChange.getControllers()));
    classMetricsList.add(generateMetricsForRole(ClassRole.SERVICE, systemChange.getServices()));
    classMetricsList.add(
        generateMetricsForRole(ClassRole.REPOSITORY, systemChange.getRepositories()));
    classMetricsList.add(generateMetricsForRole(ClassRole.DTO, systemChange.getDtos()));
    classMetricsList.add(generateMetricsForRole(ClassRole.ENTITY, systemChange.getEntities()));

    // Generate total metrics
    ClassMetrics totalMetrics = new ClassMetrics();
    totalMetrics.setClassRole(ClassRole.TOTAL);
    totalMetrics.setAddedClassCount(
        classMetricsList.stream().map(ClassMetrics::getAddedClassCount).mapToInt(Integer::intValue).sum());
    totalMetrics.setModifiedClassCount(
        classMetricsList.stream().map(ClassMetrics::getModifiedClassCount).mapToInt(Integer::intValue).sum());
    totalMetrics.setDeletedClassCount(
        classMetricsList.stream().map(ClassMetrics::getDeletedClassCount).mapToInt(Integer::intValue).sum());

    classMetricsList.add(totalMetrics);

    return classMetricsList;
  }

  /**
   * Generate metrics for a specific class role, helper method for {@link
   * #generateAllClassMetrics()}
   *
   * @param classRole role of the class
   * @param changeList list of changes to classes of that role
   * @return metrics for that class role
   */
  private ClassMetrics generateMetricsForRole(ClassRole classRole, Map<String, Delta> changeList) {
    ClassMetrics classMetrics = new ClassMetrics();
    classMetrics.setClassRole(classRole);

    for (Delta delta : changeList.values()) {
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
