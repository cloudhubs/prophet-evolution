package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

/** Represents a method declaration in Java. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Method {
  private String methodName;
  private String protection;

  @SerializedName("parameter")
  private String parameterList;

  private String returnType;
  //  private List<Annotation> annotations;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Method method = (Method) o;
    return Objects.equals(methodName, method.methodName) && Objects.equals(protection, method.protection) && Objects.equals(parameterList, method.parameterList) && Objects.equals(returnType, method.returnType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, protection, parameterList, returnType);
  }
}
