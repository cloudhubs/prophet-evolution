package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import lombok.*;

import java.util.List;

/**
 * Represents a class in Java. It holds all information regarding that class including all method
 * declarations, method calls, fields, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JClass {
  protected String className;
  protected String classPath;
  protected String packageName;
  protected ClassRole classRole;
  protected List<Method> methods;

  @SerializedName("variables")
  protected List<Field> fields;

  protected List<MethodCall> methodCalls;

  /** The associated microservice object for this class */
  protected String msId;

  /** Uniquely identify a class as an object of a given service */
  @SerializedName("id")
  public String getId() {
    return classRole.name() + ":" + msId + "#" + className;
  }

}
