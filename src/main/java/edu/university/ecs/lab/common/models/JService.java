package edu.university.ecs.lab.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JService extends JClass {
  private List<RestCall> restCalls;

  public JService(@NonNull JClass jClass) {
    super(
        jClass.getClassName(),
        jClass.getClassPath(),
        jClass.getPackageName(),
        jClass.getClassRole(),
        jClass.getMethods(),
        jClass.getFields(),
        jClass.getMethodCalls(),
        jClass.getMsId()
    );
    this.restCalls = new ArrayList<>();
  }
}
