package edu.university.ecs.lab.delta.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.models.JsonSerializable;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.ObjectToJsonUtils;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.diff.DiffEntry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Getter
@Setter
@AllArgsConstructor
public class Delta implements JsonSerializable {
  /** Relative path to the changed file. This DIFFERS from the jClass path as the jClass path starts at the repoName */
  private String localPath;

  private ChangeType changeType;
  private String commitId;
  private String msId;

  @SerializedName("changes")
  private JClass changedClass;

  /**
   * @return the microservice id of the changed class
   */
  public String getMsId() {
    return changedClass.getMsId();
  }

  /**
   * @param msId the microservice id of the changed class
   */
  public void setMsId(String msId) {
    changedClass.setMsId(msId);
    this.msId = msId;
  }

  /**
   * Get the class role of the changed class
   * @return the class role of the changed class
   */
  public ClassRole getClassRole() {
    return changedClass.getClassRole();
  }


  /**
   *
   * @param entry diff entry from git
   * @param jClass class extracted from the CHANGED file
   * @param localPath relative path to the service (ex: ./clonePath/repoName/service/path/to/file.java)
   */
  public Delta(JClass jClass, DiffEntry entry, String localPath) {
    // TODO Validate local path
    setLocalPath(localPath);
    setChangeType(ChangeType.fromDiffEntry(entry));
    setCommitId(entry.getNewId().name());
    setChangedClass(jClass);
    setMsId(jClass.getMsId());
  }

  /**
   * Converts the delta object to a JSON object
   *
   * @return the JSON object representation of the delta
   * @apiNote This will not include the localPath field in the JSON object for readability
   */
  public JsonObject toJsonObject() {
    JsonObjectBuilder jout = Json.createObjectBuilder();

    jout.add("changeType", changeType.name());
    jout.add("commitId", commitId);
    jout.add("msName", msId);

    JsonObject changeClassObj;
    if (changedClass.getClassRole() == ClassRole.CONTROLLER) {
      changeClassObj = ObjectToJsonUtils.buildRestController(msId, (JController) changedClass);
    } else if (changedClass.getClassRole() == ClassRole.SERVICE) {
      changeClassObj = ObjectToJsonUtils.buildRestService((JService) changedClass);
    } else {
      changeClassObj = ObjectToJsonUtils.buildJavaClass(changedClass);
    }

    jout.add("changes", changeClassObj);
    return jout.build();
  }

}
