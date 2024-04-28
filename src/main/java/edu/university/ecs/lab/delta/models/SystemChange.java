package edu.university.ecs.lab.delta.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JsonSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.jgit.diff.DiffEntry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static edu.university.ecs.lab.common.utils.JsonToObjectUtils.convertListToJsonArray;

@Getter
@Setter
@AllArgsConstructor
public class SystemChange implements JsonSerializable {
  @SerializedName("controllers")
  private Map<String, Delta> controllers;

  @SerializedName("services")
  private Map<String, Delta> services;

  @SerializedName("dtos")
  private Map<String, Delta> dtos;

  @SerializedName("repositories")
  private Map<String, Delta> repositories;

  @SerializedName("entities")
  private Map<String, Delta> entities;

  /**
   * Default constructor for the system change. Initializes all maps to empty.
   */
  public SystemChange() {
    controllers = new HashMap<>();
    services = new HashMap<>();
    dtos = new HashMap<>();
    repositories = new HashMap<>();
    entities = new HashMap<>();
  }

  /**
   * Creates a delta from the given class and entry and adds it to the appropriate map. If the class path
   * already exists in the given map, then the entry is replaced with the new entry. The delta created is returned, null
   * if the change was skipped due to unknown class type.
   * @param jClass class extracted from the CHANGED file
   * @param entry diff entry from git
   * @param localPath path to the class file as repoName/service/path/to/file.java (local path would be ./clonePath/repoName/service/path/to/file.java)
   * @return the delta created
   */
  public Delta addChange(JClass jClass, DiffEntry entry, String localPath) {
    // Switch through each class role and mark the change
    Delta newDelta = new Delta(jClass, entry, localPath);
    switch (Objects.requireNonNull(jClass).getClassRole()) {
        case CONTROLLER:
          controllers.put(localPath, newDelta);
          break;
        case SERVICE:
          services.put(localPath, newDelta);
          break;
        case DTO:
          dtos.put(localPath, newDelta);
          break;
        case REPOSITORY:
          repositories.put(localPath, newDelta);
          break;
        case ENTITY:
          entities.put(localPath, newDelta);
          break;
        default:
          System.out.println("Skipping change: " + entry.getChangeType() + localPath);
          break;
      }

      return newDelta;
  }

  public JsonObject toJsonObject() {
    JsonObjectBuilder finalOutputBuilder = Json.createObjectBuilder();
    finalOutputBuilder.add("controllers", convertListToJsonArray(controllers.values().stream().map(Delta::toJsonObject).collect(Collectors.toList())));
    finalOutputBuilder.add("services", convertListToJsonArray(services.values().stream().map(Delta::toJsonObject).collect(Collectors.toList())));
    finalOutputBuilder.add("repositories", convertListToJsonArray(repositories.values().stream().map(Delta::toJsonObject).collect(Collectors.toList())));
    finalOutputBuilder.add("dtos", convertListToJsonArray(dtos.values().stream().map(Delta::toJsonObject).collect(Collectors.toList())));
    finalOutputBuilder.add("entities", convertListToJsonArray(entities.values().stream().map(Delta::toJsonObject).collect(Collectors.toList())));
    return finalOutputBuilder.build();
  }
}
