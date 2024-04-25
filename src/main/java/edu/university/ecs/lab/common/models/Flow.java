package edu.university.ecs.lab.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Represents a flow from controller level down to DAO. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Flow {
  private Microservice model;
  private JController controller;
  private Method controllerMethod;
  private MethodCall serviceMethodCall;
  private Field controllerServiceField;
  private JService service;
  private Method serviceMethod;
  private MethodCall repositoryMethodCall;
  private Field serviceRepositoryField;
  private JClass repository;
  private Method repositoryMethod;
}
