package edu.university.ecs.lab.intermediate.merge.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.intermediate.merge.models.Change;
import edu.university.ecs.lab.intermediate.merge.models.Delta;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

  public MsModel addFiles(String msId, Map<String, MsModel> msModelMap, Delta delta) {
    MsModel msModel;

    if (msModelMap.containsKey(msId)) {
      msModel = msModelMap.get(msId);
    } else {
      msModel = new MsModel();
      msModel.setId(msId);
    }

    msModel.setCommit(delta.getCommitId());

    Change change = delta.getChange();

    if (Objects.nonNull(change.getControllers())) {
      msModel.getControllers().addAll(change.getControllers());
    }
    if (Objects.nonNull(change.getControllers())) {
      msModel.getServices().addAll(change.getServices());
    }
    if (Objects.nonNull(change.getControllers())) {
      msModel.getDtos().addAll(change.getDtos());
    }
    if (Objects.nonNull(change.getControllers())) {
      msModel.getRepositories().addAll(change.getRepositories());
    }
    if (Objects.nonNull(change.getControllers())) {
      msModel.getEntities().addAll(change.getEntities());
    }

    return msModel;
  }

  public MsModel modifyFiles(String msId, Map<String, MsModel> msModelMap, Delta delta) {
    if (!msModelMap.containsKey(msId)) {
      return null;
    }

    // modification is simply file removal then an add
    removeFiles(msId, msModelMap, delta);
    return addFiles(msId, msModelMap, delta);
  }

  public void removeFiles(String serviceId, Map<String, MsModel> msModelMap, Delta delta) {
    Change change = delta.getChange();

    findAndRemoveSubClasses(change.getControllers(), msModelMap.get(serviceId).getControllers());
    findAndRemoveSubClasses(change.getServices(), msModelMap.get(serviceId).getServices());
    findAndRemoveClasses(change.getDtos(), msModelMap.get(serviceId).getDtos());
    findAndRemoveClasses(change.getRepositories(), msModelMap.get(serviceId).getRepositories());
    findAndRemoveClasses(change.getEntities(), msModelMap.get(serviceId).getEntities());
  }

  private void findAndRemoveClasses(List<JClass> changeList, List<JClass> classList) {
    for (JClass jClass : changeList) {
      findAndRemoveClass(jClass.getClassName(), classList);
    }
  }

  private void findAndRemoveClass(String className, List<JClass> classList) {
    classList.removeIf(c -> c.getClassName().equals(className));
  }

  private void findAndRemoveSubClasses(
      List<? extends JClass> changeList, List<? extends JClass> classList) {
    for (JClass jClass : changeList) {
      findAndRemoveSubclass(jClass.getClassName(), classList);
    }
  }

  private void findAndRemoveSubclass(String className, List<? extends JClass> serviceList) {
    serviceList.removeIf(c -> c.getClassName().equals(className));
  }
}
