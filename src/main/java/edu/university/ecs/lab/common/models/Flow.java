package edu.university.ecs.lab.common.models;

import lombok.*;

/** Represents a flow from controller level down to DAO. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Flow {
  private Microservice model;
  private JClass controller;
  private Method controllerMethod;
  private MethodCall serviceMethodCall;
  private Field controllerServiceField;
  private JClass service;
  private Method serviceMethod;
  private MethodCall repositoryMethodCall;
  private Field serviceRepositoryField;
  private JClass repository;
  private Method repositoryMethod;
}
