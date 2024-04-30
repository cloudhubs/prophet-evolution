package edu.university.ecs.lab.common.models;

import lombok.*;

import java.util.Objects;

/** Represents a method call in Java. */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MethodCall {
  protected String methodName;
  // TODO Rename this? Represents if the called method object e.g. test.test()
  protected String calledFieldName;
  protected String parentMethod;

}
