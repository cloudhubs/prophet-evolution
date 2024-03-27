package edu.university.ecs.lab.deltas.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.models.MsModel;
import edu.university.ecs.lab.common.utils.MsFileUtils;
import edu.university.ecs.lab.deltas.models.ChangeInformation;
import edu.university.ecs.lab.intermediate.create.services.RestModelService;
import org.eclipse.jgit.diff.DiffEntry;

import javax.json.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static edu.university.ecs.lab.intermediate.create.services.RestModelService.scanFile;

/** Utility class for comparing differences between two files. */
public class DeltaComparisonUtils {

  /**
   * Get the difference between two lines in string representation.
   *
   * @param line1 original line
   * @param line2 new line
   * @return the difference between the two lines
   */
  private static String getExactDifference(String line1, String line2) {
    int minLength = Math.min(line1.length(), line2.length());
    int diffIndex = -1;

    for (int i = 0; i < minLength; i++) {
      if (line1.charAt(i) != line2.charAt(i)) {
        diffIndex = i;
        break;
      }
    }

    if (diffIndex == -1) {
      if (line1.length() != line2.length()) {
        return "Lengths differ";
      } else {
        return "Unknown difference";
      }
    } else {
      return "'" + line1.substring(diffIndex) + "' vs '" + line2.substring(diffIndex) + "'";
    }
  }

  /**
   * Extract the differences between the decoded file from {@link
   * GitFetchUtils#fetchAndDecodeFile(String)} and the local file (serviceTLD/{@link
   * DiffEntry#getOldPath()}).
   *
   * @param pathToLocal the path to the local file (serviceTLD/{@link DiffEntry#getOldPath()})
   * @return the differences between the two files as a JSON array
   * @throws IOException if an I/O error occurs
   */
  public JsonObject extractDeltaChanges(String pathToLocal) {
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

    File localFile = new File(pathToLocal);

    List<JController> controllers = new ArrayList<>();
    List<JService> services = new ArrayList<>();
    List<JClass> dtos = new ArrayList<>();
    List<JClass> repositories = new ArrayList<>();
    List<JClass> entities = new ArrayList<>();

    scanFile(localFile, controllers, services, dtos, repositories, entities);

    jsonObjectBuilder.add("controllers", MsFileUtils.buildRestControllers("", controllers));
    jsonObjectBuilder.add("services", MsFileUtils.buildRestServices(services));
    jsonObjectBuilder.add("dtos", MsFileUtils.buildJavaClass(dtos));
    jsonObjectBuilder.add("repositories", MsFileUtils.buildJavaClass(repositories));
    jsonObjectBuilder.add("entities", MsFileUtils.buildJavaClass(entities));

    return jsonObjectBuilder.build();
  }

  /**
   * Recursively determine if local statement contains or is equal to target (change)
   *
   * @param localStatement method statement potentially encompassing change
   * @param changeStatement changed statement to search for
   * @return true if provided local statement contains the change, false otherwise
   */
  private static boolean containsStatement(Statement localStatement, Statement changeStatement) {
    if (localStatement == null || changeStatement == null) {
      return false;
    }

    // check equality of current statement point
    if (StatementEqualityUtils.checkEquality(localStatement, changeStatement)) {
      return true;
    }

    for (Node child : localStatement.getChildNodes()) {
      // skip non-statement nodes (like comments and blank lines?)
      if (!(child instanceof Statement)) {
        continue;
      }

      // recursively check child statements
      if (containsStatement((Statement) child, changeStatement)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Extract changed lines between the remote and local file
   *
   * @param remoteFile decoded file
   * @param localFile local file object
   * @return list of local file changed lines
   * @throws IOException if an I/O error occurs
   */
  private List<ChangeInformation> findChangedLines(String[] remoteFile, File localFile)
      throws IOException {
    List<ChangeInformation> changedLines = new ArrayList<>();

    BufferedReader reader = new BufferedReader(new FileReader(localFile));
    String line;
    int i = 0;

    while ((line = reader.readLine()) != null) {
      // record each line-by-line difference
      if (i < remoteFile.length && !line.equals(remoteFile[i])) {
        changedLines.add(new ChangeInformation(line, remoteFile[i], i + 1));
      }

      i++;
    }

    return changedLines;
  }

  public static JClass extractFileClassModel(File localFile) {
    return scanFile(localFile);
  }

  public JsonObject extractDeltaAdditionChanges(Map<String, MsModel> currentModelMap, String repoPath, String pathToLocal) {
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

    // Set all empty initially
    jsonObjectBuilder.add("controllers", JsonValue.EMPTY_JSON_ARRAY);
    jsonObjectBuilder.add("services", JsonValue.EMPTY_JSON_ARRAY);
    jsonObjectBuilder.add("dtos", JsonValue.EMPTY_JSON_ARRAY);
    jsonObjectBuilder.add("repositories", JsonValue.EMPTY_JSON_ARRAY);
    jsonObjectBuilder.add("entities", JsonValue.EMPTY_JSON_ARRAY);


    File localFile = new File(pathToLocal);

    JClass jClass = extractFileClassModel(localFile);

    Optional<MsModel> model = currentModelMap.values().stream().filter(msModel -> msModel.getMsPath().equals(repoPath)).findFirst();

    // If the model can't be found
    if(model.isEmpty()) {
      return jsonObjectBuilder.build();
    }

    if(jClass instanceof JController) {
      Optional<JController> controller = model.get().getControllers().stream().filter(jController -> jController.getClassName().equals(jClass.getClassName())).findFirst();

      // We have a new class? Add it all...
      if(controller.isEmpty()) {
        jsonObjectBuilder.add("controllers", MsFileUtils.buildRestControllers("", List.of((JController) jClass)));
      } else {
        // We parse what specifically changed
        JController controllerChanges = (JController) extractClassSpecificChanges(jClass, controller.get());
        jsonObjectBuilder.add("controllers", MsFileUtils.buildRestControllers("", List.of(controllerChanges)));
      }

    } else if (jClass instanceof JService) {
      Optional<JService> service = model.get().getServices().stream().filter(jService -> jService.getClassName().equals(jClass.getClassName())).findFirst();


      // We have a new class? Add it all...
      if(service.isEmpty()) {
        jsonObjectBuilder.add("services", MsFileUtils.buildRestServices(List.of((JService) jClass)));
      } else {
        // We parse what specifically changed
        JService serviceChanges = (JService) extractClassSpecificChanges(jClass, service.get());
        jsonObjectBuilder.add("services", MsFileUtils.buildRestServices(List.of(serviceChanges)));
      }
    } else {
      // Search remaining class types for a match
      Optional<JClass> dto = model.get().getDtos().stream().filter(jMatch -> jMatch.getClassName().equals(jClass.getClassName())).findFirst();
      Optional<JClass> repository = model.get().getRepositories().stream().filter(jMatch -> jMatch.getClassName().equals(jClass.getClassName())).findFirst();
      Optional<JClass> entity = model.get().getEntities().stream().filter(jMatch -> jMatch.getClassName().equals(jClass.getClassName())).findFirst();

      if(dto.isEmpty() && repository.isEmpty() && entity.isEmpty()) {
        // We have a new class add it all but to where? Rely on class name for now
        if(jClass.getClassPath().contains("entity")) {
          jsonObjectBuilder.add("entities", MsFileUtils.buildJavaClass(List.of(jClass)));
        } else if(jClass.getClassName().contains("Dto")) {
          jsonObjectBuilder.add("dtos", MsFileUtils.buildJavaClass(List.of(jClass)));
        } else if(jClass.getClassName().contains("Repository")) {
          jsonObjectBuilder.add("repositories", MsFileUtils.buildJavaClass(List.of(jClass)));
        }

      } else if(dto.isPresent()) {
        JClass classChanges = extractClassSpecificChanges(jClass, dto.get());
        jsonObjectBuilder.add("dtos", MsFileUtils.buildJavaClass(List.of(classChanges)));
      } else if(repository.isPresent()) {
        JClass classChanges = extractClassSpecificChanges(jClass, dto.get());
        jsonObjectBuilder.add("repositories", MsFileUtils.buildJavaClass(List.of(classChanges)));
      } else {
        JClass classChanges = extractClassSpecificChanges(jClass, dto.get());
        jsonObjectBuilder.add("entities", MsFileUtils.buildJavaClass(List.of(classChanges)));
      }

    }

    return jsonObjectBuilder.build();
  }


  public static JClass extractClassSpecificChanges(JClass jClass1, JClass jClass2) {
    if(jClass1.getClass() != jClass2.getClass()) {
      throw new RuntimeException("Classes must be of same type");
    }
    JClass returnClass = new JClass();
    returnClass.setClassName(jClass1.getClassName());
    returnClass.setPackageName(jClass1.getPackageName());
    returnClass.setClassPath(jClass1.getClassPath());

    returnClass.setFields(extractListSpecificChanges(jClass1.getFields(), jClass2.getFields()));
    returnClass.setMethods(extractListSpecificChanges(jClass1.getMethods(), jClass2.getMethods()));
    returnClass.setMethodCalls(extractListSpecificChanges(jClass1.getMethodCalls(), jClass2.getMethodCalls()));

    if(jClass1 instanceof JController && jClass2 instanceof JController) {
      JController controller = (JController) returnClass;
      controller.setEndpoints(extractListSpecificChanges(((JController)jClass1).getEndpoints(), ((JController)jClass2).getEndpoints()));
      return controller;
    } else if(jClass1 instanceof JService && jClass2 instanceof JService) {
      JService service = (JService) returnClass;
      service.setRestCalls(extractListSpecificChanges(((JService)jClass1).getRestCalls(), ((JService)jClass2).getRestCalls()));
      return service;
    }


    return returnClass;
  }

  public static <T> List<T> extractListSpecificChanges(List<T> currentModels, List<T> newModels) {
    boolean hasMatch = false;
    T matchModel;
    List<T> uniqueModels = new ArrayList<>();

    for(T newModel : newModels) {
      for(T currModel : currentModels) {
        if(Objects.equals(currModel, newModel)) {
          hasMatch = true;
          break;
        }
      }

      if(hasMatch) {
        hasMatch = false;
      } else {
        uniqueModels.add(newModel);
      }
    }

    return uniqueModels;
  }

}
