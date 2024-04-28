package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JController extends JClass {

  @SerializedName("restEndpoints")
  private List<Endpoint> endpoints;

  public JController(JClass jClass) {
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

    this.endpoints = new ArrayList<>();
  }
}
