package edu.university.ecs.lab.impact.utils;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.common.models.Flow;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.Microservice;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.common.models.MsSystem;
import edu.university.ecs.lab.delta.models.SystemChange;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static edu.university.ecs.lab.common.utils.FlowUtils.buildFlows;

public class EntityDependencyLocator {

  public static void run(SystemChange systemChange, MsSystem msSystem) {
    // If no entities or dto's are modified then entity dependencies are not affected
    if (checkForEntityModification(systemChange)) {
      return;
    }

    for (Delta d : systemChange.getEntities()) {

      Microservice model = msSystem.getServiceMap().get(d.getMsName());

      if (Objects.isNull(model)) {
        throw new RuntimeException("Error");
      }

      List<Flow> flows = buildFlows(model);

      if (d.getChangeType().equals("MODIFY")) {
        // If the object is identical, continue
        if (!validateObjectChange(model, d)) {
          continue;
        }

        identifyImpact(flows, d);
      } else if (d.getChangeType().equals("DELETE")) {
        identifyImpact(flows, d);
      }
    }
  }

  /**
   * @param flows
   * @param delta
   */
  private static void identifyImpact(List<Flow> flows, Delta delta) {
    for (Flow flow : flows) {
      String className =
          delta
              .getLocalPath()
              .substring(
                  delta.getLocalPath().lastIndexOf("\\") + 1,
                  delta.getLocalPath().lastIndexOf(".java"));
      // If a parameter in the controller method (endpoint) contains the classname of deleted entity
      if (flow.getControllerMethod().getParameterList().contains(className)) {
        System.out.println(
            "Here we are deleting a reliant object affecting endpoint url: "
                + ((Endpoint) flow.getControllerMethod()).getUrl());
      }
    }
  }

  /**
   * Validate first that the old object exists in IR and second that the new object differs
   *
   * @param model ms system this change occurred in
   * @param delta delta change
   */
  private static boolean validateObjectChange(Microservice model, Delta delta)
      throws RuntimeException {
    Optional<JClass> oldClass =
        Stream.concat(model.getEntities().stream(), model.getDtos().stream())
            .filter(jClass -> jClass.getClassPath().equals(delta.getLocalPath()))
            .findFirst();

    if (oldClass.isEmpty()) {
      throw new RuntimeException("Shouldn't be possible");
    }

    if (Objects.equals(oldClass.get(), delta.getChange())) {
      return false;
    }

    return true;
  }

  /**
   * Checks if relevant changes are made to either dtos or entities
   *
   * @param systemChange object representing changes to a system
   * @return if relevant changes are present
   */
  private static boolean checkForEntityModification(SystemChange systemChange) {
    return (systemChange.getEntities().isEmpty() && systemChange.getDtos().isEmpty());
  }
}
