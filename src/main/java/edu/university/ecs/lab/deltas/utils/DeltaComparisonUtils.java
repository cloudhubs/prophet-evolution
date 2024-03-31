package edu.university.ecs.lab.deltas.utils;

import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.models.MsModel;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.JsonConvertUtils;
import org.eclipse.jgit.diff.DiffEntry;

import javax.json.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static edu.university.ecs.lab.intermediate.create.services.RestModelService.scanFile;
import static edu.university.ecs.lab.intermediate.create.services.RestModelService.scanFileForClassModel;

/** Utility class for comparing differences between two files. */
public class DeltaComparisonUtils {

  /**
   * Extract the differences between the decoded file from {@link
   * GitFetchUtils#fetchAndDecodeFile(String)} and the local file (serviceTLD/{@link
   * DiffEntry#getOldPath()}).
   *
   * @param pathToLocal the path to the local file (serviceTLD/{@link DiffEntry#getOldPath()})
   * @return the differences between the two files as a JSON array
   * @throws IOException if an I/O error occurs
   */
  public static JsonObject extractDeltaChanges(File classFile, ClassRole classRole) {
    JClass jClass = scanFileForClassModel(classFile);

    if (classRole == ClassRole.CONTROLLER && jClass instanceof JController) {
      return JsonConvertUtils.buildRestController("", (JController) jClass);
    } else if (classRole == ClassRole.SERVICE && jClass instanceof JService) {
      return JsonConvertUtils.buildRestService((JService) jClass);
    }

    return JsonConvertUtils.buildJavaClass(jClass);
  }

//  public static JClass extractFileClassModel(File localFile) {
//    return scanFile(localFile);
//  }

//  public JsonObject extractDeltaAdditionChanges(Map<String, MsModel> currentModelMap, String repoPath, String pathToLocal) {
//    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
//
//    // Set all empty initially
//    jsonObjectBuilder.add("controllers", JsonValue.EMPTY_JSON_ARRAY);
//    jsonObjectBuilder.add("services", JsonValue.EMPTY_JSON_ARRAY);
//    jsonObjectBuilder.add("dtos", JsonValue.EMPTY_JSON_ARRAY);
//    jsonObjectBuilder.add("repositories", JsonValue.EMPTY_JSON_ARRAY);
//    jsonObjectBuilder.add("entities", JsonValue.EMPTY_JSON_ARRAY);
//
//
//    File localFile = new File(pathToLocal);
//
//    JClass jClass = extractFileClassModel(localFile);
//
//    Optional<MsModel> model = currentModelMap.values().stream().filter(msModel -> msModel.getMsPath().equals(repoPath)).findFirst();
//
//    // If the model can't be found
//    if(model.isEmpty()) {
//      return jsonObjectBuilder.build();
//    }
//
//    if(jClass instanceof JController) {
//      Optional<JController> controller = model.get().getControllers().stream().filter(jController -> jController.getClassName().equals(jClass.getClassName())).findFirst();
//
//      // We have a new class? Add it all...
//      if(controller.isEmpty()) {
//        jsonObjectBuilder.add("controllers", JsonConvertUtils.buildRestControllers("", List.of((JController) jClass)));
//      } else {
//        // We parse what specifically changed
//        JController controllerChanges = (JController) extractClassSpecificChanges(jClass, controller.get());
//        jsonObjectBuilder.add("controllers", JsonConvertUtils.buildRestControllers("", List.of(controllerChanges)));
//      }
//
//    } else if (jClass instanceof JService) {
//      Optional<JService> service = model.get().getServices().stream().filter(jService -> jService.getClassName().equals(jClass.getClassName())).findFirst();
//
//
//      // We have a new class? Add it all...
//      if(service.isEmpty()) {
//        jsonObjectBuilder.add("services", JsonConvertUtils.buildRestServices(List.of((JService) jClass)));
//      } else {
//        // We parse what specifically changed
//        JService serviceChanges = (JService) extractClassSpecificChanges(jClass, service.get());
//        jsonObjectBuilder.add("services", JsonConvertUtils.buildRestServices(List.of(serviceChanges)));
//      }
//    } else {
//      // Search remaining class types for a match
//      Optional<JClass> dto = model.get().getDtos().stream().filter(jMatch -> jMatch.getClassName().equals(jClass.getClassName())).findFirst();
//      Optional<JClass> repository = model.get().getRepositories().stream().filter(jMatch -> jMatch.getClassName().equals(jClass.getClassName())).findFirst();
//      Optional<JClass> entity = model.get().getEntities().stream().filter(jMatch -> jMatch.getClassName().equals(jClass.getClassName())).findFirst();
//
//      if(dto.isEmpty() && repository.isEmpty() && entity.isEmpty()) {
//        // We have a new class add it all but to where? Rely on class name for now
//        if(jClass.getClassPath().contains("entity")) {
//          jsonObjectBuilder.add("entities", JsonConvertUtils.buildJavaClass(List.of(jClass)));
//        } else if(jClass.getClassName().contains("Dto")) {
//          jsonObjectBuilder.add("dtos", JsonConvertUtils.buildJavaClass(List.of(jClass)));
//        } else if(jClass.getClassName().contains("Repository")) {
//          jsonObjectBuilder.add("repositories", JsonConvertUtils.buildJavaClass(List.of(jClass)));
//        }
//
//      } else if(dto.isPresent()) {
//        JClass classChanges = extractClassSpecificChanges(jClass, dto.get());
//        jsonObjectBuilder.add("dtos", JsonConvertUtils.buildJavaClass(List.of(classChanges)));
//      } else if(repository.isPresent()) {
//        JClass classChanges = extractClassSpecificChanges(jClass, dto.get());
//        jsonObjectBuilder.add("repositories", JsonConvertUtils.buildJavaClass(List.of(classChanges)));
//      } else {
//        JClass classChanges = extractClassSpecificChanges(jClass, dto.get());
//        jsonObjectBuilder.add("entities", JsonConvertUtils.buildJavaClass(List.of(classChanges)));
//      }
//
//    }
//
//    return jsonObjectBuilder.build();
//  }
//
//
//  public static JClass extractClassSpecificChanges(JClass jClass1, JClass jClass2) {
//    if(jClass1.getClass() != jClass2.getClass()) {
//      throw new RuntimeException("Classes must be of same type");
//    }
//    JClass returnClass = new JClass();
//    returnClass.setClassName(jClass1.getClassName());
//    returnClass.setPackageName(jClass1.getPackageName());
//    returnClass.setClassPath(jClass1.getClassPath());
//
//    returnClass.setFields(extractListSpecificChanges(jClass1.getFields(), jClass2.getFields()));
//    returnClass.setMethods(extractListSpecificChanges(jClass1.getMethods(), jClass2.getMethods()));
//    returnClass.setMethodCalls(extractListSpecificChanges(jClass1.getMethodCalls(), jClass2.getMethodCalls()));
//
//    if(jClass1 instanceof JController && jClass2 instanceof JController) {
//      JController controller = (JController) returnClass;
//      controller.setEndpoints(extractListSpecificChanges(((JController)jClass1).getEndpoints(), ((JController)jClass2).getEndpoints()));
//      return controller;
//    } else if(jClass1 instanceof JService && jClass2 instanceof JService) {
//      JService service = (JService) returnClass;
//      service.setRestCalls(extractListSpecificChanges(((JService)jClass1).getRestCalls(), ((JService)jClass2).getRestCalls()));
//      return service;
//    }
//
//
//    return returnClass;
//  }

//  public static <T> List<T> extractListSpecificChanges(List<T> currentModels, List<T> newModels) {
//    boolean hasMatch = false;
//    T matchModel;
//    List<T> uniqueModels = new ArrayList<>();
//
//    for(T newModel : newModels) {
//      for(T currModel : currentModels) {
//        if(Objects.equals(currModel, newModel)) {
//          hasMatch = true;
//          break;
//        }
//      }
//
//      if(hasMatch) {
//        hasMatch = false;
//      } else {
//        uniqueModels.add(newModel);
//      }
//    }
//
//    return uniqueModels;
//  }

}
