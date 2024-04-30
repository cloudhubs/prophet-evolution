package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import lombok.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;

import static edu.university.ecs.lab.common.utils.ObjectToJsonUtils.*;

/**
 * Represents a class in Java. It holds all information regarding that class including all method
 * declarations, method calls, fields, etc.
 */
@Data
@ToString
@Builder
public class JClass implements JsonSerializable {
  /** Name of the class e.g. Food */
  protected String className;

  /** Path like repoName/.../serviceName/.../file.java */
  protected String classPath;

  /** Full java package name of the class e.g. com.cloudhubs.trainticket.food.entity */
  protected String packageName;

  /**
   * Role of the class in the microservice system. See {@link ClassRole} for possibilities. Will
   * match with subtype where applicable
   */
  protected ClassRole classRole;

  /** List of methods in the class */
  protected List<Method> methods;

  /** List of class variables e.g. (private String username;) */
  @SerializedName("variables")
  protected List<Field> fields;

  /** List of method invocations made from within this class e.g. obj.method() */
  protected List<MethodCall> methodCalls;

  /** The associated microservice object for this class */
  protected String msId;

  /** Uniquely identify a class as an object of a given service */
  @SerializedName("id")
  public String getId() {
    return classRole.name() + ":" + msId + "#" + className;
  }

  /**
   * Set the class path of the class. This will replace all "\\" with "/" for readability.
   *
   * @param classPath The class path to set
   */
  public void setClassPath(String classPath) {
    this.classPath = classPath.replaceAll("\\\\", "/");
  }

  /**
   * Constructor for a JClass object.
   *
   * @param className Name of the class
   * @param classPath Path of the class
   * @param packageName Package name of the class
   * @param classRole Role of the class
   * @param methods List of methods in the class
   * @param fields List of fields in the class
   * @param methodCalls List of method calls in the class
   * @param msId The associated microservice id
   */
  public JClass(
      String className,
      String classPath,
      String packageName,
      ClassRole classRole,
      List<Method> methods,
      List<Field> fields,
      List<MethodCall> methodCalls,
      String msId) {
    this.className = className;
    setClassPath(classPath);
    this.packageName = packageName;
    this.classRole = classRole;
    this.methods = methods;
    this.fields = fields;
    this.methodCalls = methodCalls;
    this.msId = msId;
  }

  /**
   * Convert a single JClass to a JsonObject
   *
   * @return Converted JsonObject of JClass object
   */
  @Override
  public JsonObject toJsonObject() {
    return createBuilder().build();
  }

  protected JsonObjectBuilder createBuilder() {
    JsonObjectBuilder jClassBuilder = Json.createObjectBuilder();

    jClassBuilder.add("className", this.className);
    jClassBuilder.add("classPath", this.classPath);
    jClassBuilder.add("packageName", this.packageName);
    jClassBuilder.add("classRole", this.classRole.name());
    jClassBuilder.add("msId", msId);
    jClassBuilder.add("methods", listToJsonArray(methods));
    jClassBuilder.add("variables", listToJsonArray(fields));
    jClassBuilder.add("methodCalls", listToJsonArray(methodCalls));

    return jClassBuilder;
  }

  /**
   * Check if the given class is the same as this class. This is true if they have the same
   * classPath.
   *
   * @param other The class to compare with
   * @return True if the classes are the same, false otherwise
   */
  public boolean matchClassPath(JClass other) {
    return this.getClassPath().equals(other.getClassPath());
  }
}
