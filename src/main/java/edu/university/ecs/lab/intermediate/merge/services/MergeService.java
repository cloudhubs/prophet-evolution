package edu.university.ecs.lab.intermediate.merge.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.PathUtils;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.Delta;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MergeService {
  public String incrementVersion(String version) {
    // split version by '.'
    String[] parts = version.split("\\.");

    // cast version string parts to integer
    int[] versionParts = new int[parts.length];
    for (int i = 0; i < parts.length; i++) {
      versionParts[i] = Integer.parseInt(parts[i]);
    }

    // increment end digit
    versionParts[versionParts.length - 1]++;

    // end digit > 9? increment middle and reset end digit to 0
    if (versionParts[versionParts.length - 1] == 10) {
      versionParts[versionParts.length - 1] = 0;
      versionParts[versionParts.length - 2]++;

      // middle digit > 9, increment start digit (major version) and reset middle to 0
      if (versionParts[versionParts.length - 2] == 10) {
        versionParts[versionParts.length - 2] = 0;
        versionParts[0]++;
      }
    }

    StringBuilder newVersion = new StringBuilder();
    for (int i = 0; i < versionParts.length; i++) {
      newVersion.append(versionParts[i]);
      if (i < versionParts.length - 1) {
        newVersion.append('.');
      }
    }

    return newVersion.toString();
  }

  public Microservice addFiles(ClassRole classRole, String msId, Map<String, Microservice> msModelMap, Delta delta) {
      Microservice msModel;

    if (msModelMap.containsKey(msId)) {
      msModel = msModelMap.get(msId);
    } else {
      msModel = new Microservice();
      msModel.setId(msId);
    }

//    msModel.setCommit(delta.getCommitId());



    if(classRole == ClassRole.SERVICE) {
      updateApiDestinationsAdd(msModelMap, delta.getSChange(), msId);
    }

    switch (classRole) {
      case CONTROLLER:
        msModel.getControllers().add(delta.getCChange());
        break;
      case SERVICE:
        msModel.getServices().add(delta.getSChange());
        break;
      case REPOSITORY:
        msModel.getRepositories().add(delta.getChange());
        break;
      case DTO:
        msModel.getDtos().add(delta.getChange());
        break;
      case ENTITY:
        msModel.getEntities().add(delta.getChange());
        break;
    }

    return msModel;
  }

  public Microservice modifyFiles(ClassRole classRole, String msId, Map<String, Microservice> msModelMap, Delta delta) {
    if (!msModelMap.containsKey(msId)) {
      return null;
    }

    // modification is simply file removal then an add
    removeFiles(classRole, msId, msModelMap, delta);
    return addFiles(classRole, msId, msModelMap, delta);
  }

  public void removeFiles(ClassRole classRole, String msId, Map<String, Microservice> msModelMap, Delta delta) {
    Microservice msModel;

    if (msModelMap.containsKey(msId)) {
      msModel = msModelMap.get(msId);
    } else {
      msModel = new Microservice();
      msModel.setId(msId);
    }

    if(ClassRole.CONTROLLER == classRole) {
      JController controller = msModel.getControllers().stream().filter(jController -> jController.getClassPath().contains(delta.getLocalPath().substring(1))).findFirst().orElse(null);
      if(Objects.nonNull(controller)) {
        updateApiDestinationsDelete(msModelMap, controller, msId);
      }
    }

    switch (classRole) {
      case CONTROLLER:
        msModel.getControllers().removeIf(jController -> jController.getClassPath().contains(delta.getLocalPath().substring(1)));
        break;
      case SERVICE:
        msModel.getServices().removeIf(jService -> jService.getClassPath().contains(delta.getLocalPath().substring(1)));
        break;
      case REPOSITORY:
        msModel.getRepositories().removeIf(repository -> repository.getClassPath().contains(delta.getLocalPath().substring(1)));
        break;
      case DTO:
        msModel.getDtos().removeIf(dto -> dto.getClassPath().contains(delta.getLocalPath().substring(1)));
        break;
      case ENTITY:
        msModel.getEntities().removeIf(entity -> entity.getClassPath().contains(delta.getLocalPath().substring(1)));
        break;
    }

  }

  private static void updateApiDestinationsAdd(Map<String, Microservice> msModelMap, JService service, String servicePath) {
    for(RestCall restCall : service.getRestCalls()) {
      for(Microservice ms : msModelMap.values()) {
        if(!ms.getId().equals(servicePath)) {
          for(JController controller : ms.getControllers()){
            for(Endpoint endpoint : controller.getEndpoints()) {
              if(endpoint.getUrl().equals(restCall.getApi())) {
                restCall.setDestFile(controller.getClassPath());
              }
            }
          }
        }
      }
    }
  }

  private static void updateApiDestinationsDelete(Map<String, Microservice> msModelMap, JController controller, String servicePath) {
    for(Endpoint endpoint : controller.getEndpoints()) {
      for(Microservice ms : msModelMap.values()) {
        if(!ms.getId().equals(servicePath)) {
          for(JService service : ms.getServices()){
            for(RestCall restCall : service.getRestCalls()) {
              if(restCall.getApi().equals(endpoint.getUrl()) && !restCall.getDestFile().equals("")) {
                restCall.setDestFile("");
              }
            }
          }
        }
      }
    }
  }
}
